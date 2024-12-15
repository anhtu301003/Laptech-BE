package com.project.LaptechBE.controllers;

import com.project.LaptechBE.DTO.ApiResponse;
import com.project.LaptechBE.DTO.CartDTO.AddToCartDTO;
import com.project.LaptechBE.models.User;
import com.project.LaptechBE.services.CartService;
import com.project.LaptechBE.untils.Endpoints;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Path(Endpoints.Cart.BASE)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CartController {
    private final CartService cartService;

    @POST
    public Response addToCart(AddToCartDTO addToCartDTO) {
        try{
            String userId = "";

            if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User user){
                userId = user.getId().toString();
            }

            if(addToCartDTO.getProductId().isBlank() || addToCartDTO.getQuantity() <= 0){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message("ProductId and quantity are required")
                                        .build()
                        )
                        .build();
            }

            var result = cartService.addToCart(userId, addToCartDTO.getProductId(), addToCartDTO.getQuantity());

            if(result instanceof String){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message(result.toString())
                                        .build()
                        )
                        .build();
            }

            return Response.status(Response.Status.OK)
                    .entity(
                            ApiResponse.builder()
                                    .status("OK")
                                    .message("Product added to cart successfully")
                                    .data(result)
                                    .build()
                    )
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(
                            ApiResponse.builder()
                                    .status("ERR")
                                    .message("Failed to add product to cart")
                                    .details(e.getMessage())
                                    .build()
                    )
                    .build();
        }
    }

    @GET
    public Response getCart() {
        try{
            String userId = "";
            if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User user){
                userId = user.getId().toString();
            }

            var result = cartService.getActiveCart(userId);

            return Response.status(Response.Status.OK)
                    .entity(
                            ApiResponse.builder()
                                    .status("OK")
                                    .data(result).build()
                    )
                    .build();
        }catch (Exception e){
            System.out.println("Controller Error:" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(
                            ApiResponse.builder()
                                    .status("ERR")
                                    .message("Failed to fetch cart")
                                    .details(e.getMessage()).build()
                    )
                    .build();
        }
    }
}
