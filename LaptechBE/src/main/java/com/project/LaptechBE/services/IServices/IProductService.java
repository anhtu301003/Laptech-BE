package com.project.LaptechBE.services.IServices;

import com.project.LaptechBE.DTO.ProductDTO.ProductDTO;

import java.util.Map;

public interface IProductService {
    public Object createProduct(ProductDTO productDTO);

    public Object getProducts(Map<String, Object> filters, int page, int limit);

    public Object getProductById(String id);
}
