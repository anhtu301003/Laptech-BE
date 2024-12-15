package com.project.LaptechBE.services;

import com.project.LaptechBE.DTO.ApiResponse;
import com.project.LaptechBE.DTO.UserDTO.UserDTO;
import com.project.LaptechBE.DTO.UserDTO.UserRequest.LoginRequest;
import com.project.LaptechBE.DTO.UserDTO.UserRequest.RegisterRequest;
import com.project.LaptechBE.DTO.UserDTO.UserResponse.LoginResponse;
import com.project.LaptechBE.models.User;
import com.project.LaptechBE.repositories.UserRepository;
import com.project.LaptechBE.services.IServices.IUserService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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
    public Object UpdateUser(ObjectId id, UserDTO userDTO) {
        try{
            Optional<User> checkUser = userRepository.findById(id);
            if(checkUser.isEmpty()) {
                return "The user is not defined";
            }

            // Lấy đối tượng User hiện tại từ Optional
            User existingUser = checkUser.get();

            // Cập nhật các trường được truyền trong userDTO
            existingUser.setName(userDTO.getName());
            existingUser.setAddresses(userDTO.getAddresses());
            existingUser.setAvatar(userDTO.getAvatar());
            existingUser.setEmail(userDTO.getEmail());
            existingUser.setUpdatedAt(LocalDateTime.now());

            // Lưu lại user đã cập nhật
            userRepository.save(existingUser);


            return userRepository.save(existingUser);
        }
        catch (Exception e){
            System.out.println(e.toString() + "Error when update user");
            return "Error when deleting user";
        }
    }

    @Override
    public Object DeleteUser(String id) {
        try{
            var checkUser = userRepository.findById(id);
            if(checkUser.isEmpty()) {
                return "The user is not defined";
            }

            userRepository.deleteById(id);
            return checkUser;
        } catch (Exception e) {
            return "Error when deleting user";
        }
    }

    @Override
    public Object GetDetailsUser(String id) {
        try{
            var user = userRepository.findById(id);
            if(user.isEmpty()) {
                return "The user is not defined";
            }
            return user;
        } catch (Exception e) {
            return "Error when getting details user";
        }
    }

    @Override
    public Object GetAllUsers() {
        try{
            var allUser = userRepository.findAll(Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("updatedAt")));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.builder()
                            .status("OK")
                            .message("Success")
                            .data(allUser)
                            .build()
                    )
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.builder()
                            .status("ERR")
                            .message("Error when getting all users")
                            .build()
                    )
                    .build();
        }
    }
}
