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


import com.github.loadup.capability.common.enums.TimeUnitEnum;
import com.github.loadup.components.retrytask.config.RetryStrategyConfig;
import com.github.loadup.components.retrytask.enums.RetryStrategyType;
import com.github.loadup.components.retrytask.model.RetryTask;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

public interface RetryTaskStrategy {
    RetryStrategyType getStrategyType();

    Date calculateNextExecuteTime(RetryTask retryTask, RetryStrategyConfig retryStrategyConfig);

    default Date addTime(Date date, int interval, String unit) {
        if (StringUtils.equals(unit, TimeUnitEnum.SECOND.getCode())) {
            return DateUtils.addSeconds(date, interval);
        } else if (StringUtils.equals(unit, TimeUnitEnum.MINUTE.getCode())) {
            return DateUtils.addMinutes(date, interval);
        } else if (StringUtils.equals(unit, TimeUnitEnum.HOUR.getCode())) {
            return DateUtils.addHours(date, interval);
        } else if (StringUtils.equals(unit, TimeUnitEnum.DAY.getCode())) {
            return DateUtils.addDays(date, interval);
        } else if (StringUtils.equals(unit, TimeUnitEnum.MONTH.getCode())) {
            return DateUtils.addMonths(date, interval);
        } else if (StringUtils.equals(unit, TimeUnitEnum.YEAR.getCode())) {
            return DateUtils.addYears(date, interval);
        }
        return date;
    }
}
