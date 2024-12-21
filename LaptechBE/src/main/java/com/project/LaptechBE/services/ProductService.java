package com.project.LaptechBE.services;

import com.project.LaptechBE.DTO.ProductDTO.ProductDTO;
import com.project.LaptechBE.enums.CategoryEnum;
import com.project.LaptechBE.enums.SubCategoryEnum;
import com.project.LaptechBE.models.Product;
import com.project.LaptechBE.models.submodels.submodelsProduct.ProductColor;
import com.project.LaptechBE.models.submodels.submodelsProduct.ProductReview;
import com.project.LaptechBE.models.submodels.submodelsProduct.ProductSpecification;
import com.project.LaptechBE.repositories.ProductRepository;
import com.project.LaptechBE.services.IServices.IProductService;
import com.project.LaptechBE.untils.Conveter;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final MongoTemplate mongoTemplate;

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
                    .category(CategoryEnum.valueOf(productDTO.getCategory()))
                    .subCategory(Objects.equals(productDTO.getCategory(),"laptop") ? SubCategoryEnum.fromString(productDTO.getSubCategory()) : null)
                    .brand(productDTO.getBrand())
                    .price(productDTO.getPrice())
                    .stock(productDTO.getStock())
                    .starting_price(productDTO.getStarting_price())
                    .images(productDTO.getImages())
                    .colors(
                            Objects.isNull(productDTO.getColors()) ? new ArrayList<>() : productDTO.getColors().stream()
                                    .map(color -> ProductColor.builder()
                                            .id(new ObjectId())
                                            .hex(color.getHex())
                                            .title(color.getTitle())
                                            .build()
                                    ).collect(Collectors.toList())
                    )
                    .specifications(
                            Objects.isNull(productDTO.getSpecifications()) ? new ArrayList<>() : productDTO.getSpecifications().stream()
                                    .map(
                                            specification -> ProductSpecification.builder()
                                                    .id(new ObjectId())
                                                    .type(specification.getType())
                                                    .title(specification.getTitle())
                                                    .description(specification.getDescription())
                                                    .build()
                                    ).collect(Collectors.toList())
                    )
                    .gift_value(productDTO.getGift_value())
                    .reviews(
                            Objects.isNull(productDTO.getReviews()) ? new ArrayList<>() : productDTO.getReviews().stream()
                                    .map(
                                            review -> ProductReview.builder()
                                                    .id(new ObjectId())
                                                    .rating(review.getRating())
                                                    .comment(review.getComment())
                                                    .build()
                                    ).collect(Collectors.toList())
                    )
                    .build();

            return productRepository.save(product);
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Map<String, Object> getProducts(Map<String, Object> filters, int page, int limit) {
        Map<String, Object> result = new HashMap<>();

        try {
            Query query = new Query();

            // Apply filters
            if (filters.get("category") != null) {
                query.addCriteria(Criteria.where("category").is(filters.get("category").toString()));
            }
            if (filters.get("subCategory") != null) {
                query.addCriteria(Criteria.where("subCategory").is(filters.get("subCategory").toString()));
            }
            if (filters.get("brand") != null) {
                query.addCriteria(Criteria.where("brand").is(filters.get("brand").toString()));
            }
            if (filters.get("price") != null) {
                query.addCriteria(Criteria.where("price").is(filters.get("price")));
            }

            // Handle text search
            if (filters.get("search") != null) {
                String search = filters.get("search").toString();
                query.addCriteria(new Criteria().orOperator(
                        Criteria.where("name").regex(search, "i"),
                        Criteria.where("brand").regex(search, "i"),
                        Criteria.where("description").regex(search, "i"),
                        Criteria.where("category").regex(search, "i"),
                        Criteria.where("subCategory").regex(search, "i")
                ));
            }

            // Apply sorting
            Sort sortOptions = Sort.by(Sort.Order.desc("updatedAt"));

            if (filters != null && filters.get("sort") != null) {
                Object sortValue = filters.get("sort");

                // Kiểm tra nếu giá trị là Integer và tương ứng với -1 hoặc 1
                if (sortValue instanceof Integer) {
                    int sort = (Integer) sortValue;
                    if (sort == -1) {
                        // Sắp xếp giảm dần
                        sortOptions = Sort.by(Sort.Order.desc("updatedAt"));
                    } else if (sort == 1) {
                        // Sắp xếp tăng dần
                        sortOptions = Sort.by(Sort.Order.asc("updatedAt"));
                    }
                } else if (sortValue instanceof String) {
                    String sort = (String) sortValue;
                    switch (sort) {
                        case "price-asc":
                            sortOptions = Sort.by(Sort.Order.asc("price"));
                            break;
                        case "price-desc":
                            sortOptions = Sort.by(Sort.Order.desc("price"));
                            break;
                        case "rating-desc":
                            sortOptions = Sort.by(Sort.Order.desc("averageRating"));
                            break;
                        // Thêm các trường hợp sắp xếp khác nếu cần
                    }
                }
            }

            // Check for isFeatured filter
            if ((Boolean)filters.get("isFeatured")) {
                query.addCriteria(Criteria.where("isFeatured").is(true));
                long totalProducts = mongoTemplate.count(query, Product.class);
                List<Product> products = mongoTemplate.find(query.with(sortOptions), Product.class);

                result.put("data", products);
                result.put("count", totalProducts);
                return result;
            }

            // Pagination logic
            int skip = (page - 1) * limit;
            long totalProducts = mongoTemplate.count(query, Product.class);
            List<Product> products = mongoTemplate.find(query.with(sortOptions).skip(skip).limit(limit), Product.class);

            result.put("data", products);
            result.put("count", totalProducts);
            result.put("totalPages", (int) Math.ceil((double) totalProducts / limit));
            return result;

        } catch (Exception e) {
            throw new RuntimeException("Service Error - Get Products: " + e.getMessage(), e);
        }
    }

    @Override
    public Object getProductById(String id) {
        try{
            var product = mongoTemplate.findById(id, Product.class);

            if(product == null) {
                return "Product not found";
            }
            return product;
        }catch (Exception e){
            throw new RuntimeException("Service Error - Get Product: " + e.getMessage(), e);
        }
    }

}
