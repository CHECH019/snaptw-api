package com.xdev.snaptw.user;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.xdev.snaptw.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService{

    private final UserDAO dao;

    public List<UserDTO> getAllUsers() {
        return dao.findAll()
            .stream()
            .map(UserDTO::new)
            .toList();
    }

    public UserDTO getUserById(Long id) {
        return dao.findById(id)
                .map(UserDTO::new)
                .orElseThrow(()-> new ResourceNotFoundException(
                    "Couldn't find any user with id: "+id
                ));
    }

    public UserDTO getUserByUsername(String username) {
        return dao.findUserByUsername(username)
                .map(UserDTO::new)
                .orElseThrow(()->new ResourceNotFoundException(
                    "Couldn't find any user with username: "+username
                ));
    }

    public UserDTO getUserByEmail(String email) {
        return dao.findUserByEmail(email)
                .map(UserDTO::new)
                .orElseThrow(()-> new ResourceNotFoundException(
                    "Couldn't find any user with email: "+email
                ));
    }

    public UserDTO getCurrentUser() {
        final var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new UserDTO(user);
    }
    
}