package com.badri.controller;

import com.badri.dto.PaginationRequestDto;
import com.badri.dto.UserRequestDto;
import com.badri.dto.UserResponseDTO;
import com.badri.service.UserService;
import com.badri.util.ExcelExportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private ExcelExportService excelExportService;

    @PostMapping("/create")
    ResponseEntity<?> createNewUser(
            @Valid @RequestBody UserRequestDto requestDto,
            BindingResult result) throws IllegalAccessException {
        if(result.hasErrors()){
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        UserResponseDTO users = userService.createUser(requestDto);
        return new ResponseEntity<>(users, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<PaginationRequestDto<UserResponseDTO>> getAllUsers(
            @RequestParam(value = "page",defaultValue = "0",required = false) int page,
            @RequestParam(value = "size",defaultValue = "10",required = false) int size
    ) {
        return ResponseEntity.ok(userService.getAllUsers(page,size));
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<UserResponseDTO> updateUsers(@PathVariable Long userId, @RequestBody UserRequestDto requestDto) {
        UserResponseDTO userResponseDTO = userService.updateUser(userId, requestDto);
        return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUsers(@PathVariable Long userId) {
        userService.deleteUserByUserId(userId);
        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    }

}