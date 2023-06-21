package com.xdev.snaptw.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User,Long>{
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByEmail(String email);
}
