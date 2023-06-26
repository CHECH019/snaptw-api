package com.xdev.snaptw.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xdev.snaptw.apirequest.AuthenticationRequest;
import com.xdev.snaptw.apiresponse.Response;
import com.xdev.snaptw.apiresponse.TokenResponse;
import com.xdev.snaptw.user.User;
import static com.xdev.snaptw.util.Const.BASE_URL;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(BASE_URL+"/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;
    
    @PostMapping("/signup")
    public ResponseEntity<Response> register(@RequestBody User u){
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(service.register(u));
    }

    @GetMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody AuthenticationRequest request){
        return ResponseEntity
            .ok(service.authenticate(request));
    }
}
