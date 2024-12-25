package com.project.LaptechBE.services.IServices;

import com.project.LaptechBE.DTO.UserDTO.UserDTO;
import com.project.LaptechBE.DTO.UserDTO.UserRequest.LoginRequest;
import com.project.LaptechBE.DTO.UserDTO.UserRequest.RegisterRequest;
import com.project.LaptechBE.models.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Map;

public interface IUserService {
    public Object RegisterUser(RegisterRequest registerRequest);

    public Object LoginUser(LoginRequest loginRequest);

    public Object UpdateUser(ObjectId id, UserDTO userdto);

    public Object DeleteUser(String id);

    public Object GetDetailsUser(String id);

    public Object getAllUsers();

    Object getUsers(Query query);

    Object forgotPassword(String email);

    Object verifyEmail(String email,String name,String password);
}
