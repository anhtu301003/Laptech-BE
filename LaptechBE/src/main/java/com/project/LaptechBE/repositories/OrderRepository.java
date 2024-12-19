package com.project.LaptechBE.repositories;

import com.project.LaptechBE.models.Cart;
import com.project.LaptechBE.models.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
}
