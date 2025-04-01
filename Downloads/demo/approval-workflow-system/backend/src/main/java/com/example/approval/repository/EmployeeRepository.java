package com.example.approval.repository;

import com.example.approval.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    List<Employee> findByManager(Employee manager);
    
    List<Employee> findByAvailabilityStatus(Employee.AvailabilityStatus status);
    
    boolean existsByEmail(String email);
} 