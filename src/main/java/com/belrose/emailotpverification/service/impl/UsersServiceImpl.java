package com.belrose.emailotpverification.service.impl;

import com.belrose.emailotpverification.dto.requests.RegisterRequest;
import com.belrose.emailotpverification.dto.responses.RegisterResponse;
import com.belrose.emailotpverification.model.Users;
import com.belrose.emailotpverification.repository.UsersRepository;
import com.belrose.emailotpverification.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final EmailService emailService;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
       Users existingUser = usersRepository.findByEmail(registerRequest.getEmail()).block();
       log.info("UsersServiceImpl:register: existing user {}",existingUser);
       if(existingUser!=null && (existingUser.getEmail().equals(registerRequest.getEmail()) || existingUser.isVerified())){
           log.error("UsersServiceImpl:register: User already registered and/or verified {}",existingUser);
           throw new RuntimeException("User already registered and/or verified");
       }

        String otp = generateOTP();
        log.info("UsersServiceImpl:register: OTP generated {}",otp);
        Users users = Users.builder()
                .userName(registerRequest.getUserName())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .otp(otp)
                .build();

       Users saveUser = usersRepository.save(users).block();

        assert saveUser != null;

        sendOtpToEmail(saveUser.getEmail(),otp);

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
        //Generates a 6-digit OTP
        Random random = new Random();
       int optValue =  100_000 + random.nextInt(900_000);
       return String.valueOf(optValue);
    }

    private void sendOtpToEmail(String email, String otp){
        String subject = "Email Verification";
        String body = String.format("Your verification OTP is : %s ",otp);
        emailService.sendEmail(email,subject,body);
    }
}
