package com.project.LaptechBE.controllers;

import com.project.LaptechBE.DTO.ApiResponse;
import com.project.LaptechBE.DTO.ReviewDTO.ReviewDTO;
import com.project.LaptechBE.models.Review;
import com.project.LaptechBE.models.User;
import com.project.LaptechBE.services.ReviewService;
import com.project.LaptechBE.untils.Endpoints;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Path(Endpoints.Review.BASE)
@Component
@RequiredArgsConstructor
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReviewController {

    private final ReviewService reviewService;

    @POST
    public Response addReview(ReviewDTO reviewDTO) {
        try{
            String userId = "";
            if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User user){
                userId = user.getId().toString();
            }

            var result = reviewService.addReview(userId,reviewDTO);

            return Response.status(201)
                    .entity(result)
                    .build();

        } catch (Exception e) {
            return Response.status(500)
                    .entity(
                            ApiResponse.builder()
                                    .message(e.getMessage())
                                    .build()
                    ).build();
        }
    }

    @GET
    @Path("/product/{productId}")
    public Response getReviewByProduct(@PathParam("productId") String productId) {
        return null;
    }

    @GET
    @Path("/user/{userId}")
    public Response getReviewsByUser(@PathParam("userId") String userId) {
        return null;
    }

    @PUT
    @Path("/{reviewId}")
    public Response updateReview(@PathParam("reviewId") String reviewId, ReviewDTO review) {
        return null;
    }

    @DELETE
    @Path("/{reviewId}")
    public Response deleteReview(@PathParam("reviewId") String reviewId){
        return null;
    }
}
