package com.octopus.demo.common.util;

/**
 * Configuration record for AEDUtils settings.
 * Fields may be null to indicate partial update (only non-null fields are applied).
 */
public record AEDConfig(String secretKey, String ivParameter) {}