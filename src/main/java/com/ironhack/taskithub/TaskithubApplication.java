package com.ironhack.taskithub;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ironhack.taskithub.dto.UserDTO;
import com.ironhack.taskithub.service.UserService;

@SpringBootApplication
public class TaskithubApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskithubApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    CommandLineRunner commandLineRunner(UserService userService) {

        return args -> {
            if (!userService.existsByUsername("admin")) {
                UserDTO adminUserDTO = new UserDTO();
                adminUserDTO.setName("admin");
                adminUserDTO.setUsername("admin");
                adminUserDTO.setPassword("admin");
                adminUserDTO.setRole("ADMIN");
                userService.createUser(adminUserDTO);

                System.out.println("Admin user created");
            } else {
                System.out.println("Admin user already exists");
            }

        };
    }

}
