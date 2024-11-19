package com.badri.service.impl;

import com.badri.config.JwtService;
import com.badri.dto.ApiResponse;
import com.badri.entity.User;
import com.badri.repository.UserRepository;
import com.badri.service.ForgetPasswordService;
import com.badri.util.EmailVerifyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
@Service
public class ForgetPasswordServiceImpl implements ForgetPasswordService {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Override
    public ApiResponse sendVerificationEmail(String token, EmailVerifyRequest request) {
        try {
            // Extract userId from the token
            Long tokenUserId = jwtService.extractClaimByKey(token,"userId", Long.class);

            // Find the user by emailId
            Optional<User> userOptional = userRepository.findByEmail(request.getEmail().trim());

            // Check if user exists
            if (userOptional.isEmpty()) {
                return ApiResponse.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .message("User with email " + request.getEmail() + " not found.")
                        .build();
            }

            // Get the user object from the Optional
            User user = userOptional.get();

            // Check if the userId from the token matches the userId of the found user
            if (!tokenUserId.equals(user.getId())) {
                return ApiResponse.builder()
                        .httpStatus(HttpStatus.NOT_ACCEPTABLE)
                        .message("Provided email and token userId do not match.")
                        .build();
            }

            // Generate a new token for the user (usually JWT with the userId)
            String newToken = jwtService.generateToken(user);

            // Prepare headers and body for the email request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("email", request.getEmail());
            body.add("userId", newToken); // Attach the newly generated token

            // Create HttpEntity for the request
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

            // Make the POST request to send the verification email
            ResponseEntity<ApiResponse> response = restTemplate.exchange(
                    "http://localhost:9002/api/mail/send",
                    HttpMethod.POST, requestEntity, ApiResponse.class
            );

            // Return the response from the mail service
            return response.getBody();

        } catch (Exception e) {
            // Handle any exception
            return ApiResponse.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

}
