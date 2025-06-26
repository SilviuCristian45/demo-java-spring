package com.example.demo;

import com.example.demo.dao.UserDAO;
import com.example.demo.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {

		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepository) {
		return args -> {
			userRepository.save(new UserDAO(null, "Silviu", "silviu@example.com"));
			userRepository.save(new UserDAO(null, "Maria", "maria@example.com"));
		};
	}


}
