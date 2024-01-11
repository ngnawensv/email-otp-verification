package com.belrose.emailotpverification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class Users {
    @Id
    private String id;
    private String userName;
    private String email;
    private String password;
    @Field(write = Field.Write.ALWAYS) //display the field even it is null in mongodb
    private String otp;
    private boolean verified;

}
