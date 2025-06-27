package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.services.UserService;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AppController {

    private final UserService userService;

    // Injectăm service-ul prin constructor
    public AppController(UserService userService) {
        this.userService = userService;
    }


    // Endpoint GET la /api/home
    @GetMapping("/home")
    public String home() {
        return "Welcome to the home page 999 !";
    }

    // Endpoint GET la /api/profile
    @GetMapping("/profile")
    public String profile() {
        return "This is your profile page.";
    }

    @GetMapping("/users")
    public List<User> users() throws BadRequestException {
        return this.userService.getAllUsers();
    }
    @GetMapping(path = "/")
    public HashMap index() {
        // get a successful user login
        OAuth2User user = ((OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return new HashMap(){{
            put("hello", user.getAttribute("name"));
            put("your email is", user.getAttribute("email"));
        }};
    }


    @GetMapping(path = "/unauthenticated")
    public HashMap unauthenticatedRequests() {
        return new HashMap(){{
            put("this is ", "unauthenticated endpoint");
        }};
    }
    // Endpoint POST la /api/login (de exemplu pentru login)
    @PostMapping("/login")
    public LoginRequest login(@RequestBody LoginRequest loginRequest) {
        // aici ai putea valida userul, parola, etc.
        //return "Logged in user: " + loginRequest.username;
        return this.userService.insertUser(loginRequest);
    }

    // Clasa pentru a primi date de login în body-ul POST-ului
    public static class LoginRequest {
        public String username;
        public String password;
    }
}
