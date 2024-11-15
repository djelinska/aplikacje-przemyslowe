package com.example.employee_management_app.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CurrencyValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCurrency {
    String message() default "Waluta musi być jedną z: EUR, USD, GBP, JPY";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
