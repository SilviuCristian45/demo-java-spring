package com.example.demo.Filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RequestLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        logger.info("Incoming request: method={} uri={} headers={}",
                req.getMethod(), req.getRequestURI(), req.getHeaderNames());

        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Exception in filter chain", e);
            throw e;
        }
    }
}

