package com.xdev.snaptw.user;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xdev.snaptw.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserDAO dao;

    @Override
    public List<UserDTO> getAllUsers() {
        return dao.findAll()
            .stream()
            .map(this::userDTOSupplier)
            .toList();
    }

    @Override
    public UserDTO getUserById(Long id) {
        return dao.findById(id)
            .map(this::userDTOSupplier)
            .orElseThrow(()-> new ResourceNotFoundException(
                "Couldn't find any user with id: "+id
            ));
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        return dao.findUserByUsername(username)
            .map(this::userDTOSupplier)
            .orElseThrow(()->new ResourceNotFoundException(
                "Couldn't find any user with username: "+username
            ));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return dao.findUserByEmail(email)
            .map(this::userDTOSupplier)
            .orElseThrow(()-> new ResourceNotFoundException(
                "Couldn't find any user with email: "+email
            ));
    }

    private UserDTO userDTOSupplier(User u){
        return UserDTO.builder()
                .name(u.getName())
                .lastName(u.getLastName())
                .email(u.getEmail())
                .username(u.getUsername())
                .build();
    }
    
}
