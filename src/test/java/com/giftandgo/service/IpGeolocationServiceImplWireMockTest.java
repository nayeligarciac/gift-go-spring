package com.giftandgo.service;

import com.giftandgo.model.IPGeolocation;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;



@SpringBootTest
public class IpGeolocationServiceImplWireMockTest {

    private WireMockServer wireMockServer;

    @Autowired
    private IpGeolocationService ipGeolocationService;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(
                options()
                        .port(8080));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8080);
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        wireMockServer.stop();
        Thread.sleep(1000);
    }

    @Test
    void getIpGeolocation_success() {
        IPGeolocation expected = new IPGeolocation(
              "success",
                "CA",
                "Le Groupe Videotron Ltee"
        );

        stubFor(get(urlEqualTo("http://ip-api.com/json/24.48.0.1"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                            {
                                              "query": "24.48.0.1",
                                              "status": "success",
                                              "country": "Canada",
                                              "countryCode": "CA",
                                              "region": "QC",
                                              "regionName": "Quebec",
                                              "city": "Montreal",
                                              "zip": "H1K",
                                              "lat": 45.6085,
                                              "lon": -73.5493,
                                              "timezone": "America/Toronto",
                                              "isp": "Le Groupe Videotron Ltee",
                                              "org": "Videotron Ltee",
                                              "as": "AS5769 Videotron Ltee"
                                            }
                                """)));


        IPGeolocation result = ipGeolocationService.getIpGeolocation("24.48.0.1");
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void getIpGeolocation_whenResponseFailed() {

        stubFor(get(urlEqualTo("http://ip-api.com/json/127.0.0.1"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                           {
                                               "query": "127.0.0.1",
                                               "message": "reserved range",
                                               "status": "fail"
                                             }
                                """)));

        IPGeolocation result = ipGeolocationService.getIpGeolocation("127.0.0.1");
        assertNull(result);
    }
}
