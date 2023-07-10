package com.github.loadup.components.retrytask.strategy;

import com.github.loadup.components.retrytask.config.RetryStrategyConfig;
import com.github.loadup.components.retrytask.enums.RetryStrategyType;
import com.github.loadup.components.retrytask.model.RetryTask;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class FixedWaitStrategy implements RetryTaskStrategy {

    @Override
    public RetryStrategyType getStrategyType() {
        return RetryStrategyType.FIXED_WAIT_STRATEGY;
    }

    /**
     * 固定时长等待策略，失败后，将等待固定的时长进行重试；
     *
     * @param retryTask
     * @param retryStrategyConfig
     * @return
     */

    @Override
    public Date calculateNextExecuteTime(RetryTask retryTask, RetryStrategyConfig retryStrategyConfig) {
        String strategyValue = retryStrategyConfig.getStrategyValue();
        Date lastExecuteTime = retryTask.getNextExecuteTime();
        return addTime(lastExecuteTime, Integer.parseInt(strategyValue), retryStrategyConfig.getStrategyValueUnit());
    }

}
