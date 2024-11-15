package com.example.employee_management_app.controller;

import com.example.employee_management_app.model.Person;
import com.example.employee_management_app.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        if (model.containsAttribute("successMessage")) {
            model.addAttribute("successMessage", model.getAttribute("successMessage"));
        }

        if (model.containsAttribute("errorMessage")) {
            model.addAttribute("errorMessage", model.getAttribute("errorMessage"));
        }

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

    @GetMapping("/add")
    public String showAddEmployeeForm(Model model) {
        model.addAttribute("employee", new Person());
        model.addAttribute("countries", personService.getAllUniqueCountries());
        model.addAttribute("currencies", List.of("EUR", "GBP", "USD", "JPY"));

        return "employees/add";
    }

    @PostMapping("/add")
    public String processAddEmployee(@Valid @ModelAttribute("employee") Person employee, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (personService.isEmailUnique(employee.getEmail(), null)) {
            result.rejectValue("email", "error.email", "Email musi być unikalny");
        }
        if (result.hasErrors()) {
            model.addAttribute("countries", personService.getAllUniqueCountries());
            model.addAttribute("currencies", List.of("EUR", "GBP", "USD", "JPY"));
            return "employees/add";
        }
        personService.addEmployee(employee);
        redirectAttributes.addFlashAttribute("successMessage", "Pracownik został pomyślnie dodany.");

        return "redirect:/employees";
    }

    @GetMapping("/edit/{email}")
    public String showEditEmployeeForm(@PathVariable String email, Model model) {
        Optional<Person> employeeOpt = personService.getEmployeeByEmail(email);

        if (employeeOpt.isPresent()) {
            model.addAttribute("employee", employeeOpt.get());
        } else {
            return "redirect:/employees";
        }
        model.addAttribute("countries", personService.getAllUniqueCountries());
        model.addAttribute("currencies", List.of("EUR", "GBP", "USD", "JPY"));

        return "employees/edit";
    }

    @PostMapping("/edit/{email}")
    public String processEditEmployee(@PathVariable String email, @Valid @ModelAttribute("employee") Person employee, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (personService.isEmailUnique(employee.getEmail(), email)) {
            result.rejectValue("email", "error.email", "Email musi być unikalny");
        }
        if (result.hasErrors()) {
            model.addAttribute("countries", personService.getAllUniqueCountries());
            model.addAttribute("currencies", List.of("EUR", "GBP", "USD", "JPY"));
            return "employees/edit";
        }
        personService.updateEmployeeByEmail(email, employee);
        redirectAttributes.addFlashAttribute("successMessage", "Pracownik został pomyślnie zaktualizowany.");

        return "redirect:/employees";
    }

    @PostMapping("/delete/{email}")
    public String deleteEmployee(@PathVariable String email, Model model, RedirectAttributes redirectAttributes) {
        boolean isDeletedEmployee = personService.deleteEmployeeByEmail(email);
        if (isDeletedEmployee) {
            redirectAttributes.addFlashAttribute("successMessage", "Pracownik został pomyślnie usunięty.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Wystąpił błąd podczas usuwania pracownika.");
        }

        return "redirect:/employees";
    }
}
