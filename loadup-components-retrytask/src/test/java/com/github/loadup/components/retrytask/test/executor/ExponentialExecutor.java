package com.github.loadup.components.retrytask.test.executor;

import com.github.loadup.components.retrytask.model.RetryTask;
import com.github.loadup.components.retrytask.model.RetryTaskExecuteResult;
import com.github.loadup.components.retrytask.spi.RetryTaskExecutorSpi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExponentialExecutor implements RetryTaskExecutorSpi {
    @Override
    public RetryTaskExecuteResult execute(RetryTask retryTask) {
        RetryTaskExecuteResult result = new RetryTaskExecuteResult();
        result.setSuccess(false);
        if (retryTask.getExecutedTimes() >= 5) {
            result.setSuccess(true);
        }
        log.info("retryTask run at {} times,result={}", retryTask.getExecutedTimes() + 1, result);
        return result;
    }

    @Override
    public String getTaskType() {
        return "EXPONENTIAL";
    }
}
