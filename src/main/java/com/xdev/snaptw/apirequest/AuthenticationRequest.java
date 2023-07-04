package com.xdev.snaptw.apirequest;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
    @NotBlank(message = "USERNAME should not be empty")
    String username, 

    @NotBlank(message = "PASSWORD should not be empty")
    String password
) {}
