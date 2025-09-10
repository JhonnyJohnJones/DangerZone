package com.dangerzone.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // gera getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String fullName;
    private String nickname;
    private String email;
    private String phone;
    private String cpf;
}