package com.example.employee_management_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CountryConfig {
    @Value("#{'${app.countries}'.split(',')}")
    private List<String> countries;

    public List<String> getCountries() {
        return countries;
    }
}
