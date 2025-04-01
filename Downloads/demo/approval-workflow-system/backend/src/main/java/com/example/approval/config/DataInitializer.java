package com.example.approval.config;

import com.example.approval.entity.Employee;
import com.example.approval.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(@Autowired EmployeeRepository employeeRepository) {
        return args -> {
            // Only load sample data if the database is empty
            if (employeeRepository.count() == 0) {
                // Create Principal (top-level employee)
                Employee alice = Employee.builder()
                        .name("Alice")
                        .email("alice@example.com")
                        .position("Principal")
                        .availabilityStatus(Employee.AvailabilityStatus.AVAILABLE)
                        .build();
                
                employeeRepository.save(alice);
                
                // Create Supervisor reporting to Alice
                Employee bob = Employee.builder()
                        .name("Bob")
                        .email("bob@example.com")
                        .position("Supervisor")
                        .availabilityStatus(Employee.AvailabilityStatus.AVAILABLE)
                        .manager(alice)
                        .build();
                
                employeeRepository.save(bob);
                
                // Create Teacher reporting to Bob
                Employee charlie = Employee.builder()
                        .name("Charlie")
                        .email("charlie@example.com")
                        .position("Teacher")
                        .availabilityStatus(Employee.AvailabilityStatus.UNAVAILABLE)
                        .manager(bob)
                        .build();
                
                employeeRepository.save(charlie);
                
                System.out.println("Sample data loaded successfully!");
            }
        };
    }
} 