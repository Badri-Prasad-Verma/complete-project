package com.badri.service.impl;

import com.badri.dto.LoginRequestDto;
import com.badri.entity.User;
import com.badri.repository.UserRepository;
import com.badri.service.LoginService;
import com.communication.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public ApiResponse loginRequest(LoginRequestDto loginRequestDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequestDto.getEmail(), loginRequestDto.getPassword()));

        User user = userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(()->new RuntimeException("User Not found"));

        return ApiResponse.builder().httpStatus(HttpStatus.ACCEPTED)
                .message("User Login Successfully")
                .build();
    }
}
