package com.giftandgo.service;

import com.giftandgo.model.IPGeolocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class IpGeolocationServiceImpl implements IpGeolocationService {

    private static final Logger logger = LoggerFactory.getLogger(IpGeolocationServiceImpl.class);
    private final RestClient restClient;

    @Autowired
    public IpGeolocationServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public IPGeolocation getIpGeolocation(String ip){
        IPGeolocation ipGeolocation =  restClient.get()
                .uri("http://ip-api.com/json/{ip}", ip.trim())
                .retrieve()
                .body(IPGeolocation.class);

        if(ipGeolocation == null ||
                ipGeolocation.status() == null ||
                ipGeolocation.status().equals("fail")){
            logger.error("there is an error in ip {} -> {} ", ip, ipGeolocation);
            return null;
        }

        return ipGeolocation;
    }
}
