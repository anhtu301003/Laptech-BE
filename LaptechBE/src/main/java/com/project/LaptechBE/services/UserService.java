package com.project.LaptechBE.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AccessTokenService accessTokenService;

    private final RefreshTokenService refreshTokenService;

    private final MongoTemplate mongoTemplate;

    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Override
    public Object RegisterUser(RegisterRequest registerRequest) {
        try{
            Boolean checkUser = userRepository.existsByEmail(registerRequest.getEmail());
            String defaultAvatar = "https://cdn-icons-png.freepik.com/512/8742/8742495.png";
            if (checkUser) {
                return "Email is already existed";
            }

            var hashPassword = passwordEncoder.encode(registerRequest.getPassword());

            User user = User.builder()
                    .name(registerRequest.getName())
                    .password(hashPassword)
                    .email(registerRequest.getEmail())
                    .avatar(defaultAvatar)
                    .isAdmin(false)
                    .birthDate(null)
                    .phone(null)
                    .build();

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
            existingUser.setPhone(userDTO.getPhone());

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
            return user.get();
        } catch (Exception e) {
            return "Error when getting details user";
        }
    }

    @Override
    public Object getAllUsers() {
        try{
            var allUser = userRepository.findAll(Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("updatedAt")));
            return Response.status(Response.Status.OK)
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

    @Override
    public Object getUsers(Query query) {
        try{
            var users = mongoTemplate.find(query,User.class);

            Long totalItems = mongoTemplate.count(query.skip(0).limit(0),User.class);

            return new HashMap<String,Object>(){{
                put("users",users);
                put("totalItems",totalItems);
            }};
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object forgotPassword(String Email) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(Email);
            String email = jsonNode.get("email").asText();
            Query query = new Query(Criteria.where("email").is(email));
            var user = mongoTemplate.find(query,User.class);

            SecureRandom random = new SecureRandom();
            byte[] bytes = new byte[4]; // 4 bytes sẽ tạo ra 8 ký tự hex
            random.nextBytes(bytes);

            StringBuilder newPassword = new StringBuilder();
            for (byte b : bytes) {
                newPassword.append(String.format("%02x", b)); // Chuyển mỗi byte thành 2 ký tự hex
            }

            // Mã hóa mật khẩu mới
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(newPassword);

            Update update = new Update();
            update.set("password", hashedPassword);

            mongoTemplate.updateFirst(query, update, User.class);

            return emailService.sendEmail(
                    email,
                    "Reset Password",
                    "Your new password is: " + newPassword,
                    null
            );

        }catch (Exception e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    @Override
    public Object verifyEmail(String email,String name,String password) {
        try{
            Boolean checkUser = userRepository.existsByEmail(email);
            String defaultAvatar = "https://cdn-icons-png.freepik.com/512/8742/8742495.png";
            if (checkUser) {
                return "Email is already existed";
            }

            var hashPassword = passwordEncoder.encode(password);

            User user = User.builder()
                    .name(name)
                    .password(hashPassword)
                    .email(email)
                    .avatar(defaultAvatar)
                    .isAdmin(false)
                    .birthDate(null)
                    .phone(null)
                    .build();

            userRepository.save(user);

            return user;
        } catch (Exception e) {
            System.out.println(e.toString() + "Error when create user");
            return "Error when create user";
        }
    }
}
