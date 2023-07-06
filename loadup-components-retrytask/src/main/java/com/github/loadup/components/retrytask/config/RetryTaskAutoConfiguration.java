package com.github.loadup.components.retrytask.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@AutoConfiguration
@EnableAsync
@EnableScheduling
public class RetryTaskAutoConfiguration {
    @Value("${retrytask.loader.thread.core_pool_size:5}")
    private int loaderCorePoolSize;
    @Value("${retrytask.loader.thread.max_pool_size:10}")
    private int loaderMaxPoolSize;
    @Value("${retrytask.loader.thread.keep_alive_seconds:60}")
    private int loaderKeepAliveSeconds;
    @Value("${retrytask.loader.thread.queue_capacity:10}")
    private int loaderQueueCapacity;

    @Value("${retrytask.executor.thread.core_pool_size:12}")
    private int executorCorePoolSize;
    @Value("${retrytask.executor.thread.max_pool_size:20}")
    private int executorMaxPoolSize;
    @Value("${retrytask.executor.thread.keep_alive_seconds:60}")
    private int executorKeepAliveSeconds;
    @Value("${retrytask.executor.thread.queue_capacity:100}")
    private int executorQueueCapacity;

    @Bean("retryTaskLoaderThreadPool")
    public ThreadPoolTaskExecutor retryTaskLoaderThreadPool() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(loaderCorePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(loaderMaxPoolSize);
        threadPoolTaskExecutor.setKeepAliveSeconds(loaderKeepAliveSeconds);
        threadPoolTaskExecutor.setQueueCapacity(loaderQueueCapacity);
        return threadPoolTaskExecutor;
    }

    @Bean("retryTaskExecutorThreadPool")
    public ThreadPoolTaskExecutor retryTaskExecutorThreadPool() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(executorCorePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(executorMaxPoolSize);
        threadPoolTaskExecutor.setKeepAliveSeconds(executorKeepAliveSeconds);
        threadPoolTaskExecutor.setQueueCapacity(executorQueueCapacity);
        return threadPoolTaskExecutor;
    }
}
