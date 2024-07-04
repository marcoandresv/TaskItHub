package com.ironhack.taskithub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.taskithub.dto.UserDTO;
import com.ironhack.taskithub.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserControllerUnitTest
 */
@SpringBootTest
class UserControllerUnitTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private UserService userService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void createUser_validUserDTO_userCreated() throws Exception {
        UserDTO inputDTO = new UserDTO();
        inputDTO.setUsername("newuser");
        inputDTO.setPassword("password");

        UserDTO outputDTO = new UserDTO();
        outputDTO.setId(1L);
        outputDTO.setUsername("newuser");

        when(userService.createUser(any(UserDTO.class))).thenReturn(outputDTO);

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(inputDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    void getUserByUsername_existingUsername_userReturned() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");

        when(userService.getUserByUsername("testuser")).thenReturn(userDTO);

        mockMvc.perform(get("/users/username/{username}", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void getUserById_existingId_userReturned() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");

        when(userService.getUserById(1L)).thenReturn(userDTO);

        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void getAllUsers_usersExist_listReturned() throws Exception {
        UserDTO user1 = new UserDTO();
        user1.setId(1L);
        user1.setUsername("user1");

        UserDTO user2 = new UserDTO();
        user2.setId(2L);
        user2.setUsername("user2");

        List<UserDTO> users = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    void updateUser_existingId_userUpdated() throws Exception {
        UserDTO inputDTO = new UserDTO();
        inputDTO.setUsername("updateduser");

        UserDTO outputDTO = new UserDTO();
        outputDTO.setId(1L);
        outputDTO.setUsername("updateduser");

        when(userService.updateUserFromDTO(eq(1L), any(UserDTO.class))).thenReturn(outputDTO);

        mockMvc.perform(put("/users/{id}", 1L)
                .content(objectMapper.writeValueAsString(inputDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("updateduser"));
    }

    @Test
    void deleteUser_existingId_userDeleted() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }
}
