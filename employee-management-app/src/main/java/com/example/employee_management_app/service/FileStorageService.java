package com.example.employee_management_app.service;

import com.example.employee_management_app.model.Person;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FileStorageService {
    private final Path rootLocation = Paths.get("uploads");

    public String savePhoto(MultipartFile photo, String firstName, String lastName) throws IOException {
        Files.createDirectories(rootLocation);

        String timestamp = Instant.now().toEpochMilli() + "";
        String fileName = firstName + "_" + lastName + "_" + timestamp + ".jpg";
        Path targetLocation = rootLocation.resolve(fileName);

        File tempFile = new File(targetLocation.toUri());
        int targetWidth = 300;
        int targetHeight = 400;
        Thumbnails.of(photo.getInputStream())
                .size(targetWidth, targetHeight)
                .outputQuality(0.8)
                .toFile(tempFile);
        return fileName;
    }

    public void deletePhoto(String filename) {
        if (filename != null && !filename.isEmpty()) {
            Path fileToDelete = rootLocation.resolve(filename);
            try {
                Files.deleteIfExists(fileToDelete);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public File exportEmployeePhotosToZip() throws IOException {
        File tempZipFile = File.createTempFile("employee_photos", ".zip");
        try (FileOutputStream fos = new FileOutputStream(tempZipFile);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            Files.walk(rootLocation)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            ZipEntry zipEntry = new ZipEntry(file.getFileName().toString());
                            zipOut.putNextEntry(zipEntry);

                            byte[] buffer = new byte[1024];
                            int len;
                            try (InputStream fis = new FileInputStream(file.toFile())) {
                                while ((len = fis.read(buffer)) > 0) {
                                    zipOut.write(buffer, 0, len);
                                }
                            }
                            zipOut.closeEntry();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
        return tempZipFile;
    }

    public byte[] generateCsvReport(List<Person> employees, List<String> selectedColumns) throws IOException {
        StringWriter writer = new StringWriter();
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(selectedColumns.toArray(new String[0])));

        for (Person employee : employees) {
            List<String> row = new ArrayList<>();
            for (String column : selectedColumns) {
                switch (column) {
                    case "Imię" -> row.add(employee.getFirstName());
                    case "Nazwisko" -> row.add(employee.getLastName());
                    case "Adres email" -> row.add(employee.getEmail());
                    case "Wynagrodzenie" -> row.add(String.valueOf(employee.getSalary()));
                    case "Waluta wynagrodzenia" -> row.add(employee.getCurrency());
                    case "Kraj pochodzenia" -> row.add(employee.getCountry());
                }
            }
            csvPrinter.printRecord(row);
        }
        csvPrinter.flush();
        return writer.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] generateExcelReport(List<Person> employees, List<String> selectedColumns) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Employees");
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < selectedColumns.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(selectedColumns.get(i));
            }

            for (int i = 0; i < employees.size(); i++) {
                Person employee = employees.get(i);
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < selectedColumns.size(); j++) {
                    String column = selectedColumns.get(j);
                    Cell cell = row.createCell(j);
                    switch (column) {
                        case "Imię" -> cell.setCellValue(employee.getFirstName());
                        case "Nazwisko" -> cell.setCellValue(employee.getLastName());
                        case "Adres email" -> cell.setCellValue(employee.getEmail());
                        case "Wynagrodzenie" -> cell.setCellValue(employee.getSalary());
                        case "Waluta wynagrodzenia" -> cell.setCellValue(employee.getCurrency());
                        case "Kraj pochodzenia" -> cell.setCellValue(employee.getCountry());
                    }
                }
            }

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                workbook.write(out);
                return out.toByteArray();
            }
        }
    }
}
