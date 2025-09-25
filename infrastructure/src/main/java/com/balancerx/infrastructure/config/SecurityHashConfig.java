package com.balancerx.infrastructure.config;

import com.balancerx.domain.service.ChecksumService;
import com.balancerx.infrastructure.storage.Sha256ChecksumService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityHashConfig {
    @Bean
    public ChecksumService checksumService() {
        return new Sha256ChecksumService();
    }
}
