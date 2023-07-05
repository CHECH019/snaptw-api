package com.xdev.snaptw.security.jwt;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.xdev.snaptw.exceptions.InvalidJwtSubjectException;
import com.xdev.snaptw.exceptions.NoTokenProvidedException;
import static com.xdev.snaptw.util.Const.BASE_URL;

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

        if(isAuthenticationNonRequired(request)){
            filterChain.doFilter(request, response);
            return;
        }
        final var jwt = getJwt(request);
        final var username = getUsername(jwt);

        try{
            final var userDetails = userDetailsService.loadUserByUsername(username);
            final var authToken = getAuthToken(userDetails, request);
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }catch(UsernameNotFoundException e){
            throw new InvalidJwtSubjectException("Token's subject doesn't match any user");
        }

        filterChain.doFilter(request, response);
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

    boolean isAuthenticationNonRequired(HttpServletRequest request){
        final var endpoint = request.getRequestURI()
                                    .replaceAll(BASE_URL, "");
        var authenticationNoRequired = List.of("/auth/login","/auth/signup");
        return  authenticationNoRequired.contains(endpoint);
    }
}