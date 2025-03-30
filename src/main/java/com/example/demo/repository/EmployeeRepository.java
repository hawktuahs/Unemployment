package com.example.demo.repository;

import com.example.demo.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    /**
     * Find an employee by their email address
     * @param email The email to search for
     * @return The employee if found
     */
    Employee findByEmail(String email);
    
    /**
     * Find all employees who report to a specific manager
     * @param managerId The ID of the manager
     * @return List of employees reporting to the manager
     */
    List<Employee> findByManagerId(Long managerId);
    
    /**
     * Find all employees with no manager (top-level employees)
     * @return List of employees with no manager
     */
    List<Employee> findByManagerIsNull();
    
    /**
     * Find all available employees
     * @return List of available employees
     */
    List<Employee> findByAvailableTrue();
}