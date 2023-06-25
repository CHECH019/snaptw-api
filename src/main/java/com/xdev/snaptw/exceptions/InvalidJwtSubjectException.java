package com.xdev.snaptw.exceptions;

import io.jsonwebtoken.JwtException;

public class InvalidJwtSubjectException extends JwtException{

    public InvalidJwtSubjectException(String message) {
        super(message);
    }
    
}
