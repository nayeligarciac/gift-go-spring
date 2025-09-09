package com.giftandgo.component;

import com.giftandgo.error.BlockedRequestException;
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

@Component
@Order(1)
public class IpFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(IpFilter.class);
    private final ValidateIpService validateIpService;

    @Autowired
    public IpFilter(ValidateIpService validateIpService) {
        this.validateIpService = validateIpService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String ip = getClientIpAddress(request);
        logger.info("Client ip address {}", ip);
        try {
            validateIpService.validateIp(ip);
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
}
