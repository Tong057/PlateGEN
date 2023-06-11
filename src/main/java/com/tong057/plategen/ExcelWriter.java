package com.tong057.plategen;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.List;

public class ExcelWriter {
    public static void writeRegistrationsToExcel(List<Registration> registrationList) {
        String excelFileName = "src/main/resources/excel/registrations.xlsx"; // Relative path to the Excel file

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Registrations");

            // Create column headers
            String[] headers = new String[]{"Brand", "Model", "Year of production", "Voivodeship", "Township", "Format", "Plate Number", "Cost", "Image Path"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Write registration fields to separate rows
            for (int i = 0; i < registrationList.size(); i++) {
                Registration registration = registrationList.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(registration.getVehicle().getBrand());
                row.createCell(1).setCellValue(registration.getVehicle().getModel());
                row.createCell(2).setCellValue(registration.getVehicle().getYearProd());
                row.createCell(3).setCellValue(registration.getPlate().getVoivodeship());
                row.createCell(4).setCellValue(registration.getPlate().getTownship());
                row.createCell(5).setCellValue(registration.getPlate().getFormat());
                row.createCell(6).setCellValue(registration.getPlate().getPlateNumber());
                row.createCell(7).setCellValue(registration.getPlate().getCost());
                row.createCell(8).setCellValue(registration.getPlate().getImagePath());
            }

            // Automatically adjust column widths
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Get the absolute file path
            String projectDirectory = System.getProperty("user.dir");
            String excelFilePath = projectDirectory + "/" + excelFileName;

            // Save the workbook to a file
            try (OutputStream outputStream = new FileOutputStream(excelFilePath)) {
                workbook.write(outputStream);
                System.out.println("Registrations have been written to Excel file: " + excelFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}