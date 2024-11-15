package com.example.employee_management_app.service;

import com.example.employee_management_app.config.CountryConfig;
import com.example.employee_management_app.model.Company;
import com.example.employee_management_app.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PersonService {
    private final Person president;
    private final Person vicePresident;
    private final Person secretary;
    private final Company company;
    private final CountryConfig countryConfig;

    @Autowired
    public PersonService(Person president, Person vicePresident, Person secretary, Company company, CountryConfig countryConfig) {
        this.president = president;
        this.vicePresident = vicePresident;
        this.secretary = secretary;
        this.company = company;
        this.countryConfig = countryConfig;
    }

    public void displayKeyEmployees() {
        System.out.println("\nKluczowi pracownicy z pliku XML:");
        System.out.println("Prezes: " + president);
        System.out.println("Wiceprezes: " + vicePresident);
        System.out.println("Sekretarka: " + secretary);
    }

    public List<Person> getAllEmployees() {
        return company.getAllEmployees();
    }

    public Optional<Person> getEmployeeById(int id) {
        return company.getEmployeeById(id);
    }

    public Optional<Person> getEmployeeByEmail(String email) {
        return company.getEmployeeByEmail(email);
    }

    public Person addEmployee(Person newEmployee) {
        return company.addEmployee(newEmployee);
    }

    public Person updateEmployee(int id, Person newEmployee) {
        return company.updateEmployee(id, newEmployee);
    }

    public void updateEmployeeByEmail(String email, Person newEmployee) {
        company.updateEmployeeByEmail(email, newEmployee);
    }

    public boolean deleteEmployee(int id) {
        return company.deleteEmployee(id);
    }

    public boolean deleteEmployeeByEmail(String email) {
        return company.deleteEmployeeByEmail(email);
    }

    public List<Person> filterByCompany(String companyName) {
        return company.filterByCompany(companyName);
    }

    public List<Person> sortByLastName() {
        return company.sortByLastName();
    }

    public Map<String, Double> calculateSalarySumByCurrency() {
        return company.calculateSalarySumByCurrency();
    }

    public List<String> getAllCountries() {
        return company.getAllCountries();
    }

    public List<Person> getEmployeesByCountry(String country) {
        return company.getEmployeesByCountry(country);
    }

    public boolean isEmailUnique(String email, String currentEmail) {
        return !company.isEmailUnique(email, currentEmail);
    }

    public Set<String> getAllUniqueCountries() {
        List<String> userCountries = company.getAllCountries();
        List<String> configCountries = countryConfig.getCountries();

        Set<String> allCountries = new HashSet<>();
        allCountries.addAll(userCountries);
        allCountries.addAll(configCountries);

        return allCountries;
    }
}
