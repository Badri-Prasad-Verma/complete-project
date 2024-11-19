package com.badri.util;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

@Service
public class ExcelExportService {

    public <T> void exportToExcel(List<T> entityList, HttpServletResponse response) throws IOException {
        if (entityList == null || entityList.isEmpty()) {
            throw new IllegalArgumentException("No data available to export.");
        }

        // Create the Excel workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        // Create the header row with bold style
        Row headerRow = sheet.createRow(0);
        T firstEntity = entityList.get(0);
        Field[] fields = firstEntity.getClass().getDeclaredFields();

        // Create a bold font and apply it to the header cell style
        CellStyle headerStyle = workbook.createCellStyle();
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        headerStyle.setFont(boldFont);

        int headerIndex = 0;
        for (Field field : fields) {
            field.setAccessible(true); // Allow access to private fields

            // Capitalize the first letter of the header
            String headerText = capitalizeFirstLetter(field.getName());

            // Create a header cell with the capitalized name
            Cell headerCell = headerRow.createCell(headerIndex++);
            headerCell.setCellValue(headerText);
            headerCell.setCellStyle(headerStyle); // Apply bold style to header
        }

        // Fill the data rows
        int rowIndex = 1;
        for (T entity : entityList) {
            Row dataRow = sheet.createRow(rowIndex++);
            int cellIndex = 0;
            for (Field field : fields) {
                field.setAccessible(true);
                Cell dataCell = dataRow.createCell(cellIndex++);
                try {
                    Object value = field.get(entity);
                    if (value != null) {
                        dataCell.setCellValue(value.toString());
                    } else {
                        dataCell.setCellValue("");
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Error while accessing field values.", e);
                }
            }
        }

        // Set response headers
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String entityClassName = entityList.get(0).getClass().getSimpleName();
        response.setHeader("Content-Disposition", "attachment; filename=" + entityClassName + "_report.xlsx");

        // Write to output stream
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    // Utility method to capitalize the first letter of a string
    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
