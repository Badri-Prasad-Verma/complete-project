package com.badri.controller;

import com.badri.dto.UserResponseDTO;
import com.badri.service.UserService;
import com.badri.util.ExcelExportService;
import com.badri.util.PdfExportService;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private UserService userService;
    @Autowired
    private PdfExportService pdfExportService;
    @Autowired
    private ExcelExportService excelExportService;

    @GetMapping("/excel-report/download")
    public void downloadUserReport(
            @RequestParam(value = "page",defaultValue = "0",required = false) int page,
            @RequestParam(value = "size",defaultValue = "10",required = false) int size,
            HttpServletResponse response) throws IOException {
        List<UserResponseDTO> users = (List<UserResponseDTO>) userService.getAllUsers(page,size);
        excelExportService.exportToExcel(users, response);
    }

    @GetMapping("/pdf-report/download")
    public void downloadUserPdf(
            @RequestParam(value = "page",defaultValue = "0",required = false) int page,
            @RequestParam(value = "size",defaultValue = "10",required = false) int size,
            HttpServletResponse response) throws IOException, DocumentException, DocumentException {
        List<UserResponseDTO> users = (List<UserResponseDTO>) userService.getAllUsers(page,size);
        pdfExportService.exportToPdf(users, response);
    }

}
