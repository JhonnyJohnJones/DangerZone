package com.dangerzone.backend.config;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(UserRepository repository) {
        return args -> {
            if (!repository.existsByEmail("exemple@example.com")) {
                User user = new User();
                user.setEmail("exemple@example.com");
                user.setPasswordHash("123456"); // será armazenado como hash
                user.setNickname("John");
                user.setFullName("John Doe");
                user.setCpf("12345678901");
                user.setPhone("11999999999");
                repository.save(user);
            }
        };
    }
}