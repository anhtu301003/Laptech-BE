package com.project.LaptechBE.Annotation;

import com.project.LaptechBE.untils.SubCategoryValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SubCategoryValidator.class)
public @interface ValidSubCategory {
    String message() default "Invalid SubCategory";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
