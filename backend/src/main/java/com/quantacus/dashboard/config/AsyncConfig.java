package com.quantacus.dashboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    // Named executor used by @Async("pipelineExecutor") in PipelineService.
    // Keeps pipeline threads separate from the main Tomcat request pool so
    // long-running video processing jobs don't block incoming HTTP requests.
    @Bean(name = "pipelineExecutor")
    public Executor pipelineExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("pipeline-");
        executor.initialize();
        return executor;
    }
}
