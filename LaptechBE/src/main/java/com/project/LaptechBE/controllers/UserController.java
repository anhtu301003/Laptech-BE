package com.project.LaptechBE.controllers;

import com.project.LaptechBE.DTO.UserDTO.RegisterDTO;
import com.project.LaptechBE.services.UserService;
import com.project.LaptechBE.untils.Endpoints;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Path(Endpoints.API_PREFIX)
@Component
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @POST
    @Path(Endpoints.User.REGISTER)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String register(RegisterDTO registerDTO) {
        return userService.RegisterUser(registerDTO).toString();
    }

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello World!";
    }
}
