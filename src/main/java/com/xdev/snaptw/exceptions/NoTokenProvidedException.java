package com.xdev.snaptw.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NoTokenProvidedException extends RuntimeException{

    public NoTokenProvidedException(String message){
        super(message);
    }
}
