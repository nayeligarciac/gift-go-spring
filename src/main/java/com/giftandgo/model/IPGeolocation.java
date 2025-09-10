package com.giftandgo.model;

public record IPGeolocation(
    String status,
    String countryCode,
    String isp
) {
}
