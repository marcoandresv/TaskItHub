package com.ironhack.taskithub.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ironhack.taskithub.dto.UserDTO;
import com.ironhack.taskithub.enums.Role;
import com.ironhack.taskithub.model.Department;
import com.ironhack.taskithub.model.Task;
import com.ironhack.taskithub.model.User;
import com.ironhack.taskithub.repository.DepartmentRepository;
import com.ironhack.taskithub.repository.TaskRepository;
import com.ironhack.taskithub.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

/**
 * UserService
 */

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TaskRepository taskRepository;

    public UserDTO createUser(UserDTO userDTO) {
        User user = toUser(userDTO);
        return toUserDTO(userRepository.save(user));
    }

    public User createUser(User user, Long departmentId, List<Long> taskIds) {
        if (departmentId != null) {
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new EntityNotFoundException("Department not found"));
            user.setDepartment(department);
        }

        if (taskIds != null) {
            List<Task> tasks = taskIds.stream()
                    .map(taskId -> taskRepository.findById(taskId)
                            .orElseThrow(() -> new EntityNotFoundException("Task not found")))
                    .collect(Collectors.toList());
            user.setTasks(tasks);
        }
        return userRepository.save(user);
    }

    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username).map(this::toUserDTO);
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return toUserDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toUserDTO).collect(Collectors.toList());
    }

    public UserDTO updateUserFromDTO(Long id, UserDTO userDTO) {
        User updatedUser = updateUser(id, userDTO);
        return toUserDTO(updatedUser);
    }

    public User updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (userDTO.getName() != null) {
            existingUser.setName(userDTO.getName());
        }

        if (userDTO.getPassword() != null) {
            existingUser.setPassword(userDTO.getPassword());
        }

        if (userDTO.getUsername() != null) {
            existingUser.setUsername(userDTO.getUsername());
        }

        if (userDTO.getRole() != null) {
            existingUser.setRole(Role.valueOf(userDTO.getRole()));
        }

        if (userDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(userDTO.getDepartmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Department not found"));
            existingUser.setDepartment(department);
        }

        if (userDTO.getTaskIds() != null) {
            List<Task> tasks = userDTO.getTaskIds().stream()
                    .map(taskId -> taskRepository.findById(taskId)
                            .orElseThrow(() -> new EntityNotFoundException("Task not found")))
                    .collect(Collectors.toList());
            existingUser.setTasks(tasks);
        }

        existingUser.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setDepartmentId(user.getDepartment() != null ? user.getDepartment().getId() : null);
        dto.setTaskIds(user.getTasks().stream().map(Task::getId).collect(Collectors.toList()));
        return dto;
    }

    public User toUser(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole() != null ? Role.valueOf(userDTO.getRole()) : null);
        user.setCreatedAt(userDTO.getCreatedAt());
        user.setUpdatedAt(userDTO.getUpdatedAt());

        if (userDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(userDTO.getDepartmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Department not found"));
            user.setDepartment(department);
        }
        return user;
    }
}
