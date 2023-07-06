package com.github.loadup.components.retrytask.repository;

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

import com.github.loadup.components.retrytask.config.RetryStrategyConfig;
import com.github.loadup.components.retrytask.config.RetryStrategyFactory;
import com.github.loadup.components.retrytask.manager.RetryStrategyManager;
import com.github.loadup.components.retrytask.model.RetryTask;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * the implement of retry task repository
 *
 * 
 * 
 */
@Slf4j
@Component("retryTaskRepository")
public class RetryTaskRepositoryImpl implements RetryTaskRepository {

    /**
     * retry task DAO collection, key: bizType
     */
    private Map<String, RetryTaskDAO> retryTaskDAOs = new HashMap<String, RetryTaskDAO>();
    /**
     * the factory of retry strategy
     */
    @Autowired
    private RetryStrategyFactory      retryStrategyFactory;
    /**
     * the manager of retry strategy
     */
    @Autowired
    private RetryStrategyManager      retryStrategyManager;

    /**
     * @see RetryTaskRepository#insert(RetryTask)
     */
    @Override
    public void insert(RetryTask retryTask) {

        RetryTaskDAO retryTaskDAO = obtainRetryTaskDAO(retryTask.getBizType());
        retryTaskDAO.insert(retryTask);
    }

    /**
     * @see RetryTaskRepository#lockByBizId(java.lang.String, java.lang.String)
     */
    @Override
    public RetryTask lockByBizId(String bizId, String bizType) {

        RetryTaskDAO retryTaskDAO = obtainRetryTaskDAO(bizType);
        return retryTaskDAO.lockByBizId(bizId, bizType);
    }

    /**
     * @see RetryTaskRepository#loadByBizId(java.lang.String, java.lang.String)
     */
    @Override
    public RetryTask loadByBizId(String bizId, String bizType) {

        RetryTaskDAO retryTaskDAO = obtainRetryTaskDAO(bizType);
        return retryTaskDAO.loadByBizId(bizId, bizType);
    }

    /**
     * @see RetryTaskRepository#delete(java.lang.String, java.lang.String)
     */
    @Override
    public void delete(String bizId, String bizType) {

        RetryTaskDAO retryTaskDAO = obtainRetryTaskDAO(bizType);
        retryTaskDAO.delete(bizId, bizType);
    }

    /**
     * @see RetryTaskRepository#update(RetryTask)
     */
    @Override
    public void update(RetryTask retryTask) {

        RetryTaskDAO retryTaskDAO = obtainRetryTaskDAO(retryTask.getBizType());
        retryTaskDAO.update(retryTask);
    }

    /**
     * @see RetryTaskRepository#load(String, String, int)
     */
    @Override
    public List<RetryTask> load(String virtualBizId, String bizType, int rowNum) {

        RetryTaskDAO retryTaskDAO = obtainRetryTaskDAO(bizType);
        return retryTaskDAO.load(bizType, rowNum);
    }

    /**
     * @see RetryTaskRepository#loadByPriority(String, String, String, int)
     */
    @Override
    public List<RetryTask> loadByPriority(String virtualBizId, String bizType, String priority, int rowNum) {
        RetryTaskDAO retryTaskDAO = obtainRetryTaskDAO(bizType);
        return retryTaskDAO.loadByPriority(bizType, priority, rowNum);
    }

    /**
     * @see RetryTaskRepository#loadUnuaualTask(java.lang.String, java.lang.String, int, int)
     */
    @Override
    public List<RetryTask> loadUnuaualTask(String virtualBizId, String bizType, int extremeRetryTime, int rowNum) {
        RetryTaskDAO retryTaskDAO = obtainRetryTaskDAO(bizType);
        return retryTaskDAO.loadUnuaualTask(bizType, extremeRetryTime, rowNum);
    }

    /**
     * obtain the RetryTaskDAO by bizType
     *
     * @param bizType business type
     * @return RetryTaskDAO
     */
    private RetryTaskDAO obtainRetryTaskDAO(String bizType) {

        if (retryTaskDAOs.get(bizType) != null) {
            return retryTaskDAOs.get(bizType);
        }

        synchronized (this) {

            if (retryTaskDAOs.get(bizType) != null) {
                return retryTaskDAOs.get(bizType);
            }

            RetryStrategyConfig retryStrategyConfig = retryStrategyManager
                    .getRetryStrategy(bizType);

            if (retryStrategyConfig == null) {
                log.warn(
                        "the bizType does not match the retryDataSourceConfig and retryStrategyConfig. bizType=",
                        bizType,
                        ",retryStrategyConfig=", retryStrategyConfig);
                throw new RuntimeException(
                        "the bizType does not match the retryDataSourceConfig and retryStrategyConfig");
            }

            //  构建retry task dao
            RetryTaskDAO jdbcRetryTaskDAO = new JdbcRetryTaskDAO(null);

            // 将动态代理类存放到map中
            retryTaskDAOs.put(bizType, jdbcRetryTaskDAO);
            return jdbcRetryTaskDAO;
        }
    }

}
