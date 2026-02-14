package com.bibek.utils.normalizer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringNormalizerTest {

    private static final NormalizeConfig DEFAULT = new NormalizeConfig(true, true, false);
    private static final NormalizeConfig NO_TRIM = new NormalizeConfig(false, true, false);
    private static final NormalizeConfig NO_BLANK_TO_NULL = new NormalizeConfig(true, false, false);
    private static final NormalizeConfig WITH_COLLAPSE = new NormalizeConfig(true, true, true);

    @Test
    void apply_trimAndBlankToNull_trimsAndConvertsEmptyToNull() {
        assertNull(StringNormalizer.apply("  ", DEFAULT));
        assertNull(StringNormalizer.apply("", DEFAULT));
        assertNull(StringNormalizer.apply("\t\n", DEFAULT));
        assertEquals("foo", StringNormalizer.apply("  foo  ", DEFAULT));
        assertEquals("bar", StringNormalizer.apply("bar", DEFAULT));
    }

    @Test
    void apply_collapseSpaces_reducesMultipleSpaces() {
        assertEquals("hello world", StringNormalizer.apply("hello    world", WITH_COLLAPSE));
        assertEquals("a b c", StringNormalizer.apply("a   b   c", WITH_COLLAPSE));
        assertEquals("hello world", StringNormalizer.apply("  hello   world  ", WITH_COLLAPSE));
    }

    @Test
    void apply_noTrim_preservesWhitespace() {
        assertEquals("  foo  ", StringNormalizer.apply("  foo  ", NO_TRIM));
    }

    @Test
    void apply_noBlankToNull_preservesEmptyString() {
        assertEquals("", StringNormalizer.apply("  ", NO_BLANK_TO_NULL));
    }

    @Test
    void apply_nullInput_returnsNull() {
        assertNull(StringNormalizer.apply(null, DEFAULT));
    }

    @Test
    void normalize_appliesToAllStringFields() {
        TestDto dto = new TestDto("  x  ", "  ");
        StringNormalizer.normalize(dto, DEFAULT);

        assertEquals("x", dto.a);
        assertNull(dto.b);
    }

    @Test
    void normalize_nullObject_doesNothing() {
        assertDoesNotThrow(() -> StringNormalizer.normalize(null, DEFAULT));
    }

    @Test
    void normalize_nullConfig_doesNothing() {
        TestDto dto = new TestDto("  x  ");
        StringNormalizer.normalize(dto, null);
        assertEquals("  x  ", dto.a);
    }

    /** Mutable DTO - records have final fields and can't be modified via reflection */
    static class TestDto {
        String a;
        String b;

        TestDto(String a) {
            this.a = a;
            this.b = null;
        }

        TestDto(String a, String b) {
            this.a = a;
            this.b = b;
        }
    }
}
