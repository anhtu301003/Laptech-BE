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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

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

            if (result == null || result.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .data(result).build()
                        )
                        .build(); // Trả về cart rỗng hoặc lỗi nếu không có dữ liệu
            }

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

    @PUT
    public Response updateCartItem(AddToCartDTO addToCartDTO) {
        try{
            var userId = "";
            if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User user){
                userId = user.getId().toString();
            }

            if(addToCartDTO.getProductId().isBlank() || Objects.isNull(addToCartDTO.getQuantity())){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message("ProductId and quantity are required")
                                        .build()
                        )
                        .build();
            }

            if(addToCartDTO.getQuantity() < 1){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message("Quantity must be at least 1")
                                        .build()
                        )
                        .build();
            }

            var result = cartService.updateCartItem(userId, addToCartDTO.getProductId(), addToCartDTO.getQuantity());

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
                                    .message("Cart updated successfully")
                                    .data(result)
                                    .build()
                    )
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.OK)
                    .entity(
                            ApiResponse.builder()
                                    .status("ERR")
                                    .details(e.getMessage())
                                    .message("Failed to update cart")
                                    .build()
                    )
                    .build();
        }
    }

    @DELETE
    @Path("/{productId}")
    public Response removeFromCart(@PathParam("productId") String productId) {
        try{
            String userId = "";
            if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User user){
                userId = user.getId().toString();
            }

            if(Objects.isNull(productId)){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message("ProductId is required")
                                        .build()
                        )
                        .build();
            }

            var result = cartService.removeFromCart(userId, productId);

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
                                    .data(result)
                                    .message("Product removed from cart successfully")
                                    .build()
                    )
                    .build();
        } catch (Exception e) {
            System.out.println("Controller Error:" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(
                            ApiResponse.builder()
                                    .status("ERR")
                                    .message("Failed to remove product from cart")
                                    .details(e.getMessage())
                                    .build()
                    )
                    .build();
        }
    }

    @DELETE
    @Path("/clear")
    public Response clearCart() {
        try{
            String userId = "";
            if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User user){
                userId = user.getId().toString();
            }

            var result = cartService.clearCart(userId);

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
                                    .message("Cart cleared successfully")
                                    .build()
                    )
                    .build();
        } catch (Exception e) {
            System.out.println("Controller Error:" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(
                            ApiResponse.builder()
                                    .status("ERR")
                                    .message("Failed to clear cart")
                                    .details(e.getMessage())
                                    .build()
                    )
                    .build();
        }
    }
}
