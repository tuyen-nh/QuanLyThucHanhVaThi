package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;

@SpringBootApplication
@EnableScheduling
public class QuanLyPhongThiBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuanLyPhongThiBackApplication.class, args);
	}

}
