package com.bibek.utils.normalizer.client;

import com.bibek.utils.normalizer.NormalizeInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Integration test for RestClient + HTTP Interface with normalization.
 */
class RestClientNormalizerIntegrationTest {

    private static final String BASE_URL = "http://localhost";

    private MockRestServiceServer server;
    private TestHttpClient client;

    @BeforeEach
    void setUp() {
        var normalizingConverter = new NormalizingJsonHttpMessageConverter();
        var builder = RestClient.builder()
                .baseUrl(BASE_URL)
                .messageConverters(converters -> {
                    converters.clear();
                    converters.add(normalizingConverter);
                    converters.add(new MappingJackson2HttpMessageConverter());
                });

        server = MockRestServiceServer.bindTo(builder).build();
        var restClient = builder.build();

        var factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
        client = factory.createClient(TestHttpClient.class);
    }

    @Test
    void requestBodyNormalizedBeforeSending() {
        server.expect(requestTo(BASE_URL + "/api/echo"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andExpect(content().string("{\"value\":\"trimmed\"}"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        var request = new EchoRequest("  trimmed  ");
        client.echo(request);

        assertEquals("trimmed", request.value);
        server.verify();
    }

    @Test
    void responseBodyNormalizedAfterReceiving() {
        server.expect(requestTo(BASE_URL + "/api/response"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess("{\"data\":\"  normalized  \"}", MediaType.APPLICATION_JSON));

        var response = client.getResponse();

        assertNotNull(response);
        assertEquals("normalized", response.data);
        server.verify();
    }

    @Test
    void blankStringConvertedToNull() {
        server.expect(requestTo(BASE_URL + "/api/echo"))
                .andExpect(content().string("{\"value\":null}"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        var request = new EchoRequest("   ");
        client.echo(request);

        assertNull(request.value);
        server.verify();
    }

    @HttpExchange("/api")
    interface TestHttpClient {
        @PostExchange("/echo")
        void echo(@org.springframework.web.bind.annotation.RequestBody EchoRequest request);

        @GetExchange("/response")
        ResponseDto getResponse();
    }

    @NormalizeInput
    static class EchoRequest {
        public String value;

        public EchoRequest() {
        }

        public EchoRequest(String value) {
            this.value = value;
        }
    }

    @NormalizeInput
    static class ResponseDto {
        public String data;

        public ResponseDto() {
        }
    }
}
