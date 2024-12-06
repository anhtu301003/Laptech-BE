package com.project.LaptechBE.controllers;

import com.mongodb.internal.bulk.UpdateRequest;
import com.project.LaptechBE.DTO.ApiResponse;
import com.project.LaptechBE.DTO.UserDTO.UserRequest.LoginRequest;
import com.project.LaptechBE.DTO.UserDTO.UserRequest.RegisterRequest;
import com.project.LaptechBE.DTO.UserDTO.UserResponse.LoginResponse;
import com.project.LaptechBE.services.UserService;
import com.project.LaptechBE.untils.EmailValidator;
import com.project.LaptechBE.untils.Endpoints;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Path(Endpoints.User.BASE)
@Component
@RequiredArgsConstructor
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserController {

    private final UserService userService;

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

            // Thực hiện đăng ký người dùng ở đây
            var result = userService.RegisterUser(registerRequest);

            if (result == "Email is already existed") {
                return Response.status(Response.Status.BAD_REQUEST).entity(
                        ApiResponse.builder()
                                .status("ERR")
                                .message("Email is already existed")
                                .build()
                ).build();
            }

            if (result == "Error when create user") {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message("Error when create user")
                                        .build()
                        ).build();
            }

            return Response.status(Response.Status.OK).entity(
                    ApiResponse.builder()
                            .status("OK")
                            .message("SUCCESS")
                            .data(result)
                            .build()
            ).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    ApiResponse.builder()
                            .message(e.toString())
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
                                .message(result.toString()))
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

    @PATCH
    @Path(Endpoints.User.UPDATE)
    public Response update(UpdateRequest updateRequest) {

    }
}
