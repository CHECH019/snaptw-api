package com.xdev.snaptw.apirequest;

public record RegisterRequest(
    String name,
    String lastName,
    String email,
    String username,
    String password
){}
