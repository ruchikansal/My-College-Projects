package com.jira.jiraticket.repository;

import com.jira.jiraticket.model.JiraTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JiraTicketRepository extends JpaRepository<JiraTicket, Long> {

//    @Modifying
//    @Query("UPDATE JIRA_TICKET jt SET jt.status = ?0 WHERE jt.ticket_id = ?1")
//    void updateStatusById(String status, Long ticketId);
}
