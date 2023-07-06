
package com.github.loadup.components.retrytask.config;

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

import com.github.loadup.components.retrytask.spi.RetryTaskExecuteSPI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import org.springframework.stereotype.Component;

import com.github.loadup.components.retrytask.descriptor.TaskDescriptor;

/**
 * retry task component
 * 
 * 
 * 
 */
@Component
public class RetryTaskComponent {

    /** extension point */
    private static final String           TASK_EXTENSION_POINT = "retryTaskExtension";

    /** store each taskType's executor */
    protected Map<String, TaskDescriptor> taskDescriptors      = new ConcurrentHashMap<String, TaskDescriptor>();

    /**
     * get Task Listener
     * 
     * @param  taskType taskType
     * @return RetryTaskExecuteSPI
     */
    public RetryTaskExecuteSPI<Object> getTaskListener(String taskType) {

        TaskDescriptor taskDescriptor = taskDescriptors.get(taskType);

        return taskDescriptor.getListener();
    }

    /**
     * get Thread Pool Executor
     * 
     * @param taskType taskType
     * @return executor
     */
    public Executor getExecutor(String taskType) {
        TaskDescriptor taskDescriptor = taskDescriptors.get(taskType);

        return taskDescriptor.getExecutor();
    }

    /**
     * register retry tasks
     * 
     * @param contribs contribs
     */
    private void registerRetryTasks(Object[] contribs) {

        for (Object contrib : contribs) {

            TaskDescriptor descriptor = (TaskDescriptor) contrib;

            taskDescriptors.put(descriptor.getTaskType(), descriptor);
        }

    }
}
