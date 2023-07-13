package com.github.loadup.components.retrytask.strategy;

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


import com.github.loadup.capability.common.util.core.StringPool;
import com.github.loadup.components.retrytask.config.RetryStrategyConfig;
import com.github.loadup.components.retrytask.enums.RetryStrategyType;
import com.github.loadup.components.retrytask.model.RetryTask;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class FibonacciWaitStrategy implements RetryTaskStrategy {
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
        return RetryStrategyType.FIBONACCI_WAIT_STRATEGY;
    }

    /**
     * 创建一个永久重试的重试器，每次重试失败时以斐波那契数列来计算等待时间，直到最多5分钟；5分钟后，每隔5分钟重试一次；
     *
     * 第一次失败后，依次等待时长：1*1;1*1；2*1；3*1；5*1；...
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
        long fib = fib(executedTimes);
        long result = multiplier * fib;

        if (result > maximumWait || result < 0L) {
            result = maximumWait;
        }
        Date lastExecuteTime = retryTask.getNextExecuteTime();
        return addTime(lastExecuteTime, Math.toIntExact(result), retryStrategyConfig.getStrategyValueUnit());
    }

    private long fib(long n) {
        if (n == 0L) {return 0L;}
        if (n == 1L) {return 1L;}

        long prevPrev = 0L;
        long prev = 1L;
        long result = 0L;

        for (long i = 2L; i <= n; i++) {
            result = prev + prevPrev;
            prevPrev = prev;
            prev = result;
        }

        return result;
    }

    public static void main(String[] args) {
        FibonacciWaitStrategy f = new FibonacciWaitStrategy();
        System.out.println(f.fib(0));
        System.out.println(f.fib(1));
        System.out.println(f.fib(2));
        System.out.println(f.fib(3));
        System.out.println(f.fib(4));
    }

}
