package com.project.LaptechBE.repositories;

import com.project.LaptechBE.enums.StatusEnum;
import com.project.LaptechBE.models.Cart;
import com.project.LaptechBE.models.Product;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartRepository extends MongoRepository<Cart, String> {
    Cart findByUserIdAndStatus(ObjectId objectId, String string);
}
