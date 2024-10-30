package com.example.employee_management_app;

import com.example.employee_management_app.model.Company;
import com.example.employee_management_app.model.Person;
import com.example.employee_management_app.service.PersonService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

import java.util.List;

@SpringBootApplication
@ImportResource("classpath:beans.xml")
public class EmployeeManagementAppApplication {
	@Bean
	public Company company() {
		String filePath = "src/main/resources/data.csv";
		return new Company(filePath);
	}

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(EmployeeManagementAppApplication.class, args);

		PersonService personService = context.getBean(PersonService.class);
		personService.displayKeyEmployees();

		System.out.println("\nWszyscy pracownicy:");
		List<Person> allEmployees = personService.getAllEmployees();
		allEmployees.forEach(System.out::println);

		System.out.println("\nPracownicy z firmy Dailymail:");
		List<Person> googleEmployees = personService.filterByCompany("Dailymail");
		googleEmployees.forEach(System.out::println);

		System.out.println("\nPracownicy posortowani według nazwiska:");
		List<Person> sortedByLastName = personService.sortByLastName();
		sortedByLastName.forEach(System.out::println);
	}
}
