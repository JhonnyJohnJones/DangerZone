package com.dangerzone.backend.repository;

import com.dangerzone.backend.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    boolean existsByPhone(String phone);

    Optional<User> findByEmail(String email);
}