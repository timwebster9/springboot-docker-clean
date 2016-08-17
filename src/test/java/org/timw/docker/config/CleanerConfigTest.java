package org.timw.docker.config;

import org.junit.Test;
import org.springframework.core.convert.support.DefaultConversionService;

import static org.assertj.core.api.Assertions.assertThat;

public class CleanerConfigTest {

    @Test
    public void conversionService() throws Exception {
        assertThat(new CleanerConfig().conversionService()).isExactlyInstanceOf(DefaultConversionService.class);
    }

}