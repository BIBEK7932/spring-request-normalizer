package com.bibek.utils.normalizer;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration-style test for RequestNormalizerRequestBodyAdvice.
 * Tests the advice logic directly without starting Spring Boot (avoids Mockito/Java 25 issues).
 */
class RequestNormalizerIntegrationTest {

    private final RequestNormalizerRequestBodyAdvice advice = new RequestNormalizerRequestBodyAdvice();

    @Test
    void adviceNormalizesBody_trimAndBlankToNull() throws Exception {
        Method method = TestController.class.getMethod("handle", TestRequest.class);
        MethodParameter param = new MethodParameter(method, 0);

        assertTrue(advice.supports(param, null, StringHttpMessageConverter.class));

        TestRequest body = new TestRequest("  John  ", "   ");
        Object result = advice.afterBodyRead(
                body, null, param, null, StringHttpMessageConverter.class);

        assertSame(body, result);
        assertEquals("John", body.name);
        assertNull(body.email);
    }

    @Test
    void adviceNormalizesBody_collapseSpaces() throws Exception {
        Method method = TestController.class.getMethod("handle", TestRequest.class);
        MethodParameter param = new MethodParameter(method, 0);

        TestRequest body = new TestRequest("hello    world", "a@b.com");
        advice.afterBodyRead(body, null, param, null, StringHttpMessageConverter.class);

        assertEquals("hello world", body.name);
        assertEquals("a@b.com", body.email);
    }

    @NormalizeInput(trim = true, blankToNull = true, collapseSpaces = true)
    static class TestRequest {
        public String name;
        public String email;

        public TestRequest(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }

    @RestController
    static class TestController {
        public TestRequest handle(@RequestBody TestRequest request) {
            return request;
        }
    }
}
