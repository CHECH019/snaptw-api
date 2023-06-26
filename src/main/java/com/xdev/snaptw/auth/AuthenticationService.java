package com.xdev.snaptw.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.xdev.snaptw.apirequest.AuthenticationRequest;
import com.xdev.snaptw.apiresponse.Response;
import com.xdev.snaptw.apiresponse.TokenResponse;
import com.xdev.snaptw.security.jwt.JwtService;
import com.xdev.snaptw.user.Role;
import com.xdev.snaptw.user.User;
import com.xdev.snaptw.user.UserDAO;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserDAO userDAO;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public Response register(User u){
        if(emailAlreadyInUse(u.getEmail())){
            throw new EntityExistsException("The specified email is already in use");
        }
        if(usernameAlreadyInUse(u.getUsername())){
            throw new EntityExistsException("The specified username is already in use");
        }
        User user = u;
        user.setPassword(encoder.encode(u.getPassword()));
        u.setRole(Role.USER);
        userDAO.save(u);
        return new Response("User created succesfully");

    }

    public TokenResponse authenticate(AuthenticationRequest request){
        String username = request.username();
        String password = request.password();
        UsernamePasswordAuthenticationToken authToken = 
            new UsernamePasswordAuthenticationToken(
                username, 
                password
            );
        User user = (User) authManager
                            .authenticate(authToken)
                            .getPrincipal();
         
        String token = jwtService.generateToken(user);

        return new TokenResponse(token);
    }

    private boolean usernameAlreadyInUse(String username) {
        return userDAO.findUserByUsername(username).isPresent();
    }

    public boolean emailAlreadyInUse(String email){
        return userDAO.findUserByEmail(email).isPresent();
    }
}
