package com.dangerzone.backend.service;

import com.dangerzone.backend.model.User;
import com.dangerzone.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Registra um novo usuário.
     * Cria hash da senha antes de salvar.
     */
    public User register(String email, String rawPassword, String nickname,
                         String fullName, String cpf, String phone) {

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(rawPassword);
        user.setNickname(nickname);
        user.setFullName(fullName);
        user.setCpf(cpf);
        user.setPhone(phone);

        return userRepository.save(user);
    }

    /**
     * Busca usuário pelo email.
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Valida senha digitada com a senha criptografada do banco.
     */
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}