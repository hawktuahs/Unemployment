package com.example.demo.controller;

import com.example.demo.entity.Employee;
import com.example.demo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*") // Allow requests from any origin for development
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Get all employees
     * @return List of all employees
     */
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    /**
     * Get an employee by ID
     * @param id The employee ID
     * @return The employee if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            return ResponseEntity.ok(employee);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Create a new employee
     * @param employee The employee to create
     * @return The created employee
     */
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        Employee createdEmployee = employeeService.createEmployee(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    /**
     * Update an existing employee
     * @param id The employee ID
     * @param employee The updated employee details
     * @return The updated employee
     */
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        Employee updatedEmployee = employeeService.updateEmployee(id, employee);
        if (updatedEmployee != null) {
            return ResponseEntity.ok(updatedEmployee);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Delete an employee
     * @param id The employee ID
     * @return No content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update employee availability
     * @param id The employee ID
     * @param available The availability status
     * @return The updated employee
     */
    @PatchMapping("/{id}/availability")
    public ResponseEntity<Employee> updateAvailability(@PathVariable Long id, @RequestParam boolean available) {
        Employee updatedEmployee = employeeService.updateAvailability(id, available);
        if (updatedEmployee != null) {
            return ResponseEntity.ok(updatedEmployee);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get an employee's manager
     * @param id The employee ID
     * @return The manager if found
     */
    @GetMapping("/{id}/manager")
    public ResponseEntity<Employee> getManager(@PathVariable Long id) {
        Employee manager = employeeService.findManager(id);
        if (manager != null) {
            return ResponseEntity.ok(manager);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Find the next available approver for an employee
     * @param id The employee ID
     * @return The next available approver
     */
    @GetMapping("/{id}/next-approver")
    public ResponseEntity<Employee> getNextAvailableApprover(@PathVariable Long id) {
        Employee approver = employeeService.findNextAvailableApprover(id);
        if (approver != null) {
            return ResponseEntity.ok(approver);
        }
        return ResponseEntity.notFound().build();
    }
}