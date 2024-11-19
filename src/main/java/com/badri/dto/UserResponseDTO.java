package com.badri.dto;

import lombok.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;
    private String address;
    private boolean isActive;
    private String gender;
    private Date createdOn;
    private Date updatedOn;
    private String token;
}
