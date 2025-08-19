package com.dontgoback.dontgo.interserver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class InternalApiIpFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String remoteAddr = request.getRemoteAddr();

        if (uri.startsWith("/test/internal/") && !remoteAddr.equals("127.0.0.1")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden IP: " + remoteAddr);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
