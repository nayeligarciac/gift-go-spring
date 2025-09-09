package com.giftandgo.service;

import com.giftandgo.error.BadRequestException;
import com.giftandgo.model.IPGeolocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class IpGeolocationService {

    private final RestClient restClient;

    @Autowired
    public IpGeolocationService(RestClient restClient) {
        this.restClient = restClient;
    }

    public IPGeolocation getIpGeolocation(String ip){
        IPGeolocation ipGeolocation =  restClient.get()
                .uri("http://ip-api.com/json/{ip}", ip)
                .retrieve()
                .body(IPGeolocation.class);

        if(ipGeolocation == null ||
                ipGeolocation.status() == null ||
                ipGeolocation.status().equals("fail")){
            throw new BadRequestException("There is a problem with the IP " + ip);
        }

        return ipGeolocation;
    }
}
