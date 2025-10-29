package com.dangerzone.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeDataRequest {
    private String newEmail;
    private String newNickname;
    private String password;
}