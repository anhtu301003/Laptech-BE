package com.project.LaptechBE.untils;

import com.project.LaptechBE.Annotation.ValidSubCategory;
import com.project.LaptechBE.enums.CategoryEnum;
import com.project.LaptechBE.models.Product;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SubCategoryValidator implements ConstraintValidator<ValidSubCategory, Product> {
    @Override
    public void initialize(ValidSubCategory constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Product product, ConstraintValidatorContext constraintValidatorContext) {
        if(product.getCategory() == null){
            return true;
        }
        if(product.getCategory().equals(CategoryEnum.LAPTOP) && product.getSubCategory() == null){
            return false;
        }

        return true;
    }
}
