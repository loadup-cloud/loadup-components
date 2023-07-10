package com.github.loadup.components.retrytask.strategy;

import com.github.loadup.capability.common.util.core.StringPool;
import com.github.loadup.components.retrytask.config.RetryStrategyConfig;
import com.github.loadup.components.retrytask.enums.RetryStrategyType;
import com.github.loadup.components.retrytask.model.RetryTask;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author Admin
 */
@Component
public class SequenceStrategy implements RetryTaskStrategy {
    @Override
    public RetryStrategyType getStrategyType() {
        return RetryStrategyType.INTERVAL_SEQUENCE;
    }

    @Override
    public Date calculateNextExecuteTime(RetryTask retryTask, RetryStrategyConfig retryStrategyConfig) {

        String[] intervals = StringUtils.split(retryStrategyConfig.getStrategyValue(), StringPool.COMMA);
        int executedTimes = retryTask.getExecutedTimes();
        Date lastExecuteTime = retryTask.getNextExecuteTime();

        int intervalsIdx = (executedTimes + 1) >= intervals.length ? intervals.length - 1
                : executedTimes;
        int nextInterval = Integer.parseInt(intervals[intervalsIdx]);
        return addTime(lastExecuteTime, nextInterval,
                retryStrategyConfig.getStrategyValueUnit());
    }

}
