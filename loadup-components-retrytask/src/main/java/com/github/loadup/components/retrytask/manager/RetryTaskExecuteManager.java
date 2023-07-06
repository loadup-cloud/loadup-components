package com.github.loadup.components.retrytask.manager;

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

import com.github.loadup.components.retrytask.enums.ScheduleExecuteType;
import com.github.loadup.components.retrytask.model.RetryTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * the manager of retry task execute
 *
 * 
 * 
 */
@Component
@Slf4j
public class RetryTaskExecuteManager {

    /**
     * the factory of retry task strategy
     */
    @Autowired
    private TaskStrategyExecutorFactory taskStrategyExecutorFactory;

    /**
     * Execute the retry task by the strategy
     *
     * @param retryTask           retry task
     * @param scheduleExecuteType strategy
     */
    public void execute(RetryTask retryTask, ScheduleExecuteType scheduleExecuteType) {

        if (scheduleExecuteType == null || retryTask == null) {
            log.warn("RetryTaskExecuteManager parameter illegal, scheduleExecuteType=",
                    scheduleExecuteType, ",retryTask=", retryTask);
            return;
        }

        TaskStrategyExecutor taskStrategyExecutor = taskStrategyExecutorFactory
                .obtainTaskStrategyExecutor(scheduleExecuteType);

        taskStrategyExecutor.execute(retryTask);
    }

}
