package com.bibek.utils.normalizer;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

/**
 * Shared normalization logic for request body string fields.
 */
public final class StringNormalizer {

    private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s{2,}");

    public static void normalize(Object obj, NormalizeConfig config) {
        if (obj == null || config == null) return;

        Class<?> clazz = obj.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getType() == String.class) {
                    field.setAccessible(true);
                    try {
                        String value = (String) field.get(obj);
                        if (value != null) {
                            String normalized = apply(value, config);
                            field.set(obj, normalized);
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    public static String apply(String value, NormalizeConfig config) {
        if (value == null) return null;

        if (config.collapseSpaces()) {
            value = MULTIPLE_SPACES.matcher(value).replaceAll(" ");
        }
        if (config.trim()) {
            value = value.trim();
        }
        if (config.blankToNull() && value.isEmpty()) {
            return null;
        }
        return value;
    }

    private StringNormalizer() {}
}
