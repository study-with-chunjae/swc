package net.fullstack7.swc.config;


import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Log4j2
@WebFilter(urlPatterns = {"/*"})
public class CustomFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain){
        log.info("do filter");
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String uri = request.getRequestURI();
        if(uri.contains("\\") || uri.contains("{") || uri.contains("}")){
            throw new IllegalArgumentException("Invalid characters in URI");
        }
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw new RuntimeException("Request filtering failed", e);
        }
    }
}
