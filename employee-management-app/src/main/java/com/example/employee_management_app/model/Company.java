package com.example.employee_management_app.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Company {
    private final List<Person> employees;
    private int idGenerator = 1;

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

    private synchronized int getNextId() {
        return idGenerator++;
    }

    private Person mapToPerson(String line) {
        String[] data = line.split(",");
        String firstName = data[0];
        String lastName = data[1];
        String email = data[2];
        String company = email.substring(email.indexOf('@') + 1, email.indexOf('.'));

        int id = getNextId();

        return new Person(id, firstName, lastName, email, capitalize(company));
    }

    private String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    public List<Person> getAllEmployees() {
        return employees;
    }

    public Optional<Person> getEmployeeById(int id) {
        return employees.stream().filter(e -> e.getId() == id).findFirst();
    }

    public Person addEmployee(Person newEmployee) {
        newEmployee.setId(employees.size() + 1);
        employees.add(newEmployee);

        return newEmployee;
    }

    public Person updateEmployee(int id, Person newEmployee) {
        Optional<Person> existingPerson = getEmployeeById(id);

        if (existingPerson.isPresent()) {
            Person person = existingPerson.get();
            person.setFirstName(newEmployee.getFirstName());
            person.setLastName(newEmployee.getLastName());
            person.setEmail(newEmployee.getEmail());
            person.setCompany(newEmployee.getCompany());

            return person;
        } else {
            throw new NoSuchElementException();
        }
    }

    public boolean deleteEmployee(int id) {
        return employees.removeIf(e -> e.getId() == id);
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
