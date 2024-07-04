package com.ironhack.taskithub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.taskithub.dto.TaskDTO;
import com.ironhack.taskithub.enums.Priority;
import com.ironhack.taskithub.enums.Status;
import com.ironhack.taskithub.model.Department;
import com.ironhack.taskithub.model.Task;
import com.ironhack.taskithub.model.User;
import com.ironhack.taskithub.repository.DepartmentRepository;
import com.ironhack.taskithub.repository.TaskRepository;
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

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * TaskControllerTest
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Task testTask;
    private User testUser;
    private Department testDepartment;
    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        departmentRepository.deleteAll();

        Department department = new Department();
        department.setName("Test Department");
        testDepartment = departmentRepository.save(department);

        User user = new User();
        user.setName("Test User");
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(Role.ADMIN);
        testUser = userRepository.save(user);

        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setDepartment(testDepartment);
        task.setCreatedBy(testUser);
        task.setPriority(Priority.MEDIUM);
        task.setStatus(Status.NOT_STARTED);
        task.setDueDate(LocalDateTime.now().plusDays(7));
        testTask = taskRepository.save(task);

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
    void createTask() throws Exception {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("New Task");
        taskDTO.setDescription("New Description");
        taskDTO.setPriority(Priority.HIGH);
        taskDTO.setStatus(Status.IN_PROGRESS);
        taskDTO.setDueDate(LocalDateTime.now().plusDays(14));
        taskDTO.setDepartmentId(testDepartment.getId());
        taskDTO.setCreatedById(testUser.getId());

        mockMvc.perform(post("/tasks")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Task"));
    }

    @Test
    void getTasksByDepartment() throws Exception {
        mockMvc.perform(get("/tasks/department/" + testDepartment.getId())
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    void getTasksCreatedByUser() throws Exception {
        mockMvc.perform(get("/tasks/created-by/" + testUser.getId())
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    void getTaskById() throws Exception {
        mockMvc.perform(get("/tasks/" + testTask.getId())
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void getAllTasks() throws Exception {
        mockMvc.perform(get("/tasks")
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    void updateTask() throws Exception {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Updated Task");

        mockMvc.perform(put("/tasks/" + testTask.getId())
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"));
    }

    @Test
    void deleteTask() throws Exception {
        mockMvc.perform(delete("/tasks/" + testTask.getId())
                .header("Authorization", authToken))
                .andExpect(status().isNoContent());
    }
}
