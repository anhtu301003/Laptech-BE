package com.project.LaptechBE.services;

import com.project.LaptechBE.DTO.ApiResponse;
import com.project.LaptechBE.DTO.UserDTO.UserRequest.LoginRequest;
import com.project.LaptechBE.DTO.UserDTO.UserRequest.RegisterRequest;
import com.project.LaptechBE.DTO.UserDTO.UserResponse.LoginResponse;
import com.project.LaptechBE.enums.RoleEnum;
import com.project.LaptechBE.models.User;
import com.project.LaptechBE.repositories.UserRepository;
import com.project.LaptechBE.services.IServices.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AccessTokenService accessTokenService;

    private final RefreshTokenService refreshTokenService;

    private final AuthenticationManager authenticationManager;

    @Override
    public Object RegisterUser(RegisterRequest registerRequest) {
        try{
            Boolean checkUser = userRepository.existsByEmail(registerRequest.getEmail());
            String defaultAvatar = "https://cdn-icons-png.freepik.com/512/8742/8742495.png";
            if (checkUser) {
                return "Email is already existed";
            }

            var     hashPassword = passwordEncoder.encode(registerRequest.getPassword());

            User user = User.builder()
                    .name(registerRequest.getName())
                    .password(hashPassword)
                    .email(registerRequest.getEmail())
                    .avatar(defaultAvatar)
                    .isAdmin(false).build();

            userRepository.save(user);

            return user;
        } catch (Exception e) {
            System.out.println(e.toString() + "Error when create user");
            return "Error when create user";
        }
    }

    @Override
    public Object LoginUser(LoginRequest loginRequest) {
        try{
            var checkUser = userRepository.existsByEmail(loginRequest.getEmail());

            if(checkUser == null) {
                return "User does not exist";
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                    )
            );

            var user = userRepository.findByEmail(loginRequest.getEmail());

            var jwtAccessToken = accessTokenService.generateAccessToken(user);

            var jwtRefreshToken = refreshTokenService.generateRefreshToken(user);

            return LoginResponse.builder()
                    .data(user)
                    .accesstoken(jwtAccessToken)
                    .refreshtoken(jwtRefreshToken)
                    .build();

        }catch (Exception e){
            return e.getMessage();
        }
    }

    @Override
    public User UpdateUser(String id) {
        return null;
    }
}
