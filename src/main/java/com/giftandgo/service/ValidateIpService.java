package com.giftandgo.service;

import com.giftandgo.model.IPGeolocation;

public interface ValidateIpService {
    void validateIp(String ip, IPGeolocation ipGeolocation);
}
