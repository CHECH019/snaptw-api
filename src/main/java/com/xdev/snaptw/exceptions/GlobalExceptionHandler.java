package com.xdev.snaptw.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.xdev.snaptw.apiresponse.Response;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.persistence.EntityExistsException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Response> resourceNotFound(ResourceNotFoundException e){
        return getResponse(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<Response> entityExists(EntityExistsException e){
        return getResponse(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(NoTokenProvidedException.class)
    public ResponseEntity<Response> noTokenProvided(NoTokenProvidedException e){
        return getResponse(HttpStatus.FORBIDDEN, e);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Response> badCredentials(BadCredentialsException e){
        return getResponse(HttpStatus.UNAUTHORIZED, e);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Response> expiredToken(ExpiredJwtException e){
        return getResponse(HttpStatus.UNAUTHORIZED, e);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<Response> malformedToken(MalformedJwtException e){
        return getResponse(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Response> defaultException(RuntimeException e){
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new Response(e.getClass().getName()));
    }

    private ResponseEntity<Response> getResponse(HttpStatus status,Exception e){
        return ResponseEntity
            .status(status)
            .body(new Response(e.getMessage()));
    }

}
