package com.xdev.snaptw.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank(message = "NAME should not be empty")
    String name,

    @NotBlank(message = "LAST NAME should not be empty")
    String lastName,

    @NotBlank(message = "EMAIL should not be empty")
    @Email
    String email,

    @NotBlank(message = "USERNAME should not be empty")
    String username,

    @NotBlank(message = "PASSWORD should not be empty")
    String password
){}
