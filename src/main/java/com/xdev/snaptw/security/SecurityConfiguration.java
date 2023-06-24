package com.xdev.snaptw.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.xdev.snaptw.security.jwt.ExceptionHandlerFilter;
import com.xdev.snaptw.security.jwt.JwtAuthenticationFilter;
import com.xdev.snaptw.util.Const;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .csrf(CsrfConfigurer<HttpSecurity>::disable)
                .authorizeHttpRequests(
                    authorizeHttpRequests -> authorizeHttpRequests
                            .requestMatchers(Const.BASE_URL+"/auth/**")
                            .permitAll()
                            .anyRequest()
                            .authenticated())
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(
                    exceptionHandlerFilter,
                     JwtAuthenticationFilter.class);
        return httpSecurity.build();
    }
}   
