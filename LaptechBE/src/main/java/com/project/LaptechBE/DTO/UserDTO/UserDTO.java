package com.project.LaptechBE.DTO.UserDTO;

import com.project.LaptechBE.models.submodels.submodelsUser.UserAddress;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Validated
public class UserDTO {
    private String name;
    private Date birthDate;
    @Email
    private String email;
    private String password;
    private String avatar;
    private String phone;
    private ArrayList<UserAddress> addresses = new ArrayList<>();
}
