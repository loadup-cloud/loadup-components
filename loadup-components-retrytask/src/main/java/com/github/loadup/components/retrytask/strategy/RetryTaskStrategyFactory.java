package com.github.loadup.components.retrytask.strategy;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class RetryTaskStrategyFactory {
    private Map<String, RetryTaskStrategy> retryTaskStrategyMap = new HashMap<>();

    public RetryTaskStrategy findRetryTaskStrategy(String retryStrategyType) {
        return retryTaskStrategyMap.get(retryStrategyType);
    }
}
