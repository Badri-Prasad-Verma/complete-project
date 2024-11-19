package com.badri.service;

import com.badri.dto.ApiResponse;
import com.badri.util.EmailVerifyRequest;

public interface ForgetPasswordService {

    public ApiResponse sendVerificationEmail(String token, EmailVerifyRequest emailVerifyRequest);

}
