package com.giftandgo.service;

import com.giftandgo.error.BlockedRequestException;
import com.giftandgo.error.IpFromBlockedCloudProviderException;
import com.giftandgo.model.IPGeolocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ValidateIpServiceImpl implements ValidateIpService {

    private final CloudIpProviderService cloudIpProviderService;
    private final Set<String> countryBlocked = Set.of("ES", "CN", "US");

    @Autowired
    public ValidateIpServiceImpl(CloudIpProviderService cloudIpProviderService) {
        this.cloudIpProviderService = cloudIpProviderService;
    }

    @Override
    public void validateIp(String ip, IPGeolocation ipGeolocation) {

        if(ipGeolocation != null) {
            if (countryBlocked.contains(ipGeolocation.countryCode())) {
                throw new BlockedRequestException("This request belong from blocked country");
            }
        }

        try {
            cloudIpProviderService.validateIpCloudProvider(ip);
        } catch (IpFromBlockedCloudProviderException e){
            throw new BlockedRequestException(e.getMessage(), e);
        }

    }

}
