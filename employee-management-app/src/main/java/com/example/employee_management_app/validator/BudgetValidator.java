package com.example.employee_management_app.validator;

import com.example.employee_management_app.model.Person;
import com.example.employee_management_app.service.PersonService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class BudgetValidator implements ConstraintValidator<ValidBudget, Person> {
    private final PersonService personService;
    private static final double COMPANY_BUDGET_LIMIT = 1_000_000.0;

    @Autowired
    public BudgetValidator(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public boolean isValid(Person person, ConstraintValidatorContext context) {
        if (person == null || person.getSalary() == 0 || person.getCurrency() == null) {
            return true;
        }

        double totalSalary = personService.calculateTotalSalaryForCurrency(person.getCurrency());
        double projectedTotal = totalSalary + person.getSalary();

        if (projectedTotal > COMPANY_BUDGET_LIMIT) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "Wynagrodzenie przekracza budżet firmowy dla tej waluty.")
                    .addPropertyNode("salary")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
