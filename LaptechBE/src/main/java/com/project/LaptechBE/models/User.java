package com.project.LaptechBE.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "users")
@Validated
public class User {
    @Id
    private String id;

    private String name;
    private String birthDate;
    private String email;
    private String password;
    private String avatar;
    private String phone;
    private String isAdmin;
    private ArrayList<Address> addresses;
}
