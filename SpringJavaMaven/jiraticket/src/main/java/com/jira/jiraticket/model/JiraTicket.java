package com.jira.jiraticket.model;

import javax.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "JIRA_TICKET")
public class JiraTicket {

    @Id
    private Long ticket_id;
    private String summary;
    private String description;
    private String status;
    private String priority;
    private int assignee_id;
    private int reporter_id;
    private Date created_at;

    private Date updated_at;

}
