package com.myproject.bookwebservice.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotBlank(message = "Author is required.")
@Size(max = 100, message = "Author field size can't be bigger than 100.")
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface BookAuthor {

    String message() default "Invalid author.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
