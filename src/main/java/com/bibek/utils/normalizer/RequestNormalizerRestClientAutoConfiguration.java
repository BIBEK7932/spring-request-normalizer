package com.bibek.utils.normalizer;

import com.bibek.utils.normalizer.client.RestClientNormalizerCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;

/**
 * Auto-configuration for RestClient / HTTP Interface normalization support.
 */
@Configuration
@ConditionalOnClass(RestClient.class)
@Import(RestClientNormalizerCustomizer.class)
class RequestNormalizerRestClientAutoConfiguration {
}
