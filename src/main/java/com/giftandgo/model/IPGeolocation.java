package com.giftandgo.model;

public record IPGeolocation(
    String status,
    String country,
    String countryCode,
    String region,
    String regionName,
    String city,
    String zip,
    String lat,
    String lon,
    String timezone,
    String isp,
    String org,
    String as,
    String query
) {
}
