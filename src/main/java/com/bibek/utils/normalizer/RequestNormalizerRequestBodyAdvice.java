package com.bibek.utils.normalizer;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;

@ControllerAdvice
public class RequestNormalizerRequestBodyAdvice extends RequestBodyAdviceAdapter {

    @Override
    public boolean supports(MethodParameter methodParameter,
                            Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> type = methodParameter.getParameterType();
        return methodParameter.hasParameterAnnotation(NormalizeInput.class)
                || type.isAnnotationPresent(NormalizeInput.class);
    }

    @Override
    public Object afterBodyRead(Object body,
                                HttpInputMessage inputMessage,
                                MethodParameter parameter,
                                Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
        NormalizeConfig config = resolveConfig(parameter);
        if (config != null) {
            StringNormalizer.normalize(body, config);
        }
        return body;
    }

    private NormalizeConfig resolveConfig(MethodParameter parameter) {
        NormalizeInput paramAnnotation = parameter.getParameterAnnotation(NormalizeInput.class);
        if (paramAnnotation != null) {
            return new NormalizeConfig(paramAnnotation.trim(), paramAnnotation.blankToNull(), paramAnnotation.collapseSpaces());
        }
        Class<?> type = parameter.getParameterType();
        NormalizeInput typeAnnotation = type.getAnnotation(NormalizeInput.class);
        if (typeAnnotation != null) {
            return new NormalizeConfig(typeAnnotation.trim(), typeAnnotation.blankToNull(), typeAnnotation.collapseSpaces());
        }
        return null;
    }
}
