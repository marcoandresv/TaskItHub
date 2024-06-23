package com.ironhack.taskithub.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ironhack.taskithub.model.User;
import com.ironhack.taskithub.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

/**
 * UserService
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User updatedUser) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // to get and preserve the "createdAt" and "id" values
        updatedUser.setCreatedAt(existingUser.getCreatedAt());
        updatedUser.setId(id);

        // to modify the "updatedAt" value
        updatedUser.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(updatedUser);

    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}
