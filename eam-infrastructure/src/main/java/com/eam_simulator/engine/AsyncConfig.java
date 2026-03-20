package com.eam_simulator.engine;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
class AsyncConfig {

    @Value("${engine.async.core-pool-size:2}")
    private int corePoolSize;

    @Value("${engine.async.max-pool-size:5}")
    private int maxPoolSize;

    @Value("${engine.async.queue-capacity:10}")
    private int queueCapacity;

    @Value("${engine.async.thread-prefix:GameEngine-}")
    private String threadNamePrefix;

    @Value("${engine.async.await-termination-seconds:30}")
    private int awaitTerminationSeconds;


    @Bean(name = "gameEngineTaskExecutor")
    Executor gameEngineTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
