package com.giftandgo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.giftandgo.error.IpFromBlockedCloudProviderException;
import com.giftandgo.model.AzureIpRangeFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.apache.commons.net.util.SubnetUtils;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CloudIpProviderServiceImpl implements CloudIpProviderService {

    private static final Logger logger = LoggerFactory.getLogger(CloudIpProviderServiceImpl.class);

    private static final String AWS_IP_RANGES_URL = "https://ip-ranges.amazonaws.com/ip-ranges.json";
    private static final String GCP_DNS_ROOT = "_cloud-netblocks.googleusercontent.com";

    private static final String AZURE_IP_RANGES_URL = "https://download.microsoft.com/download/7/1/D/71D86715-5596-4529-9B13-DA13A5DE5B63/ServiceTags_Public_latest.json";

    private final Set<String> blockedAwsCidrRanges = ConcurrentHashMap.newKeySet();
    private final Set<String> blockedGcpCidrRanges = ConcurrentHashMap.newKeySet();
    private final Set<String> blockedAzureCidrRanges = ConcurrentHashMap.newKeySet();

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public CloudIpProviderServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        loadIpRanges();
    }

    void loadIpRanges() {

        // Use try-catch for each provider so if one fails, the others can still load
        try {
            Set<String> awsRanges = fetchAwsIpRanges();
            blockedAwsCidrRanges.clear();
            blockedAwsCidrRanges.addAll(awsRanges);
            logger.info("Successfully loaded {} IP ranges from AWS.", awsRanges.size());
        } catch (Exception e) {
            logger.error("Failed to load AWS IP ranges.", e);
        }

        try {
            Set<String> gcpRanges = fetchGcpIpRanges();
            blockedGcpCidrRanges.clear();
            blockedGcpCidrRanges.addAll(gcpRanges);
            logger.info("Successfully loaded {} IP ranges from GCP.", gcpRanges.size());
        } catch (Exception e) {
            logger.error("Failed to load GCP IP ranges.", e);
        }

        try {
            Set<String> azureRanges = fetchAzureIpRanges();
            blockedAzureCidrRanges.clear();
            blockedAzureCidrRanges.addAll(azureRanges);
            logger.info("Successfully loaded {} IP ranges from Azure.", azureRanges.size());
        } catch (Exception e) {
            logger.error("Failed to load Azure IP ranges.", e);
        }
    }

    private Set<String> fetchAwsIpRanges() {
        Map<String, Object> awsResponse = restTemplate.getForObject(AWS_IP_RANGES_URL, Map.class);
        if (awsResponse == null) return Collections.emptySet();

        Stream<String> ipv4Prefixes = ((List<Map<String, String>>) awsResponse.get("prefixes"))
                .stream().map(p -> p.get("ip_prefix"));
        Stream<String> ipv6Prefixes = ((List<Map<String, String>>) awsResponse.get("ipv6_prefixes"))
                .stream().map(p -> p.get("ipv6_prefix"));

        return Stream.concat(ipv4Prefixes, ipv6Prefixes).collect(Collectors.toSet());
    }


    private Set<String> fetchGcpIpRanges() throws NamingException {
        Set<String> gcpRanges = new HashSet<>();
        Hashtable<String, String> env = new Hashtable<>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        DirContext dnsContext = new InitialDirContext(env);

        // First, get the list of netblock records from the root record
        Attributes mainAttributes = dnsContext.getAttributes(GCP_DNS_ROOT, new String[]{"TXT"});
        NamingEnumeration<?> mainRecords = mainAttributes.get("TXT").getAll();

        while (mainRecords.hasMore()) {
            String record = (String) mainRecords.next();
            // Records look like "v=spf1 include:_netblocks1.googleusercontent.com ...", so we parse it
            for (String part : record.replace("\"", "").split(" ")) {
                if (part.startsWith("include:")) {
                    String hostname = part.substring("include:".length());
                    // Now, look up the TXT records for this specific hostname
                    Attributes netblockAttributes = dnsContext.getAttributes(hostname, new String[]{"TXT"});
                    NamingEnumeration<?> netblockRecords = netblockAttributes.get("TXT").getAll();
                    while(netblockRecords.hasMore()) {
                        String netblockRecord = (String) netblockRecords.next();
                        for (String netblockPart : netblockRecord.replace("\"", "").split(" ")) {
                            if (netblockPart.startsWith("ip4:") || netblockPart.startsWith("ip6:")) {
                                gcpRanges.add(netblockPart.substring(4)); // remove ip4: or ip6:
                            }
                        }
                    }
                }
            }
        }
        return gcpRanges;
    }

    private Set<String> fetchAzureIpRanges() {
        String jsonResponse = restTemplate.getForObject(AZURE_IP_RANGES_URL, String.class);
        if (jsonResponse == null) return Collections.emptySet();

        try {
            AzureIpRangeFile azureData = objectMapper.readValue(jsonResponse, AzureIpRangeFile.class);
            return azureData.values().stream()
                    .filter(tag -> tag.properties() != null && tag.properties().addressPrefixes() != null)
                    .flatMap(tag -> tag.properties().addressPrefixes().stream())
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Azure IP ranges JSON", e);
        }
    }

    @Override
    public void validateIpCloudProvider(String ipAddress) {
        if (ipAddress == null ||
                blockedAwsCidrRanges.isEmpty() ||
                blockedGcpCidrRanges.isEmpty() ||
                blockedAzureCidrRanges.isEmpty()) {
            return;
        }

        isFromCloudProvider(ipAddress, blockedAwsCidrRanges, "AWS");
        isFromCloudProvider(ipAddress, blockedGcpCidrRanges, "GCP");
        isFromCloudProvider(ipAddress, blockedAzureCidrRanges, "AZURE");

    }

    private void isFromCloudProvider(String ipAddress, Set<String> blockedCidrRanges, String cloudProvider){
        for (String cidr : blockedCidrRanges) {
            try {
                SubnetUtils utils = new SubnetUtils(cidr);
                // Enable inclusive host count to check network and broadcast addresses as well
                utils.setInclusiveHostCount(true);
                if (utils.getInfo().isInRange(ipAddress)) {
                    logger.warn("Blocking request from IP {} because it matches CIDR block {}", ipAddress, cidr);
                    throw new IpFromBlockedCloudProviderException("Ip belong from cloud provider "+ cloudProvider);
                }
            } catch (IllegalArgumentException e) {
                // This can happen for malformed CIDR or IP strings
                logger.trace("Could not check IP {} against CIDR {}. Error: {}", ipAddress, cidr, e.getMessage());
            }
        }
    }
}
