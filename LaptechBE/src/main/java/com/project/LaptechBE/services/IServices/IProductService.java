package com.project.LaptechBE.services.IServices;

import com.project.LaptechBE.DTO.ProductDTO.ProductDTO;

import java.util.List;
import java.util.Map;

public interface IProductService {
    public Object createProduct(ProductDTO productDTO);

    public Object getProducts(Map<String, Object> filters, int page, int limit);

    public Object getProductById(String id);

    public Object createBulkProducts(List<ProductDTO> productDTOList);

    public Object getAllCategory();

    public Object updateProduct(String id,ProductDTO productDTO);

    public Object deleteProduct(String id);
}
