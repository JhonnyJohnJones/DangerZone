package com.dangerzone.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    // Método utilitário para setar senha com hash
    public void setPasswordHash(String rawPassword) {
        this.password = new BCryptPasswordEncoder().encode(rawPassword);
    }
}
