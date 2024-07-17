package com.jira.jiraticket.service;

import com.jira.jiraticket.model.JiraUser;
import com.jira.jiraticket.repository.JiraUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JiraUserService implements UserDetailsService {

    @Autowired
    private JiraUserRepository jiraUserRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JiraUser createUser(JiraUser user){
        return jiraUserRepository.save(user);
    }

    public Set<JiraUser> getUserByName(String username){
        return Collections.singleton(jiraUserRepository.findByUsername(username).get());
    }

    public List<JiraUser>
    unsafeFindUserByUsername(String username)
            throws SQLException {
        // UNSAFE !!! DON'T DO THIS !!!
        String sql = "select "
                + "* from jira_user where username = '"
                + username
                + "'";
        return jdbcTemplate.query(sql,
                BeanPropertyRowMapper.newInstance(JiraUser.class));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        JiraUser user = jiraUserRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username: "+ username));

        Set<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(user.getRole()));
        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(),
                authorities);
    }
}
