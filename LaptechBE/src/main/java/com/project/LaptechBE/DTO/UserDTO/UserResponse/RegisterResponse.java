package com.project.LaptechBE.DTO.UserDTO.UserResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterResponse {
    private String status;
    private String message;
    private Object data;
    private String token;
}
