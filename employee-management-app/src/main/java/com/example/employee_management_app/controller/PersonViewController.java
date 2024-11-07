package com.example.employee_management_app.controller;

import com.example.employee_management_app.model.Person;
import com.example.employee_management_app.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/employees")
public class PersonViewController {
    private final PersonService personService;

    @Autowired
    public PersonViewController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public String listEmployees(@RequestParam(value = "country", required = false) String country, Model model) {
        List<Person> employees = personService.getEmployeesByCountry(country);
        Map<String, Double> salarySumByCurrency = personService.calculateSalarySumByCurrency();

        model.addAttribute("employees", employees);
        model.addAttribute("totalEmployees", employees.size());
        model.addAttribute("salarySumByCurrency", salarySumByCurrency);
        model.addAttribute("countries", personService.getAllCountries());
        model.addAttribute("selectedCountry", country);

        return "employees/list";
    }

    @GetMapping("/{email}")
    public String getEmployeeByEmail(@PathVariable String email, Model model) {
        Optional<Person> person = personService.getEmployeeByEmail(email);
        if (person.isPresent()) {
            model.addAttribute("employee", person.get());
            return "employees/details";
        } else {
            model.addAttribute("error", "Pracownik o podanym adresie email nie istnieje w systemie.");
            return "error";
        }
    }
}
