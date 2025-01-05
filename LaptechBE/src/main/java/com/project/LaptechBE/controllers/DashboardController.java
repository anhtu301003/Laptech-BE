package com.project.LaptechBE.controllers;

import com.project.LaptechBE.untils.Endpoints;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DashboardController {

    @GET
    @Path("/revenue")
    public Response getRevenue(){
        return null;
    }

    @GET
    @Path("/revenue/daily")
    public Response getDailyRevenue(){
        return null;
    }

    @GET
    @Path("/revenue/monthly")
    public Response getMonthlyRevenue(){
        return null;
    }

    @GET
    @Path("/products/top-selling")
    public Response getTopSellingProducts(){
        return null;
    }

    @GET
    @Path("/products/low-stock")
    public Response getLowStockProducts(){
        return null;
    }

    @GET
    @Path("/products/category-distribution")
    public Response getProductCategoryDistribution(){
        return null;
    }

    @GET
    @Path("/orders/status")
    public Response getOrderStatusDistribution(){
        return null;
    }

    @GET
    @Path("/orders/payment-methods")
    public Response getPaymentMethodDistribution(){
        return null;
    }

    @GET
    @Path("/orders/average-value")
    public Response getAverageOrderValue(){
        return null;
    }

    @GET
    @Path("/customers/new")
    public Response getNewCustomers(){
        return null;
    }

    @GET
    @Path("/customers/top-buyers")
    public Response getTopBuyingCustomers(){
        return null;
    }

    @GET
    @Path("/customers/location")
    public Response getCustomerLocationDistribution(){
        return null;
    }
}
