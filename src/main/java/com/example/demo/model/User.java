package com.example.demo.model;

import com.example.demo.dao.UserDAO;

public class User {
    private Long id;
    private String name;
    private String email;

    public User() {}

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public User(UserDAO user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
    }

    // Getters și setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

