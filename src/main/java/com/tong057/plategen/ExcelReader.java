package com.tong057.plategen;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelReader {

    public static Map<String, Map<String, String>> readVoivodeshipsAndTownshipsWithCodes() {
        Map<String, Map<String, String>> voivodeshipsTownships = new HashMap<>();
        String excelFilePath = "/excel/townships.xlsx";

        try (InputStream inputStream = ExcelReader.class.getResourceAsStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                Map<String, String> townshipsCodes = new HashMap<>();
                String voivodeshipName = sheet.getSheetName();
                String townshipName = null;
                String townshipCode = null;

                for (Row row : sheet) {
                    Cell cell = row.getCell(0);
                    if (cell != null) {
                        townshipName = cell.getStringCellValue();
                    }

                    cell = row.getCell(1);
                    if (cell != null) {
                        townshipCode = cell.getStringCellValue();
                    }

                    townshipsCodes.put(townshipName, townshipCode);
                }

                voivodeshipsTownships.put(voivodeshipName, townshipsCodes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return voivodeshipsTownships;
    }

    public static List<Registration> readRegistrations() {
        List<Registration> registrations = new ArrayList<>();
        String excelFilePath = "/excel/registrations.xlsx";
        String sheetName = "Registrations";
        int startRow = 1;

        try (InputStream inputStream = ExcelReader.class.getResourceAsStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheet(sheetName);

            for (int rowIndex = startRow; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                String brand = row.getCell(0).getStringCellValue();
                String model = row.getCell(1).getStringCellValue();
                int yearProd = (int) row.getCell(2).getNumericCellValue();
                Vehicle vehicle = new Vehicle(brand, model, yearProd);

                String voivodeship = row.getCell(3).getStringCellValue();
                String township = row.getCell(4).getStringCellValue();
                String format = row.getCell(5).getStringCellValue();
                String plateNumber = row.getCell(6).getStringCellValue();
                int cost = (int) row.getCell(7).getNumericCellValue();
                String imagePath = row.getCell(8).getStringCellValue();
                Plate plate = new Plate(plateNumber, format, voivodeship, township, cost, imagePath);

                Registration registration = new Registration(plate, vehicle);
                registrations.add(registration);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return registrations;
    }


}
