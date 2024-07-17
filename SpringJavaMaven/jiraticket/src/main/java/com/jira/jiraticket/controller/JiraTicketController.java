package com.jira.jiraticket.controller;

import com.jira.jiraticket.model.JiraTicket;
import com.jira.jiraticket.service.JiraTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class JiraTicketController {

    @Autowired
    private JiraTicketService jiraTicketService;

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public JiraTicket createTicket(@RequestBody JiraTicket ticket) {
        return jiraTicketService.createTicket(ticket);
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'DEVELOPER')")
    public JiraTicket getTicketById(@PathVariable Long id) {
        return jiraTicketService.getTicketById(id);
    }

    @PutMapping(value = "/updatestatus/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DEVELOPER')")
    public JiraTicket updateStatus(@PathVariable Long id, @RequestParam String status) {
        return jiraTicketService.updateStatus(status, id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public void deleteTask(@PathVariable Long id) {
        jiraTicketService.deleteTicket(id);
    }
}
