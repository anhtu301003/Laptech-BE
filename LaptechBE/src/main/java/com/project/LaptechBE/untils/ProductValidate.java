package com.project.LaptechBE.untils;

import com.project.LaptechBE.DTO.ProductDTO.ProductDTO;
import com.project.LaptechBE.DTO.ValidationError;
import com.project.LaptechBE.enums.CategoryEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProductValidate {

    private static final Set<String> VALID_CATEGORIES = Set.of(
            "laptop", "pc", "phone", "accessory", "tablet", "other"
    );

    public static boolean validateObjectId(String id){
        return id != null && !id.matches("^[0-9a-fA-F]{24}$");
    }

    public static Object validateProductInput(ProductDTO productDTO){
        return validateProductInput(productDTO,false);
    }

    public static Object validateProductInput(ProductDTO product, boolean isUpdate) {
        Map<String, String> errors = new HashMap<>();

        // Validate only for non-update scenarios
        if (!isUpdate) {
            // Null or empty checks
            if (isNullOrEmpty(product.getName()))
                errors.put("name", "Name is required");

            if (isNullOrEmpty(product.getDescription()))
                errors.put("description", "Description is required");

            if (isNullOrEmpty(product.getBrand()))
                errors.put("brand", "Brand is required");

            if (isNullOrEmpty(product.getCategory()))
                errors.put("category", "Category is required");

            if (product.getPrice() == null)
                errors.put("price", "Price is required");

            if (product.getStock() == null)
                errors.put("stock", "Stock is required");

            if (product.getImages() == null || product.getImages().isEmpty())
                errors.put("images", "At least one image is required");
        }

        // Validate category
        if (!isNullOrEmpty(product.getCategory()) && !VALID_CATEGORIES.contains(product.getCategory())) {
            errors.put("category", "Category must be one of: " + String.join(", ", VALID_CATEGORIES));
        }

        // Validate price
        if (product.getPrice() != null) {
            if (!isNonNegativeNumber(product.getPrice())) {
                errors.put("price", "Price must be a positive number");
            }
        }

        // Validate stock
        if (product.getStock() != null) {
            if (!isNonNegativeNumber(product.getStock())) {
                errors.put("stock", "Stock must be a non-negative number");
            }
        }

        return errors.size() > 0 ?
                ValidationError.builder()
                        .message("Invalid input data")
                        .details(errors)
                        .build()
                : null;
    }

    // Utility method to check if a string is null or empty
    private static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    // Utility method to check if a number is non-negative
    private static boolean isNonNegativeNumber(Number value) {
        if (value == null) return false;

        // Xử lý các kiểu số khác nhau
        if (value instanceof Integer) {
            return (Integer) value >= 0;
        } else if (value instanceof Long) {
            return (Long) value >= 0;
        } else if (value instanceof Double) {
            return (Double) value >= 0;
        } else if (value instanceof Float) {
            return (Float) value >= 0;
        }

        return false;
    }
}
