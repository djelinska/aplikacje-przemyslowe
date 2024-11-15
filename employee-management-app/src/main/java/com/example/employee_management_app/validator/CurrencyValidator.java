package com.example.employee_management_app.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class CurrencyValidator implements ConstraintValidator<ValidCurrency, String> {
    private final List<String> allowedCurrencies = List.of("EUR", "USD", "GBP", "JPY");

    @Override
    public boolean isValid(String currency, ConstraintValidatorContext context) {
        return currency != null && allowedCurrencies.contains(currency.toUpperCase());
    }
}

