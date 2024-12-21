package com.project.LaptechBE.services.IServices;

import com.project.LaptechBE.DTO.OrderDTO.OrderDTO;

import java.util.Map;

public interface IOrderService {
    public Object createOrder(OrderDTO orderDTO, String userId);

    Object getUserOrders(String userId, Map<String, Object> filter);

    Object getAllOrders(Map<String, Object> filter);
}
