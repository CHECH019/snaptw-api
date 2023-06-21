package com.xdev.snaptw.user;

import lombok.Builder;

@Builder
public record UserDTO(
    String name,
    String lastName,
    String email,
    String username
){}