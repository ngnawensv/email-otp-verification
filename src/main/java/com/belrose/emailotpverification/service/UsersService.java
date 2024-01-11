package com.belrose.emailotpverification.service;

import com.belrose.emailotpverification.dto.requests.RegisterRequest;
import com.belrose.emailotpverification.dto.responses.RegisterResponse;
import com.belrose.emailotpverification.model.Users;

public interface UsersService {
    RegisterResponse register(RegisterRequest registerRequest);
    void verify(String email,String otp);
    Users login(String email,String password);
}
