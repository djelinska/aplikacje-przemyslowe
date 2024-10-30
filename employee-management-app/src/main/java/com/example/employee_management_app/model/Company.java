package com.example.employee_management_app.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Company {
    private final List<Person> employees;

    public Company(String filePath) {
        this.employees = loadEmployees(filePath);
    }

    private List<Person> loadEmployees(String filePath) {
        try {
            return Files.lines(Path.of(filePath))
                    .skip(1)
                    .limit(10)
                    .map(this::mapToPerson)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Błąd podczas odczytu pliku: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private Person mapToPerson(String line) {
        String[] data = line.split(",");
        String firstName = data[0];
        String lastName = data[1];
        String email = data[2];
        String company = email.substring(email.indexOf('@') + 1, email.indexOf('.'));

        return new Person(firstName, lastName, email, capitalize(company));
    }

    private String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    public List<Person> getAllEmployees() {
        return new ArrayList<>(employees);
    }

    public List<Person> filterByCompany(String company) {
        return employees.stream()
                .filter(employee -> employee.getCompany().equalsIgnoreCase(company))
                .collect(Collectors.toList());
    }

    public List<Person> sortByLastName() {
        return employees.stream()
                .sorted((p1, p2) -> p1.getLastName().compareToIgnoreCase(p2.getLastName()))
                .collect(Collectors.toList());
    }
}
