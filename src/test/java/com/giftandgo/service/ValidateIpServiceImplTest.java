package com.giftandgo.service;

import com.giftandgo.error.BlockedRequestException;
import com.giftandgo.error.IpFromBlockedCloudProviderException;
import com.giftandgo.model.IPGeolocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidateIpServiceImplTest {

    @Mock
    CloudIpProviderService cloudIpProviderService;
    ValidateIpService validateIpService;

    @BeforeEach
    void setup() {
        validateIpService = new ValidateIpServiceImpl(cloudIpProviderService);
    }

    @Test
    void validateIp(){
        String ip = "24.48.0.1";
        IPGeolocation ipGeolocation = mock(IPGeolocation.class);
        when(ipGeolocation.countryCode()).thenReturn("UK");

        validateIpService.validateIp(ip, ipGeolocation);

        verify(cloudIpProviderService).validateIpCloudProvider(ip);
    }

    @Test
    void validateIp_WhenIpBelongFromBlockedCountries(){
        String ip = "24.48.0.1";
        IPGeolocation ipGeolocation = mock(IPGeolocation.class);
        when(ipGeolocation.countryCode()).thenReturn("US");

        BlockedRequestException ex = assertThrows(BlockedRequestException.class,
                ()->  validateIpService.validateIp(ip, ipGeolocation));

        verify(cloudIpProviderService, never()).validateIpCloudProvider(ip);
        assertEquals("This request belong from blocked country", ex.getMessage());
    }

    @Test
    void validateIp_whenThereIsNoIPGeolocation(){
        String ip = "24.48.0.1";

        validateIpService.validateIp(ip, null);

        verify(cloudIpProviderService).validateIpCloudProvider(ip);
    }

    @Test
    void validateIp_WhenIpBelongFromBlockedCloudProvider(){
        String ip = "24.48.0.1";
        IPGeolocation ipGeolocation = mock(IPGeolocation.class);
        when(ipGeolocation.countryCode()).thenReturn("UK");

        doThrow(new IpFromBlockedCloudProviderException("blocked ip"))
                .when(cloudIpProviderService).validateIpCloudProvider(ip);

        BlockedRequestException ex = assertThrows(BlockedRequestException.class,
                ()->  validateIpService.validateIp(ip, ipGeolocation));

        assertEquals("blocked ip", ex.getMessage());
    }
}