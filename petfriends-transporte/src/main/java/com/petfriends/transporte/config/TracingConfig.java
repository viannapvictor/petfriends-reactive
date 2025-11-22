package com.petfriends.transporte.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;

import jakarta.annotation.PostConstruct;

@Configuration
public class TracingConfig {

    private static final Logger log = LoggerFactory.getLogger(TracingConfig.class);

    @PostConstruct
    public void init() {
        log.info("Enabling Reactor automatic context propagation for tracing");
        Hooks.enableAutomaticContextPropagation();
        log.info("Reactor automatic context propagation enabled successfully");
    }
}