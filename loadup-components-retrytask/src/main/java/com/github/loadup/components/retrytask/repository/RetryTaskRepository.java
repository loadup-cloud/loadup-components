
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

import com.github.loadup.components.retrytask.model.RetryTask;
import java.util.List;

/**
 * Repository of retry task
 * 
 * 
 * 
 */
public interface RetryTaskRepository {

    /**
     * insert RetryTask
     * 
     * @param retryTask  retry task
     */
    void insert(RetryTask retryTask);

    /**
     * update RetryTask
     * 
     * @param retryTask retry task
     */
    void update(RetryTask retryTask);

    /**
     * lock retry task by Id
     * 
     * @param bizType   business type
     * @param bizId     business id
     * @return          retry task
     */
    RetryTask lockByBizId(String bizId, String bizType);

    /**
     * load retry task by Id
     * 
     * @param bizType   business type
     * @param bizId     business id
     * @return          retry task
     */
    RetryTask loadByBizId(String bizId, String bizType);

    /**
     * delete the task
     * 
     * @param bizType   business type
     * @param bizId     business id
     */
    void delete(String bizId, String bizType);

    /**
     * 根据bizType和规定最大捞取时间捞取rowNum数量的任务id(不区分租户id)，只能给到调度三层分发的Loader层使用 // TODO 注释对应调整下
     * 
     * 捞取没有超过次数限制而且未执行过的流水
     *
     * @param virtualBizId virtual biz id, used for db sharding
     * @param bizType   business type
     * @param rowNum    total row number
     * @return task id list
     */
    List<RetryTask> load(String virtualBizId, String bizType, int rowNum);

    /**
     * 根据bizType和规定最大捞取时间捞取rowNum数量的任务id(不区分租户id)，只能给到调度三层分发的Loader层使用
     * 
     * 捞取没有超过次数限制而且未执行过的流水,根据优先级进行排序
     *
     * @param virtualBizId virtual biz id, used for db sharding
     * @param bizType   business type
     * @param priority  priority
     * @param rowNum    total row number
     * @return task id list
     */
    List<RetryTask> loadByPriority(String virtualBizId, String bizType, String priority, int rowNum);

    /**
     * 根据bizType和规定最大捞取时间捞取rowNum数量的任务id(不区分租户id)，只能给到调度三层分发的Loader层使用
     * 
     * 捞取没有超过次数限制而且状态为已执行过但是超过30分钟还没有被删除的流水
     * @param virtualBizId virtual biz id, used for db sharding
     * @param bizType   business type
     * @param rowNum    total row number
     * @param extremeRetryTime   @see RetryStrategyConfig.extremeRetryTime   
     * @return task id list
     */
    List<RetryTask> loadUnuaualTask(String virtualBizId, String bizType, int extremeRetryTime, int rowNum);
}
