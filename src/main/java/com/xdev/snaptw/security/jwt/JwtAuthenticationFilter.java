package com.xdev.snaptw.security.jwt;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.xdev.snaptw.exceptions.NoTokenProvidedException;
import com.xdev.snaptw.util.Const;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, 
        HttpServletResponse response, 
        FilterChain filterChain
    )throws ServletException, IOException {

        final String requestURI = request.getRequestURI()
                                    .replaceAll(Const.BASE_URL, "");

        if(isAuthenticationNonRequired(requestURI)){
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = getJwt(request);
        final String username = getUsername(jwt);

        if(contextHolderHasNoAuthentication()){
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if(jwtService.isJwtValid(jwt,userDetails)){
                UsernamePasswordAuthenticationToken authToken = getAuthToken(userDetails, request);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean contextHolderHasNoAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private UsernamePasswordAuthenticationToken getAuthToken(
        UserDetails userDetails, 
        HttpServletRequest request
    ){

        UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities()
                    );
        authToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        return authToken;
    }

    private String getJwt(HttpServletRequest request){
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(t -> t.startsWith("Bearer"))
                .map(t -> t.substring(7))
                .orElseThrow(() -> new NoTokenProvidedException("No bearer token provided"));
    }
    
    private String getUsername(String jwt){
        return jwtService.extractUsername(jwt)
                    .orElseThrow(()-> new JwtException(
                        "Token provided has no subject"));
    }

    boolean isAuthenticationNonRequired(String uri){
        List<String> authenticationNoRequired = List.of("/auth/login","/auth/signup");
        return authenticationNoRequired.contains(uri);
    }
}