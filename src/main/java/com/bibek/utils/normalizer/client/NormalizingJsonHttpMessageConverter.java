package com.bibek.utils.normalizer.client;

import com.bibek.utils.normalizer.NormalizeConfig;
import com.bibek.utils.normalizer.NormalizeInput;
import com.bibek.utils.normalizer.StringNormalizer;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * HttpMessageConverter that normalizes request and response bodies for RestClient / HTTP Interface.
 * Wraps Jackson JSON conversion and applies normalization to types annotated with {@link NormalizeInput}.
 */
class NormalizingJsonHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    NormalizingJsonHttpMessageConverter() {
    }

    NormalizingJsonHttpMessageConverter(com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return super.canRead(clazz, mediaType) && needsNormalization(clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return super.canWrite(clazz, mediaType) && needsNormalization(clazz);
    }

    @Override
    public Object read(Type type, Class<?> contextClass, org.springframework.http.HttpInputMessage inputMessage)
            throws IOException, org.springframework.http.converter.HttpMessageNotReadableException {
        Object result = super.read(type, contextClass, inputMessage);
        if (result != null) {
            Class<?> targetClass = resolveClass(type);
            if (targetClass != null) {
                NormalizeConfig config = resolveConfig(targetClass);
                if (config != null) {
                    StringNormalizer.normalize(result, config);
                }
            }
        }
        return result;
    }

    @Override
    protected void writeInternal(Object object, Type type, org.springframework.http.HttpOutputMessage outputMessage)
            throws IOException, org.springframework.http.converter.HttpMessageNotWritableException {
        if (object != null) {
            NormalizeConfig config = resolveConfig(object.getClass());
            if (config != null) {
                StringNormalizer.normalize(object, config);
            }
        }
        super.writeInternal(object, type, outputMessage);
    }

    private static boolean needsNormalization(Class<?> clazz) {
        return clazz != null && clazz.isAnnotationPresent(NormalizeInput.class);
    }

    private static Class<?> resolveClass(Type type) {
        if (type instanceof Class<?> c) {
            return c;
        }
        if (type instanceof java.lang.reflect.ParameterizedType pt) {
            return resolveClass(pt.getRawType());
        }
        return null;
    }

    private static NormalizeConfig resolveConfig(Class<?> type) {
        NormalizeInput a = type.getAnnotation(NormalizeInput.class);
        return a != null
                ? new NormalizeConfig(a.trim(), a.blankToNull(), a.collapseSpaces())
                : null;
    }
}
