package com.challenge.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserApplication {

	public static void main(String[] args) {
		System.out.println(System.getenv("POSTGRES_URL") + ", " + System.getenv("POSTGRES_USER") + ", " + System.getenv("POSTGRES_PASSWORD"));
		SpringApplication.run(UserApplication.class, args);
	}

}
