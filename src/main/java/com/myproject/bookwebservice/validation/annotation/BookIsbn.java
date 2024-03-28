package com.myproject.bookwebservice.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotBlank(message = "ISBN is required.")
@Size(max = 13, message = "ISBN field size can't be bigger than 13.")
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface BookIsbn {

    String message() default "Invalid ISBN.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
