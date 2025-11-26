package com.dangerzone.backend.config;

import com.dangerzone.backend.model.User;
import com.dangerzone.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class LoadDatabase {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Bean
    CommandLineRunner initDatabase(UserRepository repository) {
        return args -> {
            if (!repository.existsByEmail("example@example.com")) {
                User user = new User();
                user.setEmail("example@example.com");
                user.setPassword(passwordEncoder.encode("123456")); // será armazenado como hash
                user.setFullName("John Doe");
                repository.save(user);
            }
        };
    }
}