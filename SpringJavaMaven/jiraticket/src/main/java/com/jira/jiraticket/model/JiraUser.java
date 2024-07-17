package com.jira.jiraticket.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "JIRA_USER", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"}),
        @UniqueConstraint(columnNames = {"email"})
})
public class JiraUser {

    @Id
    private Long user_id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String role;
}
