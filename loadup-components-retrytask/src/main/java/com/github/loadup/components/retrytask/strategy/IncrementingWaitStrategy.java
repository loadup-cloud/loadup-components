package com.github.loadup.components.retrytask.strategy;

import com.github.loadup.capability.common.util.core.StringPool;
import com.github.loadup.components.retrytask.config.RetryStrategyConfig;
import com.github.loadup.components.retrytask.enums.RetryStrategyType;
import com.github.loadup.components.retrytask.model.RetryTask;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class IncrementingWaitStrategy implements RetryTaskStrategy {
    private long initialSleepTime = 1;
    private long increment        = 5;

    @Override
    public RetryStrategyType getStrategyType() {
        return RetryStrategyType.INCREMENTING_WAIT_STRATEGY;
    }

    /**
     * 递增等待策略，根据初始值和递增值，等待时长依次递增。就本例而言：
     * 第一次失败后，将依次等待1s；6s(1+5)；11(1+5+5)s；16(1+5+5+5)s；...
     *
     * @param retryTask
     * @param retryStrategyConfig
     * @return
     */

    @Override
    public Date calculateNextExecuteTime(RetryTask retryTask, RetryStrategyConfig retryStrategyConfig) {
        String[] intervals = StringUtils.split(retryStrategyConfig.getStrategyValue(), StringPool.COMMA);
        if (intervals.length == 2) {
            initialSleepTime = Long.parseLong(intervals[0]);
            increment = Long.parseLong(intervals[1]);
        }
        long result = initialSleepTime + (increment * (retryTask.getExecutedTimes() - 1));
        Date lastExecuteTime = retryTask.getNextExecuteTime();
        return addTime(lastExecuteTime, Math.toIntExact(result + 1), retryStrategyConfig.getStrategyValueUnit());
    }

}
