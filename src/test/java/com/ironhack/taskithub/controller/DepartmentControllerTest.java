package com.ironhack.taskithub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.taskithub.dto.DepartmentDTO;
import com.ironhack.taskithub.model.Department;
import com.ironhack.taskithub.model.User;
import com.ironhack.taskithub.repository.DepartmentRepository;
import com.ironhack.taskithub.repository.UserRepository;
import com.ironhack.taskithub.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * DepartmentControllerTest
 */
@SpringBootTest
@AutoConfigureMockMvc
public class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Department testDepartment;
    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        departmentRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        User user = new User();
        user.setName("Test User");
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(Role.ADMIN);
        userRepository.save(user);

        Department department = new Department();
        department.setName("Test Department");
        testDepartment = departmentRepository.save(department);

        // Obtain auth token
        MvcResult result = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        authToken = objectMapper.readTree(response).get("access_token").asText();
    }

    @Test
    void createDepartment() throws Exception {
        DepartmentDTO departmentDTO = new DepartmentDTO();
        departmentDTO.setName("New Department");

        mockMvc.perform(post("/departments")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(departmentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Department"));
    }

    @Test
    void getDepartmentById() throws Exception {
        mockMvc.perform(get("/departments/" + testDepartment.getId())
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Department"));
    }

    @Test
    void getAllDepartments() throws Exception {
        mockMvc.perform(get("/departments")
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Department"));
    }

    @Test
    void updateDepartment() throws Exception {
        DepartmentDTO departmentDTO = new DepartmentDTO();
        departmentDTO.setName("Updated Department");

        mockMvc.perform(put("/departments/" + testDepartment.getId())
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(departmentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Department"));
    }

    @Test
    void deleteDepartment() throws Exception {
        mockMvc.perform(delete("/departments/" + testDepartment.getId())
                .header("Authorization", authToken))
                .andExpect(status().isNoContent());
    }
}
