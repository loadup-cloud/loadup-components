package com.github.loadup.components.retrytask.strategy;

import com.github.loadup.capability.common.util.core.StringPool;
import com.github.loadup.components.retrytask.config.RetryStrategyConfig;
import com.github.loadup.components.retrytask.enums.RetryStrategyType;
import com.github.loadup.components.retrytask.model.RetryTask;
import java.util.Date;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class RandomWaitStrategy implements RetryTaskStrategy {
    /**
     * minimum
     */
    private long minimum = 1;
    /**
     * maximum
     */
    private long maximum = 10;

    @Override
    public RetryStrategyType getStrategyType() {
        return RetryStrategyType.RANDOM_WAIT_STRATEGY;
    }

    /**
     * 随机时长等待策略，可以设置一个随机等待的最大时长，也可以设置一个随机等待的时长区间。
     *
     * @param retryTask
     * @param retryStrategyConfig
     * @return
     */

    @Override
    public Date calculateNextExecuteTime(RetryTask retryTask, RetryStrategyConfig retryStrategyConfig) {
        String[] intervals = StringUtils.split(retryStrategyConfig.getStrategyValue(), StringPool.COMMA);
        if (intervals.length == 1) {
            maximum = Long.parseLong(intervals[0]);
        } else if (intervals.length == 2) {
            minimum = Long.parseLong(intervals[0]);
            maximum = Long.parseLong(intervals[1]);
        }
        long result = Math.abs(RandomUtils.nextLong()) % (maximum - minimum);
        Date lastExecuteTime = retryTask.getNextExecuteTime();
        return addTime(lastExecuteTime, Math.toIntExact(result + 1), retryStrategyConfig.getStrategyValueUnit());
    }

}
