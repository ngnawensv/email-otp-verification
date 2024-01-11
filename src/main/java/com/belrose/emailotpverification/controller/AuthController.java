package com.belrose.emailotpverification.controller;

import com.belrose.emailotpverification.dto.requests.RegisterRequest;
import com.belrose.emailotpverification.dto.responses.RegisterResponse;
import com.belrose.emailotpverification.model.Users;
import com.belrose.emailotpverification.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UsersService usersService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest){
        RegisterResponse registerResponse = usersService.register(registerRequest);
        return  ResponseEntity.status(HttpStatus.CREATED).body(registerResponse);
    }
    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam String email, @RequestParam String otp){
        try {
            usersService.verify(email, otp);
            return  ResponseEntity.status(HttpStatus.OK).body("User verified successfully");
        }catch (Exception ex){
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email,@RequestParam String password){
        Users users = usersService.login(email, password);
        return new ResponseEntity<>(users,HttpStatus.OK);
    }
}
