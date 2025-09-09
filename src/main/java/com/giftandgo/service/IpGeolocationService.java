package com.giftandgo.service;

import com.giftandgo.model.IPGeolocation;

public interface IpGeolocationService {
    IPGeolocation getIpGeolocation(String ip);
}
