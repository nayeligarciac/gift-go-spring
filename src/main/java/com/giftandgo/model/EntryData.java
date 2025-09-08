package com.giftandgo.model;


public record EntryData(
        String uuid,
        String id,
        String name,
        String likes,
        String transport,
        Double averageSpeed,
        Double topSpeed
) {
}
