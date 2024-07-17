package com.jira.jiraticket.repository;

import com.jira.jiraticket.model.JiraUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JiraUserRepository extends JpaRepository<JiraUser, Long> {
    Optional<JiraUser> findByEmail(String email);
    Optional<JiraUser> findByUsernameOrEmail(String username, String email);
    Optional<JiraUser> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
