package com.exam.examapp;

import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Data
public class ExamAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExamAppApplication.class, args);
    }
}
