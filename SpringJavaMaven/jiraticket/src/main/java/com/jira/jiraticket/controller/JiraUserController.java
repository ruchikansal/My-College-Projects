package com.jira.jiraticket.controller;

import com.jira.jiraticket.dto.LoginDto;
import com.jira.jiraticket.dto.UserDto;
import com.jira.jiraticket.model.JiraUser;
import com.jira.jiraticket.repository.JiraUserRepository;
import com.jira.jiraticket.service.JiraUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class JiraUserController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JiraUserService jiraUserService;

    @Autowired
    private JiraUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(value = "/register")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody UserDto user) {

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        System.out.println("************ " + authentication);

        // add check for username exists in a DB
        if(userRepository.existsByUsername(user.getUsername())){
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        // add check for email exists in DB
        if(userRepository.existsByEmail(user.getEmail())){
            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
        }

        // create user object
        JiraUser jiraUser = new JiraUser();
        jiraUser.setUser_id(user.getUser_id());
        jiraUser.setUsername(user.getUsername());
        jiraUser.setFullName(user.getFullName());
        jiraUser.setEmail(user.getEmail());
        if(checkStrongNess(user.getPassword())){
            jiraUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }else{
            return new ResponseEntity<>("Please select strong password!", HttpStatus.BAD_REQUEST);
        }
        //jiraUser.setPassword(passwordEncoder.encode(user.getPassword()));
        jiraUser.setRole(user.getRole());

        userRepository.save(jiraUser);

        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new ResponseEntity<>("User logged-in successfully!.", HttpStatus.OK);
    }

    @GetMapping(value = "/getUser/{username}")
    public Set<JiraUser> getUsers(@PathVariable String username){
        return jiraUserService.getUserByName(username);
    }

    @GetMapping(value = "/getUserUnsafe/{username}")
    public List<JiraUser> getUsersUnsafe(@PathVariable String username){
        try {
            return jiraUserService.unsafeFindUserByUsername(username);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkStrongNess(String input)
    {
        // Checking lower alphabet in string
        int n = input.length();
        boolean hasLower = false, hasUpper = false,
                hasDigit = false, specialChar = false;
        Set<Character> set = new HashSet<Character>(
                Arrays.asList('!', '@', '#', '$', '%', '^', '&',
                        '*', '(', ')', '-', '+'));
        for (char i : input.toCharArray())
        {
            if (Character.isLowerCase(i))
                hasLower = true;
            if (Character.isUpperCase(i))
                hasUpper = true;
            if (Character.isDigit(i))
                hasDigit = true;
            if (set.contains(i))
                specialChar = true;
        }

        // Strength of password
        if (hasDigit && hasLower && hasUpper && specialChar
                && (n >= 8))
            return true;
        else if ((hasLower || hasUpper || specialChar)
                && (n >= 6))
            return false;
        else
            return false;
    }

}
