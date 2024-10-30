package com.example.employee_management_app.service;

import com.example.employee_management_app.model.Company;
import com.example.employee_management_app.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {
    private final Person president;
    private final Person vicePresident;
    private final Person secretary;
    private final Company company;

    @Autowired
    public PersonService(Person president, Person vicePresident, Person secretary, Company company) {
        this.president = president;
        this.vicePresident = vicePresident;
        this.secretary = secretary;
        this.company = company;
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

    public List<Person> filterByCompany(String companyName) {
        return company.filterByCompany(companyName);
    }

    public List<Person> sortByLastName() {
        return company.sortByLastName();
    }
}
