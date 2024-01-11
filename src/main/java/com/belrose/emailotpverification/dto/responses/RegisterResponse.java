package com.belrose.emailotpverification.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponse {
    private String userName;
    private String email;
}
