package com.project.LaptechBE.repositories;

import com.project.LaptechBE.models.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
