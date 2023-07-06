
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

import com.github.loadup.components.retrytask.config.RetryTaskComponent;
import com.github.loadup.components.retrytask.model.RetryTask;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * the executer of executing the task asynchronized
 * 
 * 
 * 
 */
public class AsyncTaskStrategyExecutor implements TaskStrategyExecutor {

    /** the executor of retry task */
    @Autowired
    private RetryTaskExecutor  retryTaskExecutor;

    /** retry task component */
    @Autowired
    private RetryTaskComponent retryTaskComponent;

    /** 
     * @see TaskExecutor#execute(RetryTask)
     */
    @Override
    public void execute(RetryTask retryTask) {

        RetryTaskRunner retryTaskRunner = new RetryTaskRunner(retryTaskExecutor, retryTask);

        Executor executor = retryTaskComponent.getExecutor(retryTask.getBizType());

        executor.execute(retryTaskRunner);

    }

}
