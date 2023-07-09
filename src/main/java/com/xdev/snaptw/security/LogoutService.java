package com.xdev.snaptw.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xdev.snaptw.apiresponse.Response;
import com.xdev.snaptw.exceptions.InvalidTokenException;
import com.xdev.snaptw.exceptions.NoTokenProvidedException;
import com.xdev.snaptw.token.TokenDAO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenDAO tokenDAO;

    @Override
    public void logout(
        HttpServletRequest request, 
        HttpServletResponse response, 
        Authentication authentication
    ) {
        var jwt = getJwt(request);
        var storedToken = tokenDAO.findByToken(jwt)
            .orElseThrow(()-> new InvalidTokenException("Invalid token"));
        storedToken.setRevoked(true);
        tokenDAO.save(storedToken);
        var apiResponse = new Response("Logout success");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        try {
            new ObjectMapper().writeValue(response.getOutputStream(),apiResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getJwt(HttpServletRequest request){
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(t -> t.startsWith("Bearer"))
                .map(t -> t.substring(7))
                .orElseThrow(() -> new NoTokenProvidedException("No bearer token provided"));
    }
    
}
