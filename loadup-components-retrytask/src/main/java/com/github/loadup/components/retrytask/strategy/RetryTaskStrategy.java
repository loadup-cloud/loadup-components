package com.github.loadup.components.retrytask.strategy;

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
