package com.xdev.snaptw.security.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private HandlerExceptionResolver resolver;
    
    public ExceptionHandlerFilter(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try{
            filterChain.doFilter(request, response);
        }catch(Exception e){
            e.printStackTrace();
            resolver.resolveException(request, response, null, e);
        }
    }
    
}
