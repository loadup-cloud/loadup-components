package com.github.loadup.components.retrytask.model;

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


import com.github.loadup.capability.common.model.DTO;
import com.github.loadup.capability.common.util.ToStringUtils;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * the domain model of retry task
 *
 * 
 * 
 */
@Getter
@Setter
public class RetryTask extends DTO {

    private static final long serialVersionUID = 1992307869034208628L;
    /**
     * task serial id
     */
    private String taskId;

    /**
     * business id, used as sharding index
     */
    private String bizId;

    /**
     * business type, user can define themselves
     */
    private String bizType;

    /**
     * the executed times
     */
    private int executedTimes;

    /**
     * the time of next executation
     */
    private Date nextExecuteTime;

    /**
     * the maximum of execute times
     */
    private int maxExecuteTimes;

    /**
     * the flag of whether reach the maximum of execute times
     */
    private boolean upToMaxExecuteTimesFlag;

    /**
     * the flag of processing
     */
    private boolean processingFlag;

    /**
     * business context
     */
    private String bizContext;

    /**
     * create time
     */
    private Date gmtCreate;

    /**
     * last modified time
     */
    private Date gmtModified;

    /**
     * priority
     */
    private String priority;

    /**
     * suspended
     */
    private boolean suspended;

    @Override
    public String toString() {
        return ToStringUtils.reflectionToString(this);
    }
}
