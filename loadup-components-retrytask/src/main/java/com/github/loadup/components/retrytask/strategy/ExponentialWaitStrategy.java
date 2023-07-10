package com.github.loadup.components.retrytask.strategy;

import com.github.loadup.capability.common.util.core.StringPool;
import com.github.loadup.components.retrytask.config.RetryStrategyConfig;
import com.github.loadup.components.retrytask.enums.RetryStrategyType;
import com.github.loadup.components.retrytask.model.RetryTask;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class ExponentialWaitStrategy implements RetryTaskStrategy {
    /**
     * 1 unit 作为指数
     */
    private long multiplier  = 1;
    /**
     * 最大重试到 50 unit
     */
    private long maximumWait = 50;

    @Override
    public RetryStrategyType getStrategyType() {
        return RetryStrategyType.EXPONENTIAL_WAIT_STRATEGY;
    }

    /**
     * 创建一个永久重试的重试器，每次重试失败时以递增的指数时间等待，直到最多5分钟。
     * 5分钟后，每隔5分钟重试一次。
     * 第一次失败后，依次等待时长：2^1 * 100;2^2 * 100；2^3 * 100;...
     * strategyValue 配置为multiplier，maximumWait 不填或填错则使用默认值
     *
     * @param retryTask
     * @param retryStrategyConfig
     * @return
     */

    @Override
    public Date calculateNextExecuteTime(RetryTask retryTask, RetryStrategyConfig retryStrategyConfig) {
        String[] intervals = StringUtils.split(retryStrategyConfig.getStrategyValue(), StringPool.COMMA, 2);
        if (intervals.length == 2) {
            multiplier = Long.parseLong(intervals[0]);
            maximumWait = Long.parseLong(intervals[1]);
        }
        int executedTimes = retryTask.getExecutedTimes();
        double exp = Math.pow(2, executedTimes);
        long result = Math.round(multiplier * exp);

        if (result > maximumWait || result < 0L) {
            result = maximumWait;
        }
        Date lastExecuteTime = retryTask.getNextExecuteTime();
        return addTime(lastExecuteTime, Math.toIntExact(result), retryStrategyConfig.getStrategyValueUnit());
    }

}
