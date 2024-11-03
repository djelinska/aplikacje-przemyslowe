package com.example.employee_management_app.controller;

import com.example.employee_management_app.model.Person;
import com.example.employee_management_app.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/persons")
public class PersonController {
    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public ResponseEntity<List<Person>> getEmployees() {
        List<Person> employees = personService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getEmployeeById(@PathVariable int id) {
        Optional<Person> person = personService.getEmployeeById(id);
        return person.<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Employee with ID " + id + " not found."));
    }

    @PostMapping
    public ResponseEntity<Object> addEmployee(@RequestBody Person person) {
        try {
            Person createdPerson = personService.addEmployee(person);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPerson);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating employee: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateEmployee(@PathVariable int id, @RequestBody Person person) {
        try {
            Person updatedPerson = personService.updateEmployee(id, person);
            return ResponseEntity.ok(updatedPerson);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Employee with ID " + id + " not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating employee: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePerson(@PathVariable int id) {
        boolean deleted = personService.deleteEmployee(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Employee with ID " + id + " not found.");
        }
    }
}
