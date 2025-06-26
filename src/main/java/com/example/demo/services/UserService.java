package com.example.demo.services;

import com.example.demo.controller.AppController;
import com.example.demo.dao.UserDAO;
import com.example.demo.model.User;
import com.example.demo.repositories.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<User> getAllUsers() throws BadRequestException {
        List<UserDAO> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new BadRequestException("users not found");
        }
        return users.stream()
                .map(User::new) // echivalent cu dao -> new User(dao)
                .collect(Collectors.toList());
    }

    public AppController.LoginRequest insertUser(AppController.LoginRequest loginRequest) {

        return loginRequest;
    }
}
