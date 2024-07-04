package com.ironhack.taskithub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.taskithub.dto.UserDTO;
import com.ironhack.taskithub.enums.Role;
import com.ironhack.taskithub.model.User;
import com.ironhack.taskithub.repository.UserRepository;
import com.ironhack.taskithub.security.filters.CustomAuthenticationFilter;
import com.ironhack.taskithub.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserControllerTest
 */

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();

        testUser = new User();
        testUser.setName("Test User");
        testUser.setUsername("testuser");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setRole(Role.ADMIN);
        testUser = userRepository.save(testUser);

        // Obtain auth token
        CustomAuthenticationFilter filter = new CustomAuthenticationFilter(authenticationManager);
        MvcResult result = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        authToken = objectMapper.readTree(response).get("access_token").asText();
    }

    @Test
    void createUser() throws Exception {
        UserDTO newUser = new UserDTO();
        newUser.setName("New User");
        newUser.setUsername("newuser");
        newUser.setPassword("password");
        newUser.setRole(Role.USER.name());

        mockMvc.perform(post("/users")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New User"))
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    void getAllUsers() throws Exception {
        mockMvc.perform(get("/users")
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test User"));
    }

    @Test
    void getUserById() throws Exception {
        mockMvc.perform(get("/users/" + testUser.getId())
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void updateUser() throws Exception {
        UserDTO updateUser = new UserDTO();
        updateUser.setName("Updated User");

        mockMvc.perform(put("/users/" + testUser.getId())
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated User"));
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/" + testUser.getId())
                .header("Authorization", authToken))
                .andExpect(status().isNoContent());
    }
}
