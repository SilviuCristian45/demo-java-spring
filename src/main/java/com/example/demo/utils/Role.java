package com.example.demo.utils;


public enum Role {
    USER,
    ADMIN;

    public String withPrefix() {
        return "ROLE_" + this.name();
    }
}
