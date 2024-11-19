package com.badri.service;

import com.badri.dto.PaginationRequestDto;
import com.badri.dto.UserRequestDto;
import com.badri.dto.UserResponseDTO;

import java.util.List;

public interface UserService {

    UserResponseDTO createUser(UserRequestDto userCreateDTO) throws IllegalAccessException;
    PaginationRequestDto<UserResponseDTO> getAllUsers(int page, int size);
    UserResponseDTO updateUser(Long id,UserRequestDto requestDto);
    void deleteUserByUserId(Long id);
}
