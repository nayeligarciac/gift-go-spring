package com.giftandgo.service;

import com.giftandgo.error.BadRequestException;
import com.giftandgo.model.IPGeolocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IpGeolocationServiceImplTest {

    IpGeolocationService ipGeolocationService;
    @Mock
    RestClient restClient;

    @BeforeEach
    void setup() {
        ipGeolocationService = new IpGeolocationServiceImpl(restClient);
    }

    @Test
    void getIpGeolocation(){
        String ip = "24.48.0.1";
        IPGeolocation ipGeolocation = mockRestClient(ip, "success");

        IPGeolocation response = ipGeolocationService.getIpGeolocation(ip);
        assertEquals(ipGeolocation, response);
    }

    @Test
    void getIpGeolocation_whenRequestIsNosSuccess(){
        String ip = "24.48.0.1";
        mockRestClient(ip, "fail");

        IPGeolocation ipGeolocation = ipGeolocationService.getIpGeolocation(ip);
        assertNull(ipGeolocation);
    }

    private IPGeolocation mockRestClient(String ip, String responseStatus) {
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("http://ip-api.com/json/{ip}", ip))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        IPGeolocation ipGeolocation = mock(IPGeolocation.class);
        when(ipGeolocation.status()).thenReturn(responseStatus);
        when(responseSpec.body(IPGeolocation.class)).thenReturn(ipGeolocation);
        return ipGeolocation;
    }

}