package com.ironhack.taskithub.service;

import com.ironhack.taskithub.dto.UserDTO;
import com.ironhack.taskithub.enums.Role;
import com.ironhack.taskithub.model.User;
import com.ironhack.taskithub.repository.DepartmentRepository;
import com.ironhack.taskithub.repository.TaskRepository;
import com.ironhack.taskithub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserServiceTest
 */
@SpringBootTest
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_existingUsername_returnsUserDetails() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(Role.USER);

        when(userRepository.findByUsername("testuser")).thenReturn(List.of(user));

        UserDetails userDetails = userService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("USER")));
    }

    @Test
    void loadUserByUsername_nonExistingUsername_throwsUsernameNotFoundException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(List.of());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nonexistent"));
    }

    @Test
    void createUser_validUserDTO_returnsCreatedUserDTO() {
        UserDTO inputDTO = new UserDTO();
        inputDTO.setUsername("newuser");
        inputDTO.setPassword("password");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        savedUser.setPassword("encodedPassword");

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDTO resultDTO = userService.createUser(inputDTO);

        assertNotNull(resultDTO);
        assertEquals(1L, resultDTO.getId());
        assertEquals("newuser", resultDTO.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_existingUsername_throwsResponseStatusException() {
        User user = new User();
        user.setUsername("existinguser");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> userService.createUser(user, null, null));
    }

    @Test
    void getUserByUsername_existingUsername_returnsUserDTO() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(List.of(user));

        UserDTO resultDTO = userService.getUserByUsername("testuser");

        assertNotNull(resultDTO);
        assertEquals(1L, resultDTO.getId());
        assertEquals("testuser", resultDTO.getUsername());
    }

    @Test
    void getUserByUsername_nonExistingUsername_throwsResponseStatusException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(List.of());

        assertThrows(ResponseStatusException.class, () -> userService.getUserByUsername("nonexistent"));
    }

    @Test
    void getAllUsers_usersExist_returnsListOfUserDTOs() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<UserDTO> resultDTOs = userService.getAllUsers();

        assertNotNull(resultDTOs);
        assertEquals(2, resultDTOs.size());
        assertEquals("user1", resultDTOs.get(0).getUsername());
        assertEquals("user2", resultDTOs.get(1).getUsername());
    }

    @Test
    void updateUser_validIdAndUserDTO_returnsUpdatedUser() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("oldusername");

        UserDTO updateDTO = new UserDTO();
        updateDTO.setUsername("newusername");
        updateDTO.setPassword("newpassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User updatedUser = userService.updateUser(1L, updateDTO);

        assertNotNull(updatedUser);
        assertEquals("newusername", updatedUser.getUsername());
        assertEquals("encodedNewPassword", updatedUser.getPassword());
        verify(userRepository).save(existingUser);
    }

    @Test
    void deleteUser_existingId_deletesCalled() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }
}
