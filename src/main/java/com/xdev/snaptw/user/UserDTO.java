package com.xdev.snaptw.user;

import lombok.Builder;

@Builder
public record UserDTO(
    String name,
    String lastName,
    String email,
    String username
){
    public UserDTO(User u){
        this(u.getName(), u.getLastName(), u.getEmail(), u.getUsername());
    }
}