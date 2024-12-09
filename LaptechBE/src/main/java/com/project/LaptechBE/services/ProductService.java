package com.project.LaptechBE.services;

import com.project.LaptechBE.DTO.ProductDTO.ProductDTO;
import com.project.LaptechBE.enums.CategoryEnum;
import com.project.LaptechBE.enums.SubCategoryEnum;
import com.project.LaptechBE.models.Product;
import com.project.LaptechBE.repositories.ProductRepository;
import com.project.LaptechBE.services.IServices.IProductService;
import com.project.LaptechBE.untils.Conveter;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;

    @Override
    public Object createProduct(ProductDTO productDTO) {
        try{
            var existingProduct = productRepository.findByNameAndBrand(productDTO.getName(), productDTO.getBrand());

            if(existingProduct != null){
                return "A product with this name and brand already exists";
            }

            if(productDTO.getCategory() == "laptop" && productDTO.getSubCategory() == null){
                return "subCategory is required for laptop products";
            }


            Product product = Product.builder()
                    .name(productDTO.getName())
                    .description(productDTO.getDescription())
                    .category(CategoryEnum.fromString((productDTO.getCategory()).toUpperCase()))
                    .subCategory(SubCategoryEnum.fromString((productDTO.getSubCategory()).toUpperCase()))
                    .brand(productDTO.getBrand())
                    .price(productDTO.getPrice())
                    .stock(productDTO.getStock())
                    .sale_percentage(productDTO.getSale_percentage())
                    .starting_price(productDTO.getStarting_price())
                    .images(productDTO.getImages())
                    .colors(Conveter.convertToProductColorList(productDTO.getColors()))
                    .specifications(Conveter.convertToProductSpecificationList(productDTO.getSpecifications()))
                    .gift_value(productDTO.getGift_value())
                    .reviews(Conveter.convertToProductReviewList(productDTO.getReviews()))
                    .build();

            return productRepository.save(product);
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Object getProducts(Map<String, Object> filters, int page, int limit) {
//        Query query = new Query();
        return null;
    }

}
