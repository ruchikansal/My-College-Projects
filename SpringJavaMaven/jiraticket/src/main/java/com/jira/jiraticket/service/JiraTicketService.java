package com.jira.jiraticket.service;

import com.jira.jiraticket.model.JiraTicket;
import com.jira.jiraticket.repository.JiraTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JiraTicketService {

    @Autowired
    private JiraTicketRepository jiraTicketRepository;

    public JiraTicket createTicket(JiraTicket ticket){
        return jiraTicketRepository.save(ticket);
    }

    public JiraTicket getTicketById(Long ticketId){
        return jiraTicketRepository.findById(ticketId).get();
    }

    public JiraTicket updateStatus(String status, Long ticketId){
        JiraTicket jiraTicket = jiraTicketRepository.getReferenceById(ticketId);
        jiraTicket.setStatus(status);
        return jiraTicketRepository.save(jiraTicket);
    }

    public void deleteTicket(Long ticketId){
        jiraTicketRepository.deleteById(ticketId);
    }
}
