package com.project.LaptechBE.DTO.UserDTO.UserRequest;

import com.project.LaptechBE.DTO.UserDTO.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {
    private String id;
    private UserDTO data;
}
