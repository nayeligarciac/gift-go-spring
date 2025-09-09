package com.giftandgo.component;

import com.giftandgo.aspect.LocalStore;
import com.giftandgo.error.BlockedRequestException;
import com.giftandgo.model.IPGeolocation;
import com.giftandgo.model.LogEntry;
import com.giftandgo.service.IpGeolocationService;
import com.giftandgo.service.ValidateIpService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
public class IpFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(IpFilter.class);
    private final ValidateIpService validateIpService;
    private final IpGeolocationService ipGeolocationService;

    @Autowired
    public IpFilter(ValidateIpService validateIpService, IpGeolocationService ipGeolocationService) {
        this.validateIpService = validateIpService;
        this.ipGeolocationService = ipGeolocationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String ip = getClientIpAddress(request);
        logger.info("Client ip address {}", ip);
        try {
            IPGeolocation ipGeolocation = ipGeolocationService.getIpGeolocation(ip);
            validateIpService.validateIp(ip, ipGeolocation);
            createLogEntry(ipGeolocation, ip, request.getRequestURI());
        } catch(BlockedRequestException e){
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    e.getLocalizedMessage());
        }

        chain.doFilter(request, response);
    }

    private static String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    private void createLogEntry(IPGeolocation ipGeolocation, String ip, String requestUri) {
        String countryCode = null;
        String ipProvider = null;
        if(ipGeolocation != null){
            countryCode = ipGeolocation.countryCode();
            ipProvider = ipGeolocation.isp();
        }

        LogEntry logEntry = new LogEntry();
        logEntry.setId(UUID.randomUUID().toString());
        logEntry.setCountryCode(countryCode);
        logEntry.setIpAddress(ip);
        logEntry.setIpProvider(ipProvider);
        logEntry.setRequestUri(requestUri);
        LocalStore.setLogEntry(logEntry);
    }
}
