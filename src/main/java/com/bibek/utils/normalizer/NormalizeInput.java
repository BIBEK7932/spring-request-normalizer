package com.bibek.utils.normalizer;

import java.lang.annotation.*;

/**
 * Enables request body string field normalization.
 * <p>
 * Use like {@code @Getter} / {@code @Setter} — apply once and it affects all String fields:
 * <ul>
 *   <li><b>On class</b> — normalizes all String fields in the DTO</li>
 *   <li><b>On parameter</b> — normalizes the request body when DTO cannot be annotated</li>
 * </ul>
 * <p>
 * Supported normalizations:
 * <ul>
 *   <li><b>trim</b> — trim leading/trailing whitespace</li>
 *   <li><b>blankToNull</b> — convert blank or empty strings to null</li>
 *   <li><b>collapseSpaces</b> — collapse multiple consecutive spaces to a single space</li>
 * </ul>
 * <p>
 * Future options you could add:
 * <ul>
 *   <li><b>lowercase</b> — convert to lowercase</li>
 *   <li><b>uppercase</b> — convert to uppercase</li>
 *   <li><b>removeControlChars</b> — strip control characters (e.g. ASCII 0-31)</li>
 *   <li><b>normalizeUnicode</b> — NFC/NFD Unicode normalization</li>
 *   <li><b>stripAccents</b> — remove diacritics (é → e)</li>
 *   <li><b>maxLength</b> — truncate strings to a maximum length</li>
 * </ul>
 *
 * <p>Examples:</p>
 * <pre>{@code
 * // On class — all String fields normalized (like @Getter on class)
 * @NormalizeInput
 * public class CreateUserRequest {
 *     private String email;   // "  " -> null, " foo " -> "foo"
 *     private String name;
 * }
 *
 * // On parameter — when DTO is from a library you can't annotate
 * @PostMapping
 * public void create(@RequestBody @NormalizeInput CreateUserRequest req) {}
 *
 * // With options
 * @NormalizeInput(trim = true, blankToNull = true, collapseSpaces = true)
 * public class SearchRequest {
 *     private String query;   // "  hello    world  " -> "hello world"
 * }
 * }</pre>
 */
@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NormalizeInput {

    /**
     * Trim leading and trailing whitespace from string fields.
     */
    boolean trim() default true;

    /**
     * Convert blank or empty strings to null.
     */
    boolean blankToNull() default true;

    /**
     * Collapse multiple consecutive spaces to a single space.
     */
    boolean collapseSpaces() default false;
}
