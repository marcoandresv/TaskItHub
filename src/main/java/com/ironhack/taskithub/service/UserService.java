package com.ironhack.taskithub.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ironhack.taskithub.dto.UserDTO;
import com.ironhack.taskithub.enums.Role;
import com.ironhack.taskithub.model.Department;
import com.ironhack.taskithub.model.Task;
import com.ironhack.taskithub.model.User;
import com.ironhack.taskithub.repository.DepartmentRepository;
import com.ironhack.taskithub.repository.TaskRepository;
import com.ironhack.taskithub.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * UserService
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<User> users = userRepository.findByUsername(username);

        if (users == null || users.isEmpty()) {
            log.error("User not found");
            throw new UsernameNotFoundException("User not found");
        } else {

            User user = users.get(0);

            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(user.getRole().name()));

            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                    authorities);
        }

    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = toUser(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return toUserDTO(userRepository.save(user));
    }

    public User createUser(User user, Long departmentId, List<Long> taskIds) throws ResponseStatusException {
        if (userRepository.existsByUsername(user.getUsername())) {
            log.error("Username already exists");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        if (departmentId != null) {
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> {
                        log.error("Department not found");
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found");
                    });
            user.setDepartment(department);
        }

        if (taskIds != null) {
            List<Task> tasks = taskIds.stream()
                    .map(taskId -> taskRepository.findById(taskId)
                            .orElseThrow(() -> {
                                log.error("Task not found");
                                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
                            }))
                    .collect(Collectors.toList());
            user.setTasks(tasks);
        }
        return userRepository.save(user);
    }

    // to be able to initialize admin user upon server startup
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public UserDTO getUserByUsername(String username) throws ResponseStatusException {
        List<User> users = userRepository.findByUsername(username);
        if (users.isEmpty()) {
            log.error("User not found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        User user = users.get(0);
        return toUserDTO(user);
    }

    public UserDTO getUserById(Long id) throws ResponseStatusException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toUserDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toUserDTO).collect(Collectors.toList());
    }

    public UserDTO updateUserFromDTO(Long id, UserDTO userDTO) {
        User updatedUser = updateUser(id, userDTO);
        return toUserDTO(updatedUser);
    }

    public User updateUser(Long id, UserDTO userDTO) throws ResponseStatusException {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (userDTO.getName() != null) {
            existingUser.setName(userDTO.getName());
        }

        if (userDTO.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            // existingUser.setPassword(userDTO.getPassword());
        }

        if (userDTO.getUsername() != null) {
            existingUser.setUsername(userDTO.getUsername());
        }

        if (userDTO.getRole() != null) {
            existingUser.setRole(Role.valueOf(userDTO.getRole()));
        }

        if (userDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(userDTO.getDepartmentId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
            existingUser.setDepartment(department);
        }

        if (userDTO.getTaskIds() != null) {
            List<Task> tasks = userDTO.getTaskIds().stream()
                    .map(taskId -> taskRepository.findById(taskId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")))
                    .collect(Collectors.toList());
            existingUser.setTasks(tasks);
        }

        existingUser.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // to automatically reassign tasks to another user if the user is deleted. setting to "null"

        List<Task> userTasks = taskRepository.findByAssignedUsers_Id(id); // Fetch the asssigned-to tasks
        for (Task task : userTasks) {
            task.setAssignedUsers(null);
        }

        List<Task> createdTasks = taskRepository.findByCreatedBy_Id(id); // Fetch the created-by tasks
        for (Task task : createdTasks) {
            task.setCreatedBy(null);
        }

        taskRepository.saveAll(userTasks);
        taskRepository.saveAll(createdTasks);

        userRepository.delete(user);
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

    public User toUser(UserDTO userDTO) throws ResponseStatusException {
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
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
            user.setDepartment(department);
        }
        return user;
    }
}
