package com.giftandgo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.giftandgo.error.IpFromBlockedCloudProviderException;
import com.giftandgo.model.AzureIpRangeFile;
import com.giftandgo.model.AzureServiceTag;
import com.giftandgo.model.AzureServiceTagProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CloudIpProviderServiceImplTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ObjectMapper objectMapper;
    private CloudIpProviderServiceImpl cloudIpProviderService;

    @Test
    void validateIpCloudProvider() throws Exception {
        mockAwsApiSuccess();
        mockAzureApiSuccess();
        cloudIpProviderService = new CloudIpProviderServiceImpl(restTemplate, objectMapper);

        cloudIpProviderService.validateIpCloudProvider("169.254.169.254");
    }

    @Test
    void validateIpCloudProvider_blockAWSIp() throws Exception {
        mockAwsApiSuccess();
        mockAzureApiSuccess();
        cloudIpProviderService = new CloudIpProviderServiceImpl(restTemplate, objectMapper);

        IpFromBlockedCloudProviderException ex =  assertThrows(IpFromBlockedCloudProviderException.class,
                ()-> cloudIpProviderService.validateIpCloudProvider("3.5.140.1"));

        assertEquals("Ip belong from cloud provider AWS", ex.getMessage());
    }

    @Test
    void validateIpCloudProvider_blockAzureIp() throws Exception {
        mockAwsApiSuccess();
        mockAzureApiSuccess();
        cloudIpProviderService = new CloudIpProviderServiceImpl(restTemplate, objectMapper);

        IpFromBlockedCloudProviderException ex =  assertThrows(IpFromBlockedCloudProviderException.class,
                ()-> cloudIpProviderService.validateIpCloudProvider("20.190.128.1"));

        assertEquals("Ip belong from cloud provider AZURE", ex.getMessage());
    }

    @Test
    void validateIpCloudProvider_blockGCPIp() throws Exception {
        mockAwsApiSuccess();
        mockAzureApiSuccess();
        cloudIpProviderService = new CloudIpProviderServiceImpl(restTemplate, objectMapper);

        IpFromBlockedCloudProviderException ex =  assertThrows(IpFromBlockedCloudProviderException.class,
                ()-> cloudIpProviderService.validateIpCloudProvider("34.80.0.1"));

        assertEquals("Ip belong from cloud provider GCP", ex.getMessage());
    }


    private void mockAwsApiSuccess() {
        Map<String, Object> awsResponse = new HashMap<>();
        awsResponse.put("prefixes", List.of(
                Map.of("ip_prefix", "3.5.140.0/22")
        ));
        awsResponse.put("ipv6_prefixes", List.of(
                Map.of("ipv6_prefix", "2600:1f18::/32")
        ));
        when(restTemplate.getForObject(contains("amazonaws.com"), eq(Map.class)))
                .thenReturn(awsResponse);
    }

    private void mockAzureApiSuccess() throws Exception {
        String fakeAzureJson = "{\"values\": [{\"name\": \"AzureCloud\", \"properties\": {\"addressPrefixes\": [\"20.190.128.0/18\"]}}]}";
        AzureIpRangeFile fakeAzureData = new AzureIpRangeFile(
                List.of(new AzureServiceTag("AzureCloud", new AzureServiceTagProperties(List.of("20.190.128.0/18"))))
        );
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(fakeAzureJson);
        when(objectMapper.readValue(fakeAzureJson, AzureIpRangeFile.class)).thenReturn(fakeAzureData);
    }

}