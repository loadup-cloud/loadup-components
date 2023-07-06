
package com.github.loadup.components.retrytask.descriptor;

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

import com.github.loadup.capability.common.util.ToStringUtils;
import com.github.loadup.components.retrytask.spi.RetryTaskExecuteSPI;
import java.util.concurrent.Executor;

/**
 * Task descriptor
 *
 * 
 * 
 */
public class TaskDescriptor {

    /**
     * task name
     */
    private String taskType;

    /**
     * task executor
     */
    private RetryTaskExecuteSPI<Object> listener;

    /**
     * Thread Pool
     */
    private Executor executor;

    /**
     * description
     */
    private String desc;

    /**
     * Getter method for property <tt>listener</tt>.
     *
     * @return property value of listener
     */
    public RetryTaskExecuteSPI<Object> getListener() {
        return listener;
    }

    /**
     * Setter method for property <tt>listener</tt>.
     *
     * @param listener value to be assigned to property listener
     */
    public void setListener(RetryTaskExecuteSPI<Object> listener) {
        this.listener = listener;
    }

    /**
     * Getter method for property <tt>executor</tt>.
     *
     * @return property value of executor
     */
    public Executor getExecutor() {
        return executor;
    }

    /**
     * Setter method for property <tt>executor</tt>.
     *
     * @param executor value to be assigned to property executor
     */
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    /**
     * Getter method for property <tt>desc</tt>.
     *
     * @return property value of desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Setter method for property <tt>desc</tt>.
     *
     * @param desc value to be assigned to property desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * Getter method for property <tt>taskType</tt>.
     *
     * @return property value of taskType
     */
    public String getTaskType() {
        return taskType;
    }

    /**
     * Setter method for property <tt>taskType</tt>.
     *
     * @param taskType value to be assigned to property taskType
     */
    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringUtils.reflectionToString(this);
    }
}
