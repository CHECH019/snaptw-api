package com.xdev.snaptw.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.xdev.snaptw.apiresponse.Response;
import com.xdev.snaptw.apiresponse.TokenResponse;
import com.xdev.snaptw.exceptions.InvalidJwtSubjectException;
import com.xdev.snaptw.exceptions.InvalidTokenException;
import com.xdev.snaptw.exceptions.NoTokenProvidedException;
import com.xdev.snaptw.security.jwt.JwtService;
import com.xdev.snaptw.token.Token;
import com.xdev.snaptw.token.TokenDAO;
import com.xdev.snaptw.token.TokenType;
import com.xdev.snaptw.user.Role;
import com.xdev.snaptw.user.User;
import com.xdev.snaptw.user.UserDAO;
import com.xdev.snaptw.util.ObjectValidator;

import io.jsonwebtoken.JwtException;
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
    private final TokenDAO tokenDAO;

    public Response register(RegisterRequest u){
        validator.validate(u);
        if(isEmailInUse(u.email())){
            throw new EntityExistsException("The specified email is already in use");
        }
        if(isUsernameInUse(u.username())){
            throw new EntityExistsException("The specified username is already in use");
        }
        var encodedPassword = encoder.encode(u.password());
        User user = User
                        .builder()
                        .name(u.name())
                        .lastName(u.lastName())
                        .username(u.username())
                        .email(u.email())
                        .password(encodedPassword)
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

        final var accesstoken = jwtService.generateAccessToken(user);
        final var refreshToken = jwtService.generateRefreshToken(user);
        var tokenOptional = tokenDAO.findTokenByUserId(user.getId());
        Token refreshTokenEntity;
        if(tokenOptional.isPresent()){
            refreshTokenEntity = tokenOptional.get();
            refreshTokenEntity.setToken(refreshToken);
            refreshTokenEntity.setRevoked(false);
        }else{
            refreshTokenEntity = buildTokenEntity(user,refreshToken,TokenType.REFRESH);
        }
        tokenDAO.save(refreshTokenEntity);

        return new TokenResponse(accesstoken,refreshToken);
    }

    private Token buildTokenEntity(User user, String token, TokenType tokenType){
        return Token.builder()
            .user(user)
            .revoked(false)
            .token(token)
            .tokenType(tokenType)
            .build();
    }

    private boolean isUsernameInUse(String username) {
        return userDAO.findUserByUsername(username).isPresent();
    }

    public boolean isEmailInUse(String email){
        return userDAO.findUserByEmail(email).isPresent();
    }

    public TokenResponse refresh(String authorization) {
        if(!authorization.startsWith("Bearer")) throw new NoTokenProvidedException("No berarer token provided");
        final var refreshToken = authorization.substring(7);
        final var username = jwtService.extractUsername(refreshToken)
            .orElseThrow(()-> new JwtException("Token provided has no subject"));
        final var user =  userDAO.findUserByUsername(username)
            .orElseThrow(()-> new InvalidJwtSubjectException("JWT subject doesn't match any user"));

        final var isValidToken = tokenDAO.findByToken(refreshToken)
            .map(token-> !token.isRevoked())
            .orElseThrow(()->new InvalidTokenException("Invalid Token"));
        if(!isValidToken) throw new InvalidTokenException("The token has been revoked");
        final var accessToken = jwtService.generateAccessToken(user);

        return new TokenResponse(accessToken,refreshToken);
    }
}
