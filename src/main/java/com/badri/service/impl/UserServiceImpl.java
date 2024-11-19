package com.badri.service.impl;

import com.badri.config.JwtService;
import com.badri.dto.PaginationRequestDto;
import com.badri.dto.UserRequestDto;
import com.badri.dto.UserResponseDTO;
import com.badri.entity.Role;
import com.badri.entity.User;
import com.badri.exception.UserNotFoundException;
import com.badri.repository.UserRepository;
import com.badri.service.UserService;
import com.communication.dto.ApiResponse;
import com.communication.dto.SendEmailDto;
import jakarta.mail.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RestTemplate restTemplate;
    @Override
    public UserResponseDTO createUser(UserRequestDto requestDto) throws IllegalArgumentException {

        if(userRepository.existsByEmail(requestDto.getEmail())){
            throw new IllegalArgumentException("This email is already exists");
        }

        if(userRepository.existsByPassword(requestDto.getPassword())){
            throw new IllegalArgumentException("This password is already exists");
        }

        if(userRepository.existsByPassword(requestDto.getPhoneNumber())){
            throw new IllegalArgumentException("This Number is already exists");
        }

        User users = User.builder()
                .id(requestDto.getId()).firstName(requestDto.getFirstName())
                .middleName(requestDto.getMiddleName()).lastName(requestDto.getLastName())
                .email(requestDto.getEmail()).phoneNumber(requestDto.getPhoneNumber())
                .password(passwordEncoder.encode("Pass@word123")).address(requestDto.getAddress())
                .isActive(true).gender(requestDto.getGender()).role(Role.USER).build();

        User savedUser = userRepository.saveAndFlush(users);

        String newToken = jwtService.generateToken(users);
        String url="http://localhost:9000/users/reset-passwords/"+newToken;

        SendEmailDto sendEmailDto=new SendEmailDto();
        sendEmailDto.setRecipient(savedUser.getEmail());
        sendEmailDto.setSubject("Welcome to Our Service!");

        sendEmailDto.setMessage("Dear " + savedUser.getFirstName()+" "+savedUser.getMiddleName()+" "+savedUser.getLastName() +""
                +",\n\n" + "Thank you for registering with us. We are excited to have you on board!"
                +"\n"+ "Your EmailId is : "+ users.getEmail() + "\n"+ "Your Click on the below link to reset your password : "+ url+
                "\n\nIf you have any questions, feel free to reach out to our support team.\n\n" +
                        "Best regards,\n" +
                        "Software Developers Team");
        sendEmailDto.setAttachment(null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SendEmailDto> requestEntity = new HttpEntity<>(sendEmailDto, headers);
        restTemplate.exchange("http://localhost:9002/api/mail/send", HttpMethod.POST, requestEntity, String.class);

        var jwtToken = jwtService.generateToken(users);
        UserResponseDTO responseDTO = UserResponseDTO.builder()
                .id(savedUser.getId()).firstName(savedUser.getFirstName())
                .middleName(savedUser.getMiddleName()).lastName(savedUser.getLastName())
                .email(savedUser.getEmail()).phoneNumber(savedUser.getPhoneNumber())
                .password(savedUser.getPassword()).address(savedUser.getAddress())
                .isActive(savedUser.isActive()).gender(savedUser.getGender())
                .token(jwtToken).createdOn(savedUser.getCreatedOn())
                .updatedOn(savedUser.getUpdatedOn()).build();
        return responseDTO;

    }

    @Override
    public PaginationRequestDto<UserResponseDTO> getAllUsers(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<User> paginatedUsers = userRepository.findAll(pageable);

        List<UserResponseDTO> userResponseDTOs = paginatedUsers.getContent()
                .stream()
                .map(user -> UserResponseDTO.builder()
                        .id(user.getId()).firstName(user.getFirstName())
                        .middleName(user.getMiddleName()).lastName(user.getLastName())
                        .email(user.getEmail()).phoneNumber(user.getPhoneNumber())
                        .password(passwordEncoder.encode(user.getPassword())).address(user.getAddress())
                        .isActive(user.isActive()).gender(user.getGender())
                        .createdOn(user.getCreatedOn()).updatedOn(user.getUpdatedOn())
                        .build()
                ).collect(Collectors.toList());

        return PaginationRequestDto.<UserResponseDTO>builder()
                .users(userResponseDTOs).page(paginatedUsers.getNumber())           // Current page
                .size(paginatedUsers.getSize()).totalPages(paginatedUsers.getTotalPages())
                .totalElements(paginatedUsers.getTotalElements()).isLast(paginatedUsers.isLast())
                .build();
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDto requestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User", "id", id));

        user.setFirstName(requestDto.getFirstName());
        user.setMiddleName(requestDto.getMiddleName());
        user.setLastName(requestDto.getLastName());
        user.setEmail(requestDto.getEmail());
        user.setPhoneNumber(requestDto.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setAddress(requestDto.getAddress());
        user.setActive(requestDto.isActive());

        User savedUser = userRepository.saveAndFlush(user);

        UserResponseDTO responseDTO = UserResponseDTO.builder()
                .id(savedUser.getId()).firstName(savedUser.getFirstName())
                .middleName(savedUser.getMiddleName()).lastName(savedUser.getLastName())
                .email(savedUser.getEmail()).phoneNumber(savedUser.getPhoneNumber())
                .password(savedUser.getPassword()).address(savedUser.getAddress())
                .isActive(savedUser.isActive()).gender(savedUser.getGender())
                .createdOn(savedUser.getCreatedOn()).updatedOn(savedUser.getUpdatedOn())
                .build();
        return responseDTO;
    }

    @Override
    public void deleteUserByUserId(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User", "id", id));

        userRepository.delete(user);

    }

}
