package com.belrose.emailotpverification.service.impl;

import com.belrose.emailotpverification.dto.requests.RegisterRequest;
import com.belrose.emailotpverification.dto.responses.RegisterResponse;
import com.belrose.emailotpverification.model.Users;
import com.belrose.emailotpverification.repository.UsersRepository;
import com.belrose.emailotpverification.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final EmailService emailService;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
       Users existingUser = usersRepository.findByEmail(registerRequest.getEmail()).block();
       if(existingUser!=null && existingUser.isVerified()){
           throw new RuntimeException("User Already Registered");
       }
        String otp = generateOTP();
        Users users = Users.builder()
                .userName(registerRequest.getUserName())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .otp(otp)
                .build();
       Users saveUser = usersRepository.save(users).block();
        assert saveUser != null;
        sendVerificationEmail(saveUser.getEmail(),otp);
        return RegisterResponse.builder()
                .userName(users.getUserName())
                .email(users.getEmail())
                .build();
    }

    @Override
    public void verify(String email, String otp) {
       Users users =  usersRepository.findByEmail(email).block();
        if(users == null){
            throw new RuntimeException("User not found");
        }else if(users.isVerified()){
            throw new RuntimeException("User is already verified");
        }else if(otp.equals(users.getOtp())){
            users.setVerified(true);
           usersRepository.save(users).block();
        }else{
            throw new RuntimeException("Internal Server Error");
        }
    }

    @Override
    public Users login(String email, String password) {
        Users byEmail = usersRepository.findByEmail(email).block();
        if(byEmail!=null && byEmail.isVerified() && byEmail.getPassword().equals(password)){
            return byEmail;
        }else {
            throw new RuntimeException("Internal Server Error");
        }
    }

    private String generateOTP(){
        Random random = new Random();
       int optValue =  10000 + random.nextInt(90000);
       return String.valueOf(optValue);
    }

    private void sendVerificationEmail(String email,String otp){
        String subject = "Email Verification";
        String body = String.format("Your verification OTP is : %s ",otp);
        emailService.sendEmail(email,subject,body);
    }
}
