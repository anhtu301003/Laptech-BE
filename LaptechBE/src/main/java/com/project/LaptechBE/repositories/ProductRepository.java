package com.project.LaptechBE.repositories;

import com.project.LaptechBE.models.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    public Product findByNameAndBrand(String name, String brand);
    public long countByIsFeatured(boolean isFeatured);
    public List<Product> findByIsFeatured(boolean isFeatured, Sort sort);

    Object findByIdAndNameAndBrand(String id, String name, String brand);
}
