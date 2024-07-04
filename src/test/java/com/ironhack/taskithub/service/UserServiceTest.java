package com.ironhack.taskithub.service;

import com.ironhack.taskithub.dto.UserDTO;
import com.ironhack.taskithub.enums.Role;
import com.ironhack.taskithub.model.Department;
import com.ironhack.taskithub.model.Task;
import com.ironhack.taskithub.model.User;
import com.ironhack.taskithub.repository.DepartmentRepository;
import com.ironhack.taskithub.repository.TaskRepository;
import com.ironhack.taskithub.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserServiceTest
 */
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TaskRepository taskRepository;

    private User testUser;
    private Department testDepartment;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testDepartment = new Department();
        testDepartment.setName("Test Department");
        testDepartment = departmentRepository.save(testDepartment);

        testUser = new User();
        testUser.setName("Test User");
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setRole(Role.USER);
        testUser.setDepartment(testDepartment);
        testUser = userRepository.save(testUser);

        testTask = new Task();
        testTask.setTitle("Test Task");
        testTask.setCreatedBy(testUser);
        testTask = taskRepository.save(testTask);
    }

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    @Test
    @Transactional
    void loadUserByUsername_existingUsername_returnsUserDetails() {
        UserDetails userDetails = userService.loadUserByUsername("testuser");
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
    }

    @Test
    @Transactional
    void loadUserByUsername_nonExistingUsername_throwsException() {
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nonexistentuser"));
    }

    @Test
    @Transactional
    void createUser_validInput_userCreated() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("New User");
        userDTO.setUsername("newuser");
        userDTO.setPassword("password");
        userDTO.setRole(Role.USER.name());
        userDTO.setDepartmentId(testDepartment.getId());

        UserDTO createdUser = userService.createUser(userDTO);

        assertNotNull(createdUser);
        assertEquals("New User", createdUser.getName());
        assertEquals("newuser", createdUser.getUsername());
        assertEquals(Role.USER.name(), createdUser.getRole());
        assertEquals(testDepartment.getId(), createdUser.getDepartmentId());
    }

    @Test
    @Transactional
    void getUserByUsername_existingUsername_returnsUser() {
        UserDTO foundUser = userService.getUserByUsername("testuser");
        assertNotNull(foundUser);
        assertEquals(testUser.getName(), foundUser.getName());
    }

    @Test
    @Transactional
    void getUserByUsername_nonExistingUsername_throwsException() {
        assertThrows(ResponseStatusException.class, () -> userService.getUserByUsername("nonexistentuser"));
    }

    @Test
    @Transactional
    void getUserById_existingId_returnsUser() {
        UserDTO foundUser = userService.getUserById(testUser.getId());
        assertNotNull(foundUser);
        assertEquals(testUser.getName(), foundUser.getName());
    }

    @Test
    @Transactional
    void getUserById_nonExistingId_throwsException() {
        assertThrows(ResponseStatusException.class, () -> userService.getUserById(999L));
    }

    @Test
    @Transactional
    void getAllUsers_returnsAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        assertFalse(users.isEmpty());
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals(testUser.getUsername())));
    }

    @Test
    @Transactional
    void updateUser_validInput_userUpdated() {
        UserDTO updateDTO = new UserDTO();
        updateDTO.setName("Updated User");
        updateDTO.setRole(Role.ADMIN.name());

        User updatedUser = userService.updateUser(testUser.getId(), updateDTO);

        assertNotNull(updatedUser);
        assertEquals("Updated User", updatedUser.getName());
        assertEquals(Role.ADMIN, updatedUser.getRole());
    }

    
    @Test
    @Transactional
    void deleteUser_existingId_userDeleted() {
        List<Task> userTasks = taskRepository.findByAssignedUsers_Id(testUser.getId());
        
        userService.deleteUser(testUser.getId());
        
        assertTrue(userRepository.findById(testUser.getId()).isEmpty());
        assertTrue(taskRepository.findByAssignedUsers_Id(testUser.getId()).isEmpty());
        assertTrue(userTasks.isEmpty());

        for (Task task : userTasks) {
            Task foundTask = taskRepository.findById(task.getId()).orElse(null);
            assertNotNull(foundTask);
            assertNull(foundTask.getAssignedUsers());
            assertNull(foundTask.getCreatedBy());
        }

    }

    @Test
    @Transactional
    void existsByUsername_existingUsername_returnsTrue() {
        assertTrue(userService.existsByUsername("testuser"));
    }

    @Test
    @Transactional
    void existsByUsername_nonExistingUsername_returnsFalse() {
        assertFalse(userService.existsByUsername("nonexistentuser"));
    }
}
