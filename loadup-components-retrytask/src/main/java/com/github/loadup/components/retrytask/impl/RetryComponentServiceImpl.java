package com.github.loadup.components.retrytask.impl;

/*-
 * #%L
 * loadup-components-retrytask
 * %%
 * Copyright (C) 2022 - 2023 loadup_cloud
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import com.github.loadup.components.retrytask.RetryComponentService;
import com.github.loadup.components.retrytask.config.RetryStrategyConfig;
import com.github.loadup.components.retrytask.config.RetryStrategyFactory;
import com.github.loadup.components.retrytask.enums.ScheduleExecuteType;
import com.github.loadup.components.retrytask.manager.TaskStrategyExecutor;
import com.github.loadup.components.retrytask.manager.TaskStrategyExecutorFactory;
import com.github.loadup.components.retrytask.model.RetryTask;
import com.github.loadup.components.retrytask.model.RetryTaskRequest;
import com.github.loadup.components.retrytask.repository.RetryTaskRepository;
import com.github.loadup.components.retrytask.transaction.RetryTaskTransactionSynchronization;
import com.github.loadup.components.retrytask.transaction.TransactionTemplateFactory;
import com.github.loadup.components.retrytask.utils.ContextUtil;
import com.github.loadup.components.retrytask.utils.IdUtil;
import com.github.loadup.components.retrytask.utils.RetryStrategyUtil;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * The implement of Core Service of retry component
 */
@Slf4j
@Component("retryComponentService")
public class RetryComponentServiceImpl implements RetryComponentService {

    /**
     * the repository service of retry task
     */
    @Autowired
    private RetryTaskRepository retryTaskRepository;

    /**
     * the manager of retry strategy
     */
    @Autowired
    private RetryStrategyFactory retryStrategyFactory;

    /**
     * the factory of transaction templete
     */
    @Autowired
    private TransactionTemplateFactory  transactionTemplateFactory;
    @Autowired
    private TaskStrategyExecutorFactory taskStrategyExecutorFactory;

    /**
     * @see RetryComponentService#register(RetryTaskRequest)
     */
    @Override
    public RetryTask register(RetryTaskRequest retryTaskRequest) {

        // check parameter
        checkParams(retryTaskRequest);

        // construct the retry task
        RetryTask retryTask = constructRetryTask(retryTaskRequest);

        // store the retry task
        retryTaskRepository.insert(retryTask);

        // process the task
        processTask(retryTask, retryTaskRequest.getScheduleExecuteType());

        return retryTask;
    }

    /**
     * @see RetryComponentService#deleteRetryTask(java.lang.String, java.lang.String)
     */
    @Override
    public void deleteRetryTask(String bizId, String bizType) {

        checkParams(bizId, bizType);
        retryTaskRepository.delete(bizId, bizType);
    }

    /**
     * @see RetryComponentService#updateRetryTask(java.lang.String, java.lang.String)
     */
    @Override
    public void updateRetryTask(final String bizId, final String bizType) {

        // check params
        checkParams(bizId, bizType);

        final RetryStrategyConfig retryStrategyConfig = retryStrategyFactory
                .buildRetryStrategyConfig(bizType);
        // obtain the transaction template
        TransactionTemplate transactionTemplate = transactionTemplateFactory
                .obtainTemplate(bizType);

        // execute the transaction
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {

                RetryTask retryTask = retryTaskRepository.lockByBizId(bizId, bizType);
                RetryStrategyUtil.updateRetryTaskByStrategy(retryTask, retryStrategyConfig);
                retryTaskRepository.update(retryTask);
            }
        });
    }

    /**
     * check parameter
     *
     * @param bizId   business id
     * @param bizType business type
     */
    private void checkParams(String bizId, String bizType) {

        if (StringUtils.isBlank(bizId) || StringUtils.isBlank(bizType)) {
            log.warn("handleTask parameter is null. bizId=", bizId, ",bizType=",
                    bizType);
            throw new RuntimeException("handleTask parameter is null");
        }

    }

    /**
     * check parameter
     *
     * @param retryTaskRequest request of retry task
     */
    private void checkParams(RetryTaskRequest retryTaskRequest) {

        if (retryTaskRequest == null) {
            log.warn("parameter illegal,retryTaskRequest=null");
            throw new RuntimeException("parameter illegal,retryTaskRequest=null");
        }

        checkParams(retryTaskRequest.getBizId(), retryTaskRequest.getBizType(),
                retryTaskRequest.getScheduleExecuteType());
    }

    /**
     * check parameter
     *
     * @param bizId               business id
     * @param bizType             business type
     * @param scheduleExecuteType the type of schedule execute
     */
    private void checkParams(String bizId, String bizType, ScheduleExecuteType scheduleExecuteType) {

        if (StringUtils.isBlank(bizId) || StringUtils.isBlank(bizType) || scheduleExecuteType == null) {
            log.warn("register parameter is null. bizId=", bizId, ",bizType=", bizType,
                    ",scheduleExecuteType=", scheduleExecuteType);
            throw new RuntimeException("register parameter is null");
        }

        if (bizId.length() < 12) {
            log.warn("In the register process, the length of business id is illegal. bizId=", bizId);
            throw new RuntimeException(
                    "In the register process, the length of business id is illegal");
        }
    }

    /**
     * construct retry task
     *
     * @param retryTaskRequest request of retry task
     * @return retry task
     */
    private RetryTask constructRetryTask(RetryTaskRequest retryTaskRequest) {

        RetryTask retryTask = new RetryTask();
        RetryStrategyConfig retryStrategyConfig = retryStrategyFactory
                .buildRetryStrategyConfig(retryTaskRequest.getBizType());
        // get the infomation from the thread context
        ContextUtil.extractContext2Task(retryTask);
        retryTask
                .setTaskId(IdUtil.generateId(retryTaskRequest.getBizId()));
        retryTask.setBizId(retryTaskRequest.getBizId());
        retryTask.setBizType(retryTaskRequest.getBizType());
        retryTask.setExecutedTimes(0);
        retryTask.setNextExecuteTime(DateUtils.addMinutes(new Date(),
                retryTaskRequest.getStartExecuteInterval()));
        retryTask.setMaxExecuteTimes(retryStrategyConfig.getMaxExecuteCount());
        retryTask.setUpToMaxExecuteTimesFlag(false);
        retryTask.setProcessingFlag(false);
        retryTask.setBizContext(retryTaskRequest.getBizContext());
        retryTask.setGmtCreate(new Date());
        retryTask.setGmtModified(new Date());
        retryTask.setPriority(retryTaskRequest.getPriority());

        return retryTask;
    }

    /**
     * process after executing the task
     *
     * @param retryTask           retry task
     * @param scheduleExecuteType the type of execute schedule
     */
    private void processTask(RetryTask retryTask, ScheduleExecuteType scheduleExecuteType) {

        RetryStrategyConfig retryStrategyConfig = retryStrategyFactory.buildRetryStrategyConfig(retryTask
                .getBizType());

        // is need execute immediately
        if (!retryStrategyConfig.isExecuteImmediately()) {
            return;
        }

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            //when transaction is already started. After the transaction is commited successfully, the transaction manager process the
            // callback
            TransactionSynchronizationManager
                    .registerSynchronization(new RetryTaskTransactionSynchronization(retryTask,
                            taskStrategyExecutorFactory, scheduleExecuteType));
        } else {
            //when there is no trasaction, the task will be executed immediatelly
            TaskStrategyExecutor taskStrategyExecutor = taskStrategyExecutorFactory
                    .obtainTaskStrategyExecutor(scheduleExecuteType);

            taskStrategyExecutor.execute(retryTask);
        }
    }

}
