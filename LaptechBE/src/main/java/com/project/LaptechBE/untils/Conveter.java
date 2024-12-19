package com.project.LaptechBE.untils;

import com.project.LaptechBE.DTO.ProductDTO.ProductDTO;
import com.project.LaptechBE.models.Product;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Conveter {

//    public static List<Product.Color> convertToProductColorList(List<ProductDTO.Color> dtoColors) {
//        return dtoColors.stream()
//                .map(dtoColor -> Product.Color.builder()
//                        .title(dtoColor.getTitle())
//                        .hex(dtoColor.getHex())
//                        .id(new ObjectId())
//                        .build())
//
//                .collect(Collectors.toList());
//    }
//
//    public static List<Product.Specification> convertToProductSpecificationList(List<ProductDTO.Specification> dtoSpecifications) {
//        return dtoSpecifications.stream()
//                .map(dtoSpecification -> Product.Specification.builder()
//                        .type(dtoSpecification.getType())
//                        .description(dtoSpecification.getDescription())
//                        .title(dtoSpecification.getTitle())
//                        .id(new ObjectId())
//                        .build())
//                .collect(Collectors.toList());
//    }
//
//    public static List<Product.Review> convertToProductReviewList(List<ProductDTO.Review> dtoReviews) {
//        if(dtoReviews == null) return new ArrayList<>();
//        return dtoReviews.stream()
//                .map(dtoReview -> Product.Review.builder()
//                        .comment(dtoReview.getComment())
//                        .rating(dtoReview.getRating())
//                        .id(new ObjectId())
//                        .build())
//                .collect(Collectors.toList());
//    }
}
