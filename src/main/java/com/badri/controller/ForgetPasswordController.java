package com.badri.controller;

import com.badri.dto.ApiResponse;
import com.badri.service.ForgetPasswordService;
import com.badri.util.EmailVerifyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/passwords")
public class ForgetPasswordController {
    @Autowired
    private ForgetPasswordService forgetPasswordService;
    @PostMapping("/send")
    public ResponseEntity<ApiResponse> sendVerificationEmail(
            @RequestParam("token") String token,
            @RequestBody EmailVerifyRequest emailVerifyRequest){
        ApiResponse apiResponse = forgetPasswordService.sendVerificationEmail(token, emailVerifyRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.ACCEPTED);
    }

}
