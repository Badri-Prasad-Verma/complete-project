package com.badri.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

@Service
public class PdfExportService {

    public <T> void exportToPdf(List<T> entityList, HttpServletResponse response) throws IOException, DocumentException {
        if (entityList == null || entityList.isEmpty()) {
            throw new IllegalArgumentException("No data available to export.");
        }

        // Set the response content type and header for PDF download
        response.setContentType("application/pdf");
        String entityClassName = entityList.get(0).getClass().getSimpleName();
        response.setHeader("Content-Disposition", "attachment; filename=" + entityClassName + "_report.pdf");

        // Create the PDF document
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Add title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph(entityClassName + " Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        // Create the table based on the number of fields
        T firstEntity = entityList.get(0);
        Field[] fields = firstEntity.getClass().getDeclaredFields();
        PdfPTable table = new PdfPTable(fields.length);
        table.setWidthPercentage(100);

        // Add table header row with bold font and capitalized first letters
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        for (Field field : fields) {
            field.setAccessible(true); // Allow access to private fields

            // Capitalize the first letter of the field name
            String headerText = capitalizeFirstLetter(field.getName());

            // Create a bold header cell
            PdfPCell headerCell = new PdfPCell(new Phrase(headerText, headerFont));
            headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(headerCell);
        }

        // Add table data rows
        for (T entity : entityList) {
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(entity);
                    table.addCell(value != null ? value.toString() : "");
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Error while accessing field values.", e);
                }
            }
        }

        // Add the table to the document
        document.add(table);

        // Close the document
        document.close();
    }

    // Utility method to capitalize the first letter of a string
    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
