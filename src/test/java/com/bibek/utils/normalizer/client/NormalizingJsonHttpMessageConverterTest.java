package com.bibek.utils.normalizer.client;

import com.bibek.utils.normalizer.NormalizeInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.http.MockHttpOutputMessage;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class NormalizingJsonHttpMessageConverterTest {

    private NormalizingJsonHttpMessageConverter converter;

    @BeforeEach
    void setUp() {
        converter = new NormalizingJsonHttpMessageConverter();
    }

    @Test
    void canRead_returnsTrueForNormalizeInputType() {
        assertTrue(converter.canRead(NormalizedRequest.class, MediaType.APPLICATION_JSON));
    }

    @Test
    void canRead_returnsFalseForNonNormalizeInputType() {
        assertFalse(converter.canRead(PlainRequest.class, MediaType.APPLICATION_JSON));
    }

    @Test
    void canWrite_returnsTrueForNormalizeInputType() {
        assertTrue(converter.canWrite(NormalizedRequest.class, MediaType.APPLICATION_JSON));
    }

    @Test
    void canWrite_returnsFalseForNonNormalizeInputType() {
        assertFalse(converter.canWrite(PlainRequest.class, MediaType.APPLICATION_JSON));
    }

    @Test
    void read_normalizesResponseBody() throws Exception {
        String json = "{\"name\":\"  John  \",\"email\":\"   \"}";
        MockHttpInputMessage input = new MockHttpInputMessage(json.getBytes(StandardCharsets.UTF_8));
        input.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        NormalizedResponse result = (NormalizedResponse) converter.read(
                NormalizedResponse.class, null, input);

        assertNotNull(result);
        assertEquals("John", result.name);
        assertNull(result.email);
    }

    @Test
    void read_normalizesResponseBody_collapseSpaces() throws Exception {
        String json = "{\"name\":\"hello    world\",\"status\":\"ok\"}";
        MockHttpInputMessage input = new MockHttpInputMessage(json.getBytes(StandardCharsets.UTF_8));
        input.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        NormalizedResponse result = (NormalizedResponse) converter.read(
                NormalizedResponse.class, null, input);

        assertNotNull(result);
        assertEquals("hello world", result.name);
        assertEquals("ok", result.status);
    }

    @Test
    void write_normalizesRequestBodyBeforeSerialization() throws Exception {
        NormalizedRequest request = new NormalizedRequest("  foo  ", "  ");
        MockHttpOutputMessage output = new MockHttpOutputMessage();

        converter.write(request, MediaType.APPLICATION_JSON, output);

        // Object should be normalized in place before write
        assertEquals("foo", request.name);
        assertNull(request.email);

        String body = output.getBodyAsString(StandardCharsets.UTF_8);
        assertTrue(body.contains("\"name\":\"foo\""));
        assertTrue(body.contains("\"email\":null"));
    }

    @Test
    void write_normalizesRequestBody_collapseSpaces() throws Exception {
        NormalizedRequest request = new NormalizedRequest("a    b    c", "x@y.com");
        MockHttpOutputMessage output = new MockHttpOutputMessage();

        converter.write(request, MediaType.APPLICATION_JSON, output);

        assertEquals("a b c", request.name);
        assertEquals("x@y.com", request.email);
    }

    @NormalizeInput(trim = true, blankToNull = true, collapseSpaces = true)
    static class NormalizedRequest {
        public String name;
        public String email;

        public NormalizedRequest() {
        }

        public NormalizedRequest(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }

    @NormalizeInput(trim = true, blankToNull = true, collapseSpaces = true)
    static class NormalizedResponse {
        public String name;
        public String email;
        public String status;

        public NormalizedResponse() {
        }
    }

    static class PlainRequest {
        public String name;
    }
}
