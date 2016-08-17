package org.timw.docker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
public class CleanerConfig {

    @Bean
    public ConversionService conversionService() {
        return new DefaultConversionService();
    }
}
