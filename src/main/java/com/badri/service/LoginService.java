package com.badri.service;

import com.badri.dto.LoginRequestDto;
import com.communication.dto.ApiResponse;

public interface LoginService {

    ApiResponse loginRequest(LoginRequestDto loginRequestDto);

}
