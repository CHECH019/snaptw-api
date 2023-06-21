package com.xdev.snaptw.user;

import java.util.List;

public interface UserService {
    public List<UserDTO> getAllUsers();
    public UserDTO getUserById(Long id);
    public UserDTO getUserByUsername(String username);
    public UserDTO getUserByEmail(String email);
}
