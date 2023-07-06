package com.github.loadup.components.retrytask.enums;

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

import com.github.loadup.capability.common.enums.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The type of retry strategy
 *
 * 
 * 
 */
@Getter
@AllArgsConstructor
public enum RetryStrategyType implements IEnum {

    /**
     * User can define interval
     * https://www.jianshu.com/p/a289dde63043
     */
    INTERVAL_SEQUENCE("INTERVAL_SEQUENCE", "User can define interval"),
    /**
     * 指数等待策略
     * 第一次失败后，依次等待时长：2^1 * 100;2^2 * 100；2^3 * 100;...
     * http://en.wikipedia.org/wiki/Exponential_backoff
     */
    ExponentialWaitStrategy("ExponentialWaitStrategy", "User can define interval"),
    /**
     * 斐波那契等待策略
     * 第一次失败后，依次等待时长：1*100;1*100；2*100；3*100；5*100；...
     */
    FibonacciWaitStrategy("FibonacciWaitStrategy", "User can define interval"),
    /**
     * 固定时长等待策略
     */
    FixedWaitStrategy("FixedWaitStrategy", "User can define interval"),

    /**
     * 随机时长等待策略
     */
    RandomWaitStrategy("RandomWaitStrategy", "User can define interval"),
    /**
     * 递增等待策略
     * 第一次失败后，将依次等待1s；6s(1+5)；11(1+5+5)s；16(1+5+5+5)s；...
     */
    IncrementingWaitStrategy("IncrementingWaitStrategy", "User can define interval"),
    /**
     * 异常等待策略
     * 根据所发生的异常指定重试的等待时长；如果异常不匹配，则等待时长为0；
     */
    ExceptionWaitStrategy("ExceptionWaitStrategy", "User can define interval"),
    /**
     * 复合等待策略
     * 复合等待策略；如果所执行的程序满足一个或多个等待策略，那么等待时间为所有等待策略时间的总和。
     */
    CompositeWaitStrategy("CompositeWaitStrategy", "User can define interval"),
    ;

    /**
     * code
     */
    private String code;

    /**
     * description
     */
    private String description;

}
