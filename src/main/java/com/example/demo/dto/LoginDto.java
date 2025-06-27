package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                   // Generează getteri, setteri, toString, equals, hashCode
@Builder                // Permite construirea obiectelor folosind pattern-ul Builder
@NoArgsConstructor      // Generează constructor fără parametri
@AllArgsConstructor     // Generează constructor cu toți parametrii
public class LoginDto {
    private String username;
    private String password;
}