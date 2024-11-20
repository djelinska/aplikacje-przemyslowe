package com.example.employee_management_app.model;

import com.example.employee_management_app.validator.ValidBudget;
import com.example.employee_management_app.validator.ValidCurrency;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

@ValidBudget
public class Person {
    private int id;

    @NotBlank(message = "Imię jest wymagane")
    @Length(min = 2, max = 50, message = "Imię musi mieć od 2 do 50 znaków")
    @Pattern(regexp = "^[A-Za-z-]+$", message = "Imię może zawierać tylko litery i myślniki")
    private String firstName;

    @NotBlank(message = "Nazwisko jest wymagane")
    @Length(min = 2, max = 50, message = "Nazwisko musi mieć od 2 do 50 znaków")
    @Pattern(regexp = "^[A-Za-z-]+$", message = "Nazwisko może zawierać tylko litery i myślniki")
    private String lastName;

    @NotBlank(message = "Adres e-mail jest wymagany")
    @Email(message = "Podaj poprawny adres email")
    private String email;

    @NotNull(message = "Wynagrodzenie jest wymagane")
    @DecimalMin(value = "0.01", message = "Wynagrodzenie musi być dodatnie")
    @DecimalMax(value = "1000000", message = "Wynagrodzenie nie może przekraczać 1,000,000")
    private Double salary = 0.0;

    @NotBlank(message = "Waluta jest wymagana")
    @ValidCurrency
    private String currency;

    @NotBlank(message = "Kraj jest wymagany")
    private String country;

    private String company;

    public Person(int id, String firstName, String lastName, String email, double salary, String currency, String country, String company) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.salary = salary;
        this.currency = currency;
        this.country = country;
        this.company = company;
    }

    public Person() {
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", salary=" + salary +
                ", currency='" + currency + '\'' +
                ", country='" + country + '\'' +
                ", company='" + company + '\'' +
                '}';
    }
}
