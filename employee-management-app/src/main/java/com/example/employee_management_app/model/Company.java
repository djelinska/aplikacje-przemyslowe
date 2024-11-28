package com.example.employee_management_app.model;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
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
        double salary = Double.parseDouble(data[3]);
        String currency = data[4];
        String country = data[5];
        String company = email.substring(email.indexOf('@') + 1, email.indexOf('.'));

        int id = getNextId();

        return new Person(id, firstName, lastName, email, salary, currency, country, capitalize(company));
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

    public Optional<Person> getEmployeeByEmail(String email) {
        return employees.stream().filter(e -> Objects.equals(e.getEmail(), email)).findFirst();
    }

    public Person addEmployee(Person newEmployee) {
        if (newEmployee.getCurrency() != null) {
            newEmployee.setCurrency(newEmployee.getCurrency().toUpperCase());
        }

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
            person.setSalary(newEmployee.getSalary());
            person.setCurrency(newEmployee.getCurrency());
            person.setCountry(newEmployee.getCountry());
            person.setCompany(newEmployee.getCompany());

            return person;
        } else {
            throw new NoSuchElementException();
        }
    }

    public void updateEmployeeByEmail(String email, Person newEmployee) {
        Optional<Person> existingPerson = getEmployeeByEmail(email);

        if (existingPerson.isPresent()) {
            Person person = existingPerson.get();
            person.setFirstName(newEmployee.getFirstName());
            person.setLastName(newEmployee.getLastName());
            person.setEmail(newEmployee.getEmail());
            person.setSalary(newEmployee.getSalary());
            person.setCurrency(newEmployee.getCurrency());
            person.setCountry(newEmployee.getCountry());
            person.setPhotoFilename(newEmployee.getPhotoFilename());
            if (newEmployee.getPhotoFilename() != null && !newEmployee.getPhotoFilename().isEmpty()) {
                person.setPhotoFilename(newEmployee.getPhotoFilename());
            }
        } else {
            throw new NoSuchElementException();
        }
    }

    public boolean deleteEmployee(int id) {
        return employees.removeIf(employee -> employee.getId() == id);
    }

    public boolean deleteEmployeeByEmail(String email) {
        return employees.removeIf(employee -> Objects.equals(employee.getEmail(), email));
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

    public Map<String, Double> calculateSalarySumByCurrency() {
        return employees.stream()
                .collect(Collectors.groupingBy(Person::getCurrency, Collectors.summingDouble(Person::getSalary)));
    }

    public List<String> getAllCountries() {
        return employees.stream()
                .map(Person::getCountry)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Person> filterByCountry(String country) {
        return employees.stream()
                .filter(employee -> employee.getCountry().equalsIgnoreCase(country))
                .collect(Collectors.toList());
    }

    public List<Person> getEmployeesByCountry(String country) {
        return (country == null || country.isEmpty()) ? employees : this.filterByCountry(country);
    }

    public boolean isEmailUnique(String email, String currentEmail) {
        return employees.stream()
                .noneMatch(employee -> !employee.getEmail().equals(currentEmail) && employee.getEmail().equals(email));
    }

    public double calculateTotalSalaryForCurrency(String currency) {
        return employees.stream()
                .filter(employee -> currency.equals(employee.getCurrency()))
                .mapToDouble(Person::getSalary)
                .sum();
    }

    public List<String> importData(MultipartFile file) throws IOException {
        List<String> errorMessages = new ArrayList<>();

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".csv") && !fileName.endsWith(".xlsx"))) {
            throw new IllegalArgumentException("Nieobsługiwany format pliku. Dozwolone: CSV, Excel.");
        }

        boolean isCsv = fileName.endsWith(".csv");
        List<String[]> dataRows = isCsv ? readCsv(file) : readExcel(file);

        List<Person> newEmployees = new ArrayList<>();

        for (int i = 0; i < dataRows.size(); i++) {
            String[] row = dataRows.get(i);
            if (row.length < 6) {
                errorMessages.add("Wiersz " + (i + 1) + ": Brak wymaganych kolumn.");
                continue;
            }

            try {
                Person person = mapToPerson(row);
                validatePerson(person);
                newEmployees.add(person);
            } catch (Exception e) {
                errorMessages.add("Wiersz " + (i + 1) + ": " + e.getMessage());
            }
        }

        if (!errorMessages.isEmpty()) {
            return errorMessages;
        }

        employees.clear();
        employees.addAll(newEmployees);

        return errorMessages;
    }

    private List<String[]> readCsv(MultipartFile file) throws IOException {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> rows = reader.readAll();
            if (rows.size() > 1) {
                return rows.subList(1, rows.size());
            } else {
                throw new IOException("Plik CSV nie zawiera danych do przetworzenia.");
            }
        } catch (CsvException e) {
            throw new IOException("Błąd w strukturze CSV", e);
        }
    }

    private List<String[]> readExcel(MultipartFile file) throws IOException {
        List<String[]> dataRows = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                dataRows.add(mapExcelRow(row));
            }
        }
        return dataRows;
    }

    private String[] mapExcelRow(Row row) {
        return new String[]{
                row.getCell(0).getStringCellValue(),
                row.getCell(1).getStringCellValue(),
                row.getCell(2).getStringCellValue(),
                String.valueOf(row.getCell(3).getNumericCellValue()),
                row.getCell(4).getStringCellValue(),
                row.getCell(5).getStringCellValue()
        };
    }

    private Person mapToPerson(String[] row) {
        return new Person(
                0,
                row[0],
                row[1],
                row[2],
                Double.parseDouble(row[3]),
                row[4],
                row[5],
                null,
                null,
                null
        );
    }

    private void validatePerson(Person person) {
        if (person.getFirstName() == null || person.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("Imię nie może być puste.");
        }
        if (person.getLastName() == null || person.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Nazwisko nie może być puste.");
        }
        if (person.getEmail() == null || !person.getEmail().contains("@")) {
            throw new IllegalArgumentException("Niepoprawny adres email.");
        }
        if (person.getSalary() <= 0) {
            throw new IllegalArgumentException("Wynagrodzenie musi być większe niż 0.");
        }
        if (person.getCurrency() == null || person.getCurrency().isEmpty()) {
            throw new IllegalArgumentException("Waluta nie może być pusta.");
        }
        if (person.getCountry() == null || person.getCountry().isEmpty()) {
            throw new IllegalArgumentException("Kraj pochodzenia nie może być pusty.");
        }
    }
}
