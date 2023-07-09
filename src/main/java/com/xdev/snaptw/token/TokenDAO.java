package com.xdev.snaptw.token;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TokenDAO extends JpaRepository<Token,Long>{
    @Query("""
        SELECT t from Token t 
        JOIN User u ON t.user.id = u.id 
        WHERE u.id = ?1 
    """)
    Optional<Token> findTokenByUserId (Long userId);

    Optional<Token> findByToken(String token);
}
