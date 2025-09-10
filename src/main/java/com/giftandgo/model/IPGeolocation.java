package com.giftandgo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IPGeolocation(
    String status,
    String countryCode,
    String isp
) {
}
