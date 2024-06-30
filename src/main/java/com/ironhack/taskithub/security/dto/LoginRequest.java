package com.ironhack.taskithub.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginRequest
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    private String username;
    private String password;

    
}
