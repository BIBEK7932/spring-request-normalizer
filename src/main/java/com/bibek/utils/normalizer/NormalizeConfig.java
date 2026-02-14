package com.bibek.utils.normalizer;

/**
 * Configuration for string normalization.
 */
public record NormalizeConfig(boolean trim, boolean blankToNull, boolean collapseSpaces) {}
