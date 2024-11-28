package com.example.employee_management_app.controller;

import com.example.employee_management_app.model.Person;
import com.example.employee_management_app.service.FileStorageService;
import com.example.employee_management_app.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Controller
@RequestMapping("/employees")
public class PersonViewController {
    private final PersonService personService;
    private final FileStorageService fileStorageService;

    @Autowired
    public PersonViewController(PersonService personService, FileStorageService fileStorageService) {
        this.personService = personService;
        this.fileStorageService = fileStorageService;
    }

    private boolean isPhotoValid(MultipartFile photo, BindingResult result) {
        String contentType = photo.getContentType();
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            result.rejectValue("photo", "error.photo", "Nieobsługiwany format pliku. Dozwolone: JPG, PNG.");
            return false;
        } else if (photo.getSize() > 1024 * 1024) {
            result.rejectValue("photo", "error.photo", "Plik jest za duży. Maksymalny rozmiar: 1 MB.");
            return false;
        }
        return true;
    }

    private void deleteOldPhotoIfExists(String email) {
        personService.getEmployeeByEmail(email)
                .ifPresent(emp -> {
                    String currentPhotoFilename = emp.getPhotoFilename();
                    if (currentPhotoFilename != null && !currentPhotoFilename.isEmpty()) {
                        fileStorageService.deletePhoto(currentPhotoFilename);
                    }
                });
    }

    private void populateCountriesAndCurrencies(Model model) {
        model.addAttribute("countries", personService.getAllUniqueCountries());
        model.addAttribute("currencies", List.of("EUR", "GBP", "USD", "JPY"));
    }

    @GetMapping
    public String listEmployees(@RequestParam(value = "country", required = false) String country, Model model) {
        model.addAttribute("employees", personService.getEmployeesByCountry(country));
        model.addAttribute("totalEmployees", personService.getEmployeesByCountry(country).size());
        model.addAttribute("salarySumByCurrency", personService.calculateSalarySumByCurrency());
        model.addAttribute("countries", personService.getAllCountries());
        model.addAttribute("selectedCountry", country);
        return "employees/list";
    }

    @GetMapping("/{email}")
    public String getEmployeeByEmail(@PathVariable String email, Model model) {
        return personService.getEmployeeByEmail(email)
                .map(person -> {
                    model.addAttribute("employee", person);
                    return "employees/details";
                })
                .orElseGet(() -> {
                    model.addAttribute("errorMessage", "Pracownik o podanym adresie e-mail nie istnieje.");
                    return "employees/list";
                });
    }

    @GetMapping("/add")
    public String showAddEmployeeForm(Model model) {
        model.addAttribute("employee", new Person());
        populateCountriesAndCurrencies(model);
        return "employees/add";
    }

    @PostMapping("/add")
    public String processAddEmployee(@Valid @ModelAttribute("employee") Person employee, BindingResult result,
                                     @RequestParam("photo") MultipartFile photo, RedirectAttributes redirectAttributes, Model model) {
        if (personService.isEmailUnique(employee.getEmail(), null)) {
            result.rejectValue("email", "error.email", "Adres e-mail musi być unikalny.");
        }

        if (!photo.isEmpty() && isPhotoValid(photo, result)) {
            try {
                String savedFilename = fileStorageService.savePhoto(photo, employee.getFirstName(), employee.getLastName());
                employee.setPhotoFilename(savedFilename);
            } catch (IOException e) {
                result.rejectValue("photo", "error.photo", "Błąd podczas zapisywania zdjęcia.");
            }
        }

        if (result.hasErrors()) {
            populateCountriesAndCurrencies(model);
            return "employees/add";
        }

        personService.addEmployee(employee);
        redirectAttributes.addFlashAttribute("successMessage", "Pracownik został pomyślnie dodany.");
        return "redirect:/employees";
    }

    @GetMapping("/edit/{email}")
    public String showEditEmployeeForm(@PathVariable String email, Model model) {
        return personService.getEmployeeByEmail(email)
                .map(employee -> {
                    model.addAttribute("employee", employee);
                    populateCountriesAndCurrencies(model);
                    return "employees/edit";
                })
                .orElse("redirect:/employees");
    }

    @PostMapping("/edit/{email}")
    public String processEditEmployee(@PathVariable String email, @Valid @ModelAttribute("employee") Person employee,
                                      @RequestParam("photo") MultipartFile photo, BindingResult result,
                                      RedirectAttributes redirectAttributes, Model model) {
        if (personService.isEmailUnique(employee.getEmail(), email)) {
            result.rejectValue("email", "error.email", "Adres e-mail musi być unikalny.");
        }

        if (!photo.isEmpty() && isPhotoValid(photo, result)) {
            deleteOldPhotoIfExists(email);
            try {
                String savedFilename = fileStorageService.savePhoto(photo, employee.getFirstName(), employee.getLastName());
                employee.setPhotoFilename(savedFilename);
            } catch (IOException e) {
                result.rejectValue("photo", "error.photo", "Błąd podczas zapisywania zdjęcia.");
            }
        }

        if (result.hasErrors()) {
            populateCountriesAndCurrencies(model);
            return "employees/edit";
        }

        personService.updateEmployeeByEmail(email, employee);
        redirectAttributes.addFlashAttribute("successMessage", "Pracownik został pomyślnie zaktualizowany.");
        return "redirect:/employees";
    }

    @PostMapping("/delete/{email}")
    public String deleteEmployee(@PathVariable String email, RedirectAttributes redirectAttributes) {
        personService.getEmployeeByEmail(email).ifPresentOrElse(employee -> {
            fileStorageService.deletePhoto(employee.getPhotoFilename());
            if (personService.deleteEmployeeByEmail(email)) {
                redirectAttributes.addFlashAttribute("successMessage", "Pracownik został pomyślnie usunięty.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Wystąpił błąd podczas usuwania pracownika.");
            }
        }, () -> redirectAttributes.addFlashAttribute("errorMessage", "Pracownik o podanym e-mailu nie istnieje."));

        return "redirect:/employees";
    }

    @GetMapping("/export-photos")
    public ResponseEntity<byte[]> exportEmployeePhotos() {
        try {
            File zipFile = fileStorageService.exportEmployeePhotosToZip();
            byte[] zipContent = Files.readAllBytes(zipFile.toPath());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=employee_photos.zip");
            headers.add("Content-Type", "application/zip");
            return new ResponseEntity<>(zipContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Błąd podczas generowania pliku ZIP".getBytes());
        }
    }

    @PostMapping("/generate-report")
    public ResponseEntity<byte[]> generateReport(@RequestParam("format") String format, @RequestParam("selectedColumns") List<String> selectedColumns) {
        try {
            byte[] reportContent;
            String fileName;

            if ("csv".equalsIgnoreCase(format)) {
                reportContent = fileStorageService.generateCsvReport(personService.getAllEmployees(), selectedColumns);
                fileName = "employees_report.csv";
            } else if ("excel".equalsIgnoreCase(format)) {
                reportContent = fileStorageService.generateExcelReport(personService.getAllEmployees(), selectedColumns);
                fileName = "employees_report.xlsx";
            } else {
                throw new IllegalArgumentException("Nieobsługiwany format raportu: " + format);
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType("csv".equalsIgnoreCase(format) ? MediaType.TEXT_PLAIN : MediaType.APPLICATION_OCTET_STREAM)
                    .body(reportContent);
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas generowania raportu", e);
        }
    }

    @PostMapping("/import")
    public String importData(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            List<String> errors = personService.importData(file);
            if (!errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessages", errors);
            } else {
                redirectAttributes.addFlashAttribute("successMessage", "Dane zostały pomyślnie zaimportowane.");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas importu: " + e.getMessage());
        }

        return "redirect:/employees";
    }
}
