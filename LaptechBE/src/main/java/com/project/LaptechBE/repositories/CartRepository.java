package com.project.LaptechBE.repositories;

import com.project.LaptechBE.models.Cart;
import com.project.LaptechBE.models.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartRepository extends MongoRepository<Cart, String> {
    public Cart findByUserIdAndStatus(String userId, String status);
}
