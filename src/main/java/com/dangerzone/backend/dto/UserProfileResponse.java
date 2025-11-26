package com.dangerzone.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String fullName;
    private String email;
    private List<UserReportResponse> reports;
}
