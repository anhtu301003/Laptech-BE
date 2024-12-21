package com.project.LaptechBE.controllers;

import com.project.LaptechBE.DTO.ApiResponse;
import com.project.LaptechBE.DTO.OrderDTO.OrderDTO;
import com.project.LaptechBE.models.User;
import com.project.LaptechBE.services.OrderService;
import com.project.LaptechBE.untils.Endpoints;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Path(Endpoints.Order.BASE)
@Component
@RequiredArgsConstructor
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrderController {

    private final OrderService orderService;

    @POST
    public Response createOrder(OrderDTO orderDTO) {
        try{
            String userId = "";
            if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User user){
                userId = user.getId().toString();
            }

            if(Objects.isNull(orderDTO.getItems()) || Objects.isNull(orderDTO.getShippingAddress()) || Objects.isNull(orderDTO.getPaymentMethod())){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message("Missing required fields")
                                        .build()
                        )
                        .build();
            }

            if(Objects.isNull(orderDTO.getShippingAddress().getFullName()) ||Objects.isNull(orderDTO.getShippingAddress().getDetailAddress()) || Objects.isNull(orderDTO.getShippingAddress().getCity()) || Objects.isNull(orderDTO.getShippingAddress().getPhone())){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message("Missing required address fields: ")
                                        .build()
                        )
                        .build();
            }

            var result = orderService.createOrder(orderDTO,userId);
            return Response.status(Response.Status.CREATED)
                    .entity(
                            ApiResponse.builder()
                                    .status("OK")
                                    .message("ORDER_CREATED_SUCCESSFULLY")
                                    .data(result)
                                    .build()
                    )
                    .build();
        }catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(
                            ApiResponse.builder()
                                    .status("ERR")
                                    .message(e.getMessage())
                                    .build()
                    )
                    .build();
        }
    }

//    @GET
//    public Response getUserOrders(
//            @QueryParam("page") @DefaultValue("1") int page,
//            @QueryParam("limit") @DefaultValue("10") int limit
//    ){
//        try{
//
//            String userId = "";
//            if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User user){
//                userId = user.getId().toString();
//            }
//
//            Map<String,Object> filter = new HashMap<>();
//            filter.put("page", page);
//            filter.put("limit", limit);
//
//            var result = orderService.getUserOrders(userId,filter);
//
//            return Response.status(Response.Status.OK)
//                    .entity(
//                            ApiResponse.builder()
//                                    .status("OK")
//                                    .message("SUCCESS")
//                                    .data(result)
//                                    .build()
//                    )
//                    .build();
//
//        } catch (Exception e) {
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                    .entity(
//                            ApiResponse.builder()
//                                    .status("ERR")
//                                    .message(e.getMessage())
//                                    .build()
//                    )
//                    .build();
//        }
//    }

    @GET
    public Response getAllOrders(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("limit") @DefaultValue("10") int limit,
            @QueryParam("status") String status,
            @QueryParam("paymentStatus") String paymentStatus,
            @QueryParam("startDate") String startDate,
            @QueryParam("endDate") String endDate,
            @QueryParam("search") String search
    )
    {
        try{
            Map<String,Object> filter = new HashMap<>();
            filter.put("status", status);
            filter.put("paymentStatus", paymentStatus);
            filter.put("startDate", startDate);
            filter.put("endDate", endDate);
            filter.put("search", search);
            filter.put("page", page);
            filter.put("limit", limit);

           var result = orderService.getAllOrders(filter);

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
                                    .status("ERR")
                                    .message(e.getMessage())
                                    .build()
                    )
                    .build();
        }
    }
}
