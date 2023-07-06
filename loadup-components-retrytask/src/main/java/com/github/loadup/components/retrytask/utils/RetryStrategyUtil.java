package com.github.loadup.components.retrytask.utils;

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
import com.github.loadup.components.retrytask.model.RetryTask;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * 重试策略工具类
 *
 * 
 * 
 */
public class RetryStrategyUtil {

    /**
     * 分隔符
     */
    public static final char SEPARATOR = ',';

    /**
     * 根据重试策略更新重试任务
     *
     * @param retryTask           retryTask
     * @param retryStrategyConfig retryStrategyConfig
     */
    public static void updateRetryTaskByStrategy(RetryTask retryTask,
            RetryStrategyConfig retryStrategyConfig) {
        String[] intervals = StringUtils.split(retryStrategyConfig.getStrategyValue(), SEPARATOR);
        int executedTimes = retryTask.getExecutedTimes();
        int maxExecuteTimes = retryTask.getMaxExecuteTimes();
        Date lastExecuteTime = retryTask.getNextExecuteTime();

        int intervalsIdx = (executedTimes + 1) >= intervals.length ? intervals.length - 1
                : executedTimes;
        int nextInterval = Integer.parseInt(intervals[intervalsIdx]);
        Date nextExecuteTime = addTime(lastExecuteTime, nextInterval,
                retryStrategyConfig.getStrategyValueUnit());
        retryTask.setExecutedTimes(executedTimes + 1);
        retryTask.setGmtModified(new Date());
        retryTask.setNextExecuteTime(nextExecuteTime);
        retryTask.setProcessingFlag(false);
        retryTask.setUpToMaxExecuteTimesFlag(
                isReachMaxExecuteTimes(retryTask.getExecutedTimes(), maxExecuteTimes));
    }

    /**
     * 根据不同时间单位添加时间间隔
     *
     * @param date
     * @param unit
     * @return
     */
    private static Date addTime(Date date, int interval, String unit) {

        if (unit == "S") {
            return DateUtils.addSeconds(date, interval);
        } else if (unit == "H") {
            return DateUtils.addHours(date, interval);
        } else if (unit == "D") {
            return DateUtils.addDays(date, interval);
        } else {
            return DateUtils.addMinutes(date, interval);
        }

    }

    /**
     * 判断重试次数是否达到上限
     *
     * @param executedTimes   已经执行重试次数
     * @param maxExecuteTimes 最大重试上限
     * @return 是否达到上限
     */
    private static boolean isReachMaxExecuteTimes(int executedTimes, int maxExecuteTimes) {

        if (maxExecuteTimes < 0) {
            return false;
        }
        return (executedTimes >= maxExecuteTimes);
    }
}
