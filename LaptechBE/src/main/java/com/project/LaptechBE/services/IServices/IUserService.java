package com.project.LaptechBE.services.IServices;

import com.project.LaptechBE.DTO.UserDTO.RegisterDTO;
import com.project.LaptechBE.models.User;

public interface IUserService {
    public User RegisterUser(RegisterDTO registerDTO);
}
