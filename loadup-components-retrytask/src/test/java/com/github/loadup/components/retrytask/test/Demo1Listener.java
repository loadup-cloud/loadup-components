package com.github.loadup.components.retrytask.test;

import com.github.loadup.components.retrytask.model.RetryTask;
import com.github.loadup.components.retrytask.model.RetryTaskExecuteResult;
import com.github.loadup.components.retrytask.spi.RetryTaskExecuteSPI;
import org.springframework.stereotype.Component;

@Component("demo1Listener")
public class Demo1Listener implements RetryTaskExecuteSPI {
    @Override
    public RetryTaskExecuteResult execute(RetryTask retryTask) {
        RetryTaskExecuteResult result = new RetryTaskExecuteResult();
        result.setSuccess(false);
        System.out.println(result);
        return result;
    }
}
