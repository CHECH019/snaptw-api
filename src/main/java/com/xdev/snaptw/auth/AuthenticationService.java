package com.xdev.snaptw.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.xdev.snaptw.apirequest.AuthenticationRequest;
import com.xdev.snaptw.apirequest.RegisterRequest;
import com.xdev.snaptw.apiresponse.Response;
import com.xdev.snaptw.apiresponse.TokenResponse;
import com.xdev.snaptw.security.jwt.JwtService;
import com.xdev.snaptw.user.Role;
import com.xdev.snaptw.user.User;
import com.xdev.snaptw.user.UserDAO;
import com.xdev.snaptw.util.ObjectValidator;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserDAO userDAO;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final ObjectValidator validator;

    public Response register(RegisterRequest u){
        validator.validate(u);
        if(isEmailInUse(u.email())){
            throw new EntityExistsException("The specified email is already in use");
        }
        if(isUsernameInUse(u.username())){
            throw new EntityExistsException("The specified username is already in use");
        }
        var pass = encoder.encode(u.password());
        User user = User
                        .builder()
                        .name(u.name())
                        .lastName(u.lastName())
                        .username(u.username())
                        .email(u.email())
                        .password(pass)
                        .role(Role.USER)
                        .build();
        userDAO.save(user);
        return new Response("User created succesfully");

    }

    public TokenResponse authenticate(AuthenticationRequest request){
        validator.validate(request);
        final var username = request.username();
        final var password = request.password();
        final var authToken = new UsernamePasswordAuthenticationToken(username,password);
        final var user = (User) authManager
                .authenticate(authToken)
                .getPrincipal();

        final var token = jwtService.generateToken(user);

        return new TokenResponse(token);
    }

    private boolean isUsernameInUse(String username) {
        return userDAO.findUserByUsername(username).isPresent();
    }

    public boolean isEmailInUse(String email){
        return userDAO.findUserByEmail(email).isPresent();
    }
}
