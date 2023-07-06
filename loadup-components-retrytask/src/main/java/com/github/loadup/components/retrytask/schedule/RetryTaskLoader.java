
package com.github.loadup.components.retrytask.schedule;

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
import com.github.loadup.components.retrytask.constant.RetryTaskConstants;
import com.github.loadup.components.retrytask.enums.TaskPriorityEnum;
import com.github.loadup.components.retrytask.manager.RetryStrategyManager;
import com.github.loadup.components.retrytask.model.RetryTask;
import com.github.loadup.components.retrytask.repository.RetryTaskRepository;
import com.github.loadup.components.retrytask.utils.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import org.springframework.util.CollectionUtils;

/**
 * the retry task loader
 *
 * 
 * 
 */
@Component
@Slf4j
public class RetryTaskLoader {


    /**
     * the repository of retry task
     */
    @Autowired
    private RetryTaskRepository retryTaskRepository;

    /**
     * the manager of retry strategy
     */
    @Autowired
    private RetryStrategyManager retryStrategyManager;

    /**
     * load items
     *
     * @param dataLoadFilterItem DataLoadFilterItem
     * @return items
     */
    public List<String> load(DataLoadFilterItem dataLoadFilterItem) {

        //校验DataItem入参类型
        checkDataItem(dataLoadFilterItem);

        //SQL执行捞取Task列表
        List<RetryTask> retryTasks = loadTasks(dataLoadFilterItem);

        //生成对应的businessKeys，bizType-bizId
        List<String> businessKeys = convertRetryTasks2BusinessKeys(dataLoadFilterItem, retryTasks);

        return businessKeys;
    }

    /**
     * 生成对应的businessKeys，bizType-bizId
     *
     * @param dataLoadFilterItem dataLoadFilterItem
     * @param retryTasks         retryTasks
     * @return businessKeys
     */
    private List<String> convertRetryTasks2BusinessKeys(DataLoadFilterItem dataLoadFilterItem,
                                                        List<RetryTask> retryTasks) {

        List<String> businessKeys = new ArrayList<String>();
        if (CollectionUtils.isEmpty(retryTasks)) {
            return businessKeys;
        }
        RetryTaskDataItem retryTaskDataItem = (RetryTaskDataItem) dataLoadFilterItem;
        String bizType = retryTaskDataItem.getBizType();

        for (RetryTask retryTask : retryTasks) {
            businessKeys.add(bizType + RetryTaskConstants.INTERVAL_CHAR + retryTask.getBizId());
        }
        return businessKeys;
    }

    /**
     * 捞取TaskId列表
     *
     * @param dataLoadFilterItem dataLoadFilterItem
     * @return List<RetryTask>
     */
    private List<RetryTask> loadTasks(DataLoadFilterItem dataLoadFilterItem) {

        List<RetryTask> resultTasks = new ArrayList<RetryTask>();

        RetryTaskDataItem retryTaskDataItem = (RetryTaskDataItem) dataLoadFilterItem;

        String bizType = retryTaskDataItem.getBizType();
        RetryStrategyConfig retryStrategyConfig = retryStrategyManager.getRetryStrategy(bizType);

        if (retryStrategyConfig == null) {
            return resultTasks;
        }

        int tableNum = retryTaskDataItem.getDbNum() * retryTaskDataItem.getTableNumPerDb();
        // 避免计算出来小于1的情况，导致每张表都不捞取
        int rowNum = (retryStrategyConfig.getMaxLoadNum() + tableNum) / tableNum;

        List<RetryTask> retryTasks = null;
        // 生成虚拟业务id
        String virtualBizId = IdUtil.generateVirtualId(retryTaskDataItem.getShardingIdx());
        if (retryStrategyConfig.isIgnorePriority()) {
            retryTasks = retryTaskRepository.load(virtualBizId, bizType, rowNum);
        } else {
            retryTasks = loadByPriority(virtualBizId, bizType, rowNum);
        }

        List<RetryTask> unusualRetryTasks = retryTaskRepository.loadUnuaualTask(virtualBizId,
                retryTaskDataItem.getBizType(), retryStrategyConfig.getExtremeRetryTime(), rowNum);

        if (!CollectionUtils.isEmpty(retryTasks)) {
            resultTasks.addAll(retryTasks);
        }
        if (!CollectionUtils.isEmpty(unusualRetryTasks)) {
            resultTasks.addAll(unusualRetryTasks);
        }

        return resultTasks;
    }

    /**
     * load by priority
     *
     * @param virtualBizId virtualBizId
     * @param bizType      bizType
     * @param rowNum       rowNum
     * @return retryTasks
     */
    private List<RetryTask> loadByPriority(String virtualBizId, String bizType, int rowNum) {

        List<RetryTask> retryTasks = new ArrayList<RetryTask>();

        List<RetryTask> retryTaskHighLevel = retryTaskRepository.loadByPriority(virtualBizId, bizType,
                TaskPriorityEnum.H.getCode(), rowNum);

        int remainSize = 0;

        if (CollectionUtils.isEmpty(retryTaskHighLevel)) {
            remainSize = rowNum;
        } else {
            remainSize = rowNum - retryTaskHighLevel.size();
            retryTasks.addAll(retryTaskHighLevel);
        }

        if (remainSize > 0) {
            List<RetryTask> retryTaskLowLevel = retryTaskRepository.loadByPriority(virtualBizId, bizType,
                    TaskPriorityEnum.L.getCode(), remainSize);
            if (!CollectionUtils.isEmpty(retryTaskLowLevel)) {
                retryTasks.addAll(retryTaskLowLevel);
            }
        }

        return retryTasks;

    }

    /**
     * 校验数据项
     *
     * @param dataLoadFilterItem dataLoadFilterItem
     */
    private void checkDataItem(DataLoadFilterItem dataLoadFilterItem) {

        if (!(dataLoadFilterItem instanceof RetryTaskDataItem)) {
            log.warn( "Loader传参失败，类型不符合。", dataLoadFilterItem);
            throw new RuntimeException("Loader传参失败，类型不符合");
        }
    }

}
