package com.dangerzone.backend.repository;

import com.dangerzone.backend.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
}