package com.bibek.utils.normalizer.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Registers the normalizing JSON converter with RestClient for HTTP Interface support.
 */
@Configuration
@org.springframework.boot.autoconfigure.condition.ConditionalOnClass(RestClient.class)
public class RestClientNormalizerCustomizer {

    @Autowired(required = false)
    private ObjectMapper objectMapper;

    @Bean
    RestClientCustomizer normalizingRestClientCustomizer() {
        return builder -> builder.messageConverters(converters -> {
            var normalizing = objectMapper != null
                    ? new NormalizingJsonHttpMessageConverter(objectMapper)
                    : new NormalizingJsonHttpMessageConverter();
            converters.add(0, normalizing);
        });
    }
}
