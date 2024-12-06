package com.project.LaptechBE.services.IServices;

import com.project.LaptechBE.DTO.UserDTO.UserRequest.LoginRequest;
import com.project.LaptechBE.DTO.UserDTO.UserRequest.RegisterRequest;
import com.project.LaptechBE.models.User;

public interface IUserService {
    public Object RegisterUser(RegisterRequest registerRequest);
    public Object LoginUser(LoginRequest loginRequest);
    public Object UpdateUser(String id);
}
