package com.jira.jiraticket.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long user_id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String role;
}
