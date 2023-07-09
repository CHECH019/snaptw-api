package com.xdev.snaptw.security.jwt;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.xdev.snaptw.exceptions.InvalidTokenException;
import com.xdev.snaptw.exceptions.NoTokenProvidedException;
import com.xdev.snaptw.token.TokenDAO;

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
    private final TokenDAO tokenDAO;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, 
        HttpServletResponse response, 
        FilterChain filterChain
    )throws ServletException, IOException {

        if(isAuthenticationNonRequired(request)){
            filterChain.doFilter(request, response);
            return;
        }
        final var jwt = getJwt(request);
        final var username = getUsername(jwt);
        validateToken(jwt);

        try{
            final var userDetails = userDetailsService.loadUserByUsername(username);
            final var authToken = getAuthToken(userDetails, request);
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }catch(UsernameNotFoundException e){
            throw new InvalidTokenException("Token's subject doesn't match any user");
        }

        filterChain.doFilter(request, response);
    }

    private void validateToken(final String jwt) {
        var optionalRefreshToken = tokenDAO.findByToken(jwt);
        if(optionalRefreshToken.isPresent())
            throw new InvalidTokenException("Token invalid, revoked or expired");
    }

    private UsernamePasswordAuthenticationToken getAuthToken(
        UserDetails userDetails, 
        HttpServletRequest request
    ){

        var authToken = new UsernamePasswordAuthenticationToken(
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

    boolean isAuthenticationNonRequired(HttpServletRequest request){
        final var endpoint = request.getRequestURI();
        return  endpoint.contains("/auth");
    }
}