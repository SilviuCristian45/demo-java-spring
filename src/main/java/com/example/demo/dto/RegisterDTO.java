package com.example.demo.dto;

import com.example.demo.utils.Role;
import com.example.demo.utils.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private Role role;
}
