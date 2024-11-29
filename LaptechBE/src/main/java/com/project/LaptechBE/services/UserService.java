package com.project.LaptechBE.services;

import com.project.LaptechBE.DTO.UserDTO.RegisterDTO;
import com.project.LaptechBE.models.User;
import com.project.LaptechBE.repositories.UserRepository;
import com.project.LaptechBE.services.IServices.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;

    @Override
    public User RegisterUser(RegisterDTO registerDTO) {
        User user = User.builder().name(registerDTO.getUsername()).password(registerDTO.getPassword()).email(registerDTO.getEmail()).build();
        return userRepository.save(user);
    }
}
