package com.project.LaptechBE.controllers;

import com.project.LaptechBE.DTO.ApiResponse;
import com.project.LaptechBE.DTO.UserDTO.UserDTO;
import com.project.LaptechBE.DTO.UserDTO.UserRequest.LoginRequest;
import com.project.LaptechBE.DTO.UserDTO.UserRequest.RegisterRequest;
import com.project.LaptechBE.DTO.UserDTO.UserResponse.LoginResponse;
import com.project.LaptechBE.models.User;
import com.project.LaptechBE.services.EmailService;
import com.project.LaptechBE.services.OrderService;
import com.project.LaptechBE.services.RefreshTokenService;
import com.project.LaptechBE.services.UserService;
import com.project.LaptechBE.untils.EmailValidator;
import com.project.LaptechBE.untils.Endpoints;
import com.project.LaptechBE.untils.generateRandomToken;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Path(Endpoints.User.BASE)
@Component
@RequiredArgsConstructor
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserController {

    private final UserService userService;

    private final RefreshTokenService refreshTokenService;
    private final OrderService orderService;
    private final EmailService emailService;

    public Map<String,String> emailVerificationTokens = new HashMap<>();

    @Value("${server.port}")
    private String serverPort;

    @Value("${server.address:localhost}")
    private String serverAddress;
    @POST
    @Path(Endpoints.User.REGISTER)
    public Response register(RegisterRequest registerRequest) {
        try {

            if (registerRequest.getName().isBlank() || registerRequest.getPassword().isBlank() || registerRequest.getEmail().isBlank() || registerRequest.getConfirmPassword().isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(
                        ApiResponse.builder()
                                .status("ERR")
                                .message("The input is required").build()
                ).build();
            }

            if (EmailValidator.isValidEmail(registerRequest.getEmail()) == false) {
                return Response.status(Response.Status.BAD_REQUEST).entity(
                        ApiResponse.builder()
                                .status("ERR")
                                .message("the input is email").build()
                ).build();
            }

            if (registerRequest.getPassword().equals(registerRequest.getConfirmPassword()) == false) {
                return Response.status(Response.Status.BAD_REQUEST).entity(
                        ApiResponse.builder()
                                .status("ERR")
                                .message("The password is not equal to confirmPassword").build()
                ).build();
            }

            var Token = generateRandomToken.randomToken();

            emailVerificationTokens.put(registerRequest.getEmail(), Token);

            String verifyUrl = "http://"+serverAddress + ":" + serverPort + "/api/user/verify-email?token="+Token+"&email="+registerRequest.getEmail()+"&password="+registerRequest.getPassword()+"&name="+registerRequest.getName();

            var sent = emailService.sendEmail(
                    registerRequest.getEmail(),
                    "Email Verification for Laptech",
                    "",
                    "Hi" + registerRequest.getName() + "Thank you for registering. Please verify your email by clicking the link below: <a href=\"" + verifyUrl + "\">Verify Email</a>"
            );

            if(!sent){
                return Response.status(500)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message("Error when sending email")
                                        .build()
                        ).build();
            }
            return Response.status(200)
                    .entity(
                            ApiResponse.builder()
                                    .status("OK")
                                    .message("Email sent successfully")
                                    .build()
                    ).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    ApiResponse.builder()
                            .message("An error occurred while sending the verification email.")
                            .build()
            ).build();
        }
    }

    @POST
    @Path(Endpoints.User.LOGIN)
    public Response login(LoginRequest loginRequest) {
        try {

            if (loginRequest.getEmail().isBlank() || loginRequest.getPassword().isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message("Email and password are required")
                                        .build()
                        ).build();
            }

            if (EmailValidator.isValidEmail(loginRequest.getEmail()) == false) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message("Invalid email format")
                                        .build()
                        ).build();
            }

            var result = userService.LoginUser(loginRequest);

            if (result instanceof String) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.builder()
                                .status("ERR")
                                .message(result.toString())
                                .build())
                        .build();
            }


            if (result instanceof LoginResponse loginResponse) {

                NewCookie cookie = new NewCookie.Builder("refresh_token")
                        .value(loginResponse.getRefreshtoken())
                        .httpOnly(true)
                        .secure("production".equalsIgnoreCase(System.getenv("NODE_ENV")))
                        .path("/")
                        .maxAge(7 * 24 * 60 * 60) // 7 ngày
                        .build();

                // Thêm cookie vào phản hồi

                return Response.status(Response.Status.OK)
                        .cookie(cookie)
                        .entity(
                                ApiResponse.builder()
                                        .status("OK")
                                        .message("LOGIN_SUCCESS")
                                        .data(loginResponse.getData())
                                        .access_token(loginResponse.getAccesstoken()).build()
                        )
                        .build();
            }

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.builder()
                            .status("ERR")
                            .message("Internal server error")
                            .build())
                    .build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.builder()
                        .status("ERR")
                        .message("Internal server error")
                        .build())
                .build();
    }

    @PUT
    public Response update(UserDTO userDTO) {

        try {
            ObjectId userid = null;
            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User user) {
                userid = user.getId();
            }

            if(userid == null){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message("The userId is required")
                                        .build()
                        )
                        .build();
            }

            var result = userService.UpdateUser(userid, userDTO);

            if(result == "The user is not defined"){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.builder()
                                .status("ERR")
                                .message("The user is not defined")
                                .build()
                        )
                        .build();
            }

            if(result == "Error when update user"){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.builder()
                                .status("ERR")
                                .message("Error when update user")
                                .build()
                        )
                        .build();
            }

            return Response.status(Response.Status.OK)
                    .entity(ApiResponse.builder()
                                    .status("OK")
                                    .message("Success")
                                    .data(result)
                                    .build()
                    )
                    .build();
        }catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.builder()
                            .message(e.toString())
                            .build()
                    )
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        try{
            if(id.isBlank()){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message("The userId is required")
                        )
                        .build();
            }

            var result = userService.DeleteUser(id);

            if(result == "The user is not defined"){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message("The user is not defined")
                                        .build()
                        )
                        .build();
            }

            if(result == "Error when delete user"){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message("Error when delete user")
                                        .build()
                        )
                        .build();
            }

            return Response.status(Response.Status.OK)
                    .entity(
                            ApiResponse.builder()
                                    .status("OK")
                                    .message("SUCCESS")
                                    .data(result)
                                    .build()
                    )
                    .build();
        }catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(
                            ApiResponse.builder()
                                    .message(e.getMessage())
                                    .build()
                    )
                    .build();
        }

    }

    @GET
    @Path("/all")
    public Response getUsers(
            @QueryParam("search") String search,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("limit") @DefaultValue("10") int limit
    ) {
        try{
            int skip = (page - 1) * limit;

            // Tạo query
            Query query = new Query();
            if (search != null && !search.isEmpty()) {
                query.addCriteria(new Criteria().orOperator(
                        Criteria.where("name").regex(search, "i"),
                        Criteria.where("email").regex(search, "i")
                ));
            }

            query.skip(skip).limit(limit);

            Map<String,Object> result = (Map<String, Object>) userService.getUsers(query);
            return Response.status(200)
                    .entity(
                            ApiResponse.builder()
                                .status("OK")
                                .message("Success")
                                .data(result.get("users"))
                                .totalPages((int) Math.ceil((double) ((long) result.get("totalItems"))))
                                .build()
                    ).build();
        }catch (Exception e){
            return Response.status(400)
                    .entity(
                            ApiResponse.builder()
                                    .status("ERR")
                                    .message(e.getMessage())
                                    .build()
                    ).build();
        }
    }

    @GET
    public Response getDetailsUser() {
        try{

            String userId = "";

            if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User user) {
                userId = user.getId().toString();
            }

            if(userId.isBlank()){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message("The userId is required")
                                        .build()
                        )
                        .build();

            }

            var result = userService.GetDetailsUser(userId);

            if(result == "The user is not defined"){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message("The user is not defined")
                                        .build()
                        )
                        .build();
            }

            if(result == "Error when get user details"){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message("Error when get user details")
                                        .build()
                        )
                        .build();
            }

            return Response.status(Response.Status.OK)
                    .entity(
                            ApiResponse.builder()
                                    .status("OK")
                                    .message("SUCCESS")
                                    .data(result)
                                    .build()
                    )
                    .build();
        }catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(
                            ApiResponse.builder()
                                    .message(e.getMessage())
                                    .build()
                    )
                    .build();
        }
    }

//    @GET
//    public Response getAllUser(){
//        try {
//            var result = userService.getAllUsers();
//            return Response.status(200)
//                    .entity(result)
//                    .build();
//        } catch (Exception e) {
//            return Response.status(500)
//                    .entity(
//                            ApiResponse.builder()
//                                    .message(e.getMessage())
//                                    .build()
//                    ).build();
//        }
//    }

    @POST
    @Path(Endpoints.User.REFRESHTOKEN)
    public Response refreshToken(@CookieParam("refresh_token") String refreshToken) {
        try{
            if(refreshToken == null || refreshToken.isEmpty()){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message("The token is required")
                                        .build()
                        )
                        .build();
            }

            var result = refreshTokenService.refreshAccessToken(refreshToken);

            return Response.status(Response.Status.OK)
                    .entity(
                            ApiResponse.builder()
                                    .data(result)
                                    .build()
                    ).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(
                            ApiResponse.builder()
                                    .message(e.getMessage())
                                    .build()
                    ).build();
        }
    }

    @GET
    @Path("/verify-email")
    public Response verifyEmail(
            @QueryParam("token") String token,
            @QueryParam("email") String email,
            @QueryParam("name") String name,
            @QueryParam("password") String password
    ) {
        try{

            if(emailVerificationTokens.get(email) == null || !emailVerificationTokens.get(email).equals(token)){
                return Response.status(400)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message("Invalid or expired verification token.")
                                        .build()
                        )
                        .build();
            }

            emailVerificationTokens.remove(email);

            var user = userService.verifyEmail(email,name,password);

            return Response.status(201)
                    .entity(
                            ApiResponse.builder()
                                    .status("OK")
                                    .message("Email verified and user created successfully.")
                                    .data(user)
                                    .build()
                    ).build();
        } catch (Exception e) {
            return Response.status(201)
                    .entity(
                            ApiResponse.builder()
                                    .status("ERR")
                                    .message("An error occurred during email verification.")
                                    .build()
                    ).build();
        }
    }

    @POST
    @Path("/forgot-password")
    public Response forgotPassword(String email){
        if(email.isEmpty()){
            return Response.status(400).entity(
                    ApiResponse.builder()
                            .message("Email is required")
                            .build()
            ).build();
        }
        try{
            var result = userService.forgotPassword(email);

            return Response.status(200)
                    .entity(
                            ApiResponse.builder()
                                    .status("OK")
                                    .message("Password has been reset. Please check your email.")
                                    .build()
                    ).build();
        }catch (Exception e){
            return Response.status(500)
                    .entity(
                            ApiResponse.builder()
                                    .status("ERR")
                                    .message("Internal server error")
                                    .build()
                    ).build();
        }
    }
}
