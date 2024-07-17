package com.jira.jiraticket.dto;

import lombok.Data;

@Data
public class JiraTicketDto {
    private Long ticket_id;
    private String summary;
    private String description;
    private String status;
    private String priority;
    private int assignee_id;
    private int reporter_id;

}
