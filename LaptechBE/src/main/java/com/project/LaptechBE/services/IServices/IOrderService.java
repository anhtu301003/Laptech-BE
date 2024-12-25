package com.project.LaptechBE.services.IServices;

import com.project.LaptechBE.DTO.OrderDTO.OrderDTO;

import java.util.Map;

public interface IOrderService {
    public Object createOrder(OrderDTO orderDTO, String userId);

    Object getUserOrders(String userId, Map<String, Object> filter, String status);

    public Object getAllOrders(Map<String, Object> filter);

    public Object updateOrderStatus(String order,String status,String note,String userId);

    public Object getOrderStatus(String startDate,String endDate);

    public Object getOrderById(String orderId);

}
