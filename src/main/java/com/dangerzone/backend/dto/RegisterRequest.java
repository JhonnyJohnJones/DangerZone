package com.dangerzone.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String fullName;
    private String nickname;
    private String email;
    private String phone;
}