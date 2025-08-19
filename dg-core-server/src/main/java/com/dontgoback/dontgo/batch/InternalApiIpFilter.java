package com.dontgoback.dontgo.batch;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Order(1)
public class InternalApiIpFilter extends OncePerRequestFilter {
    @Value("${custom.allowed-ips}")
    private String allowedIpStr;

    private Set<String> allowedIps;

    @PostConstruct
    public void init() {
        allowedIps = Arrays.stream(allowedIpStr.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String remoteAddr = request.getRemoteAddr();
        
        // 내부 통신만 허용
        if (uri.startsWith("/test/internal/") && !allowedIps.contains(remoteAddr)) {
            log.warn("Blocked internal API access from IP: {}", remoteAddr);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden IP: " + remoteAddr);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
