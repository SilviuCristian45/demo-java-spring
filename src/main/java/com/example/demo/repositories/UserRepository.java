package com.example.demo.repositories;


import com.example.demo.dao.UserDAO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDAO, Long> {
    // poți adăuga metode custom aici dacă vrei
}
