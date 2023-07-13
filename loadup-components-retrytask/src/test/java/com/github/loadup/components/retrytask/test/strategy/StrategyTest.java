package com.github.loadup.components.retrytask.test.strategy;

import com.github.loadup.capability.common.enums.TimeUnitEnum;
import com.github.loadup.components.retrytask.BaseTest;
import com.github.loadup.components.retrytask.config.RetryStrategyConfig;
import com.github.loadup.components.retrytask.enums.RetryStrategyType;
import com.github.loadup.components.retrytask.model.RetryTask;
import com.github.loadup.components.retrytask.strategy.RetryTaskStrategyFactory;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class StrategyTest extends BaseTest {
    @Resource
    RetryTaskStrategyFactory retryTaskStrategyFactory;

    /**
     * 20,40,60,60,160,320
     */
    @Test
    void testExponentialStrategy() {
        RetryTask retryTask = new RetryTask();
        retryTask.setMaxExecuteTimes(-1);
        retryTask.setNextExecuteTime(new Date());

        RetryStrategyConfig config = new RetryStrategyConfig();
        String strategyType = RetryStrategyType.EXPONENTIAL_WAIT_STRATEGY.getCode();
        config.setStrategyType(strategyType);
        config.setStrategyValue("20,500");
        config.setStrategyValueUnit(TimeUnitEnum.SECOND.getCode());

        Map<Integer, Integer> mapping = new HashMap<>();
        mapping.put(0, 20);
        mapping.put(1, 40);
        mapping.put(2, 80);
        mapping.put(3, 160);
        mapping.put(4, 320);
        mapping.forEach((k, v) -> {
            retryTask.setExecutedTimes(k);
            Date date = retryTaskStrategyFactory.findRetryTaskStrategy(strategyType)
                    .calculateNextExecuteTime(retryTask, config);
            Date oriNextExecuteTime = retryTask.getNextExecuteTime();
            log.info("executedTimes={},currentExecuteTime={},nextExecuteTime={}", k, oriNextExecuteTime, date);
            retryTask.setNextExecuteTime(date);
            assertDateEquals(date, DateUtils.addSeconds(oriNextExecuteTime, v));
        });
    }

    /**
     * 20,20,60,100,160
     */
    @Test
    void testFibonacciWaitStrategy() {
        RetryTask retryTask = new RetryTask();
        retryTask.setMaxExecuteTimes(-1);
        retryTask.setNextExecuteTime(new Date());

        RetryStrategyConfig config = new RetryStrategyConfig();
        String strategyType = RetryStrategyType.FIBONACCI_WAIT_STRATEGY.getCode();
        config.setStrategyType(strategyType);
        config.setStrategyValue("20,500");
        config.setStrategyValueUnit(TimeUnitEnum.SECOND.getCode());

        Map<Integer, Integer> mapping = new HashMap<>();
        mapping.put(1, 20);
        mapping.put(2, 20);
        mapping.put(3, 40);
        mapping.put(4, 60);
        mapping.put(5, 100);
        mapping.forEach((k, v) -> {
            retryTask.setExecutedTimes(k);
            Date date = retryTaskStrategyFactory.findRetryTaskStrategy(strategyType)
                    .calculateNextExecuteTime(retryTask, config);
            Date oriNextExecuteTime = retryTask.getNextExecuteTime();
            log.info("executedTimes={},currentExecuteTime={},nextExecuteTime={}", k, retryTask.getNextExecuteTime(), date);
            retryTask.setNextExecuteTime(date);
            assertDateEquals(date, DateUtils.addSeconds(oriNextExecuteTime, v));
        });
    }

    @Test
    void testFixWaitStrategy() {
        RetryTask retryTask = new RetryTask();
        retryTask.setMaxExecuteTimes(-1);
        retryTask.setNextExecuteTime(new Date());

        RetryStrategyConfig config = new RetryStrategyConfig();
        String strategyType = RetryStrategyType.FIXED_WAIT_STRATEGY.getCode();
        config.setStrategyType(strategyType);
        config.setStrategyValue("30");
        config.setStrategyValueUnit(TimeUnitEnum.SECOND.getCode());

        Map<Integer, Integer> mapping = new HashMap<>();
        mapping.put(1, 30);
        mapping.put(2, 30);
        mapping.put(3, 30);
        mapping.put(4, 30);
        mapping.put(5, 30);
        mapping.forEach((k, v) -> {
            retryTask.setExecutedTimes(k);
            Date date = retryTaskStrategyFactory.findRetryTaskStrategy(strategyType)
                    .calculateNextExecuteTime(retryTask, config);
            Date oriNextExecuteTime = retryTask.getNextExecuteTime();
            log.info("executedTimes={},currentExecuteTime={},nextExecuteTime={}", k, oriNextExecuteTime, date);
            retryTask.setNextExecuteTime(date);

            assertDateEquals(date, DateUtils.addSeconds(oriNextExecuteTime, v));
        });
    }

    @Test
    void testRandomWaitStrategy() {
        RetryTask retryTask = new RetryTask();
        retryTask.setMaxExecuteTimes(-1);
        retryTask.setNextExecuteTime(new Date());

        RetryStrategyConfig config = new RetryStrategyConfig();
        String strategyType = RetryStrategyType.RANDOM_WAIT_STRATEGY.getCode();
        config.setStrategyType(strategyType);
        config.setStrategyValue("10,20");
        config.setStrategyValueUnit(TimeUnitEnum.SECOND.getCode());

        Map<Integer, Integer> mapping = new HashMap<>();
        mapping.put(1, 20);
        mapping.put(2, 20);
        mapping.put(3, 20);
        mapping.put(4, 20);
        mapping.put(5, 20);
        mapping.forEach((k, v) -> {
            retryTask.setExecutedTimes(k);
            Date date = retryTaskStrategyFactory.findRetryTaskStrategy(strategyType)
                    .calculateNextExecuteTime(retryTask, config);
            Date oriNextExecuteTime = retryTask.getNextExecuteTime();
            log.info("executedTimes={},currentExecuteTime={},nextExecuteTime={}", k, oriNextExecuteTime, date);
            retryTask.setNextExecuteTime(date);
            Assertions.assertTrue(DateUtils.addSeconds(oriNextExecuteTime, v).after(date));
            Assertions.assertTrue(DateUtils.addSeconds(oriNextExecuteTime, 9).before(date));
        });
    }

    @Test
    void testIncrementingWaitStrategy() {
        RetryTask retryTask = new RetryTask();
        retryTask.setMaxExecuteTimes(-1);
        retryTask.setNextExecuteTime(new Date());

        RetryStrategyConfig config = new RetryStrategyConfig();
        String strategyType = RetryStrategyType.INCREMENTING_WAIT_STRATEGY.getCode();
        config.setStrategyType(strategyType);
        config.setStrategyValue("10,5");
        config.setStrategyValueUnit(TimeUnitEnum.SECOND.getCode());

        Map<Integer, Integer> mapping = new HashMap<>();
        mapping.put(1, 15);
        mapping.put(2, 20);
        mapping.put(3, 25);
        mapping.put(4, 30);
        mapping.put(5, 35);
        mapping.forEach((k, v) -> {
            retryTask.setExecutedTimes(k);
            Date date = retryTaskStrategyFactory.findRetryTaskStrategy(strategyType)
                    .calculateNextExecuteTime(retryTask, config);
            Date oriNextExecuteTime = retryTask.getNextExecuteTime();
            log.info("executedTimes={},currentExecuteTime={},nextExecuteTime={}", k, oriNextExecuteTime, date);
            retryTask.setNextExecuteTime(date);
            Assertions.assertTrue(DateUtils.addSeconds(oriNextExecuteTime, v).after(date));
            Assertions.assertTrue(DateUtils.addSeconds(oriNextExecuteTime, 9).before(date));
        });
    }
}
