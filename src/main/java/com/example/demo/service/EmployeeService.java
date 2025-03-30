package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Get all employees
     * @return List of all employees
     */
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * Get an employee by ID
     * @param id The employee ID
     * @return The employee if found, null otherwise
     */
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    /**
     * Create a new employee
     * @param employee The employee to create
     * @return The created employee
     */
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    /**
     * Update an existing employee
     * @param id The employee ID
     * @param employeeDetails The updated employee details
     * @return The updated employee
     */
    public Employee updateEmployee(Long id, Employee employeeDetails) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee != null) {
            employee.setName(employeeDetails.getName());
            employee.setEmail(employeeDetails.getEmail());
            employee.setPosition(employeeDetails.getPosition());
            employee.setManager(employeeDetails.getManager());
            employee.setAvailable(employeeDetails.isAvailable());
            return employeeRepository.save(employee);
        }
        return null;
    }

    /**
     * Delete an employee
     * @param id The employee ID
     */
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    /**
     * Find an employee's manager
     * @param employeeId The employee ID
     * @return The manager if found, null otherwise
     */
    public Employee findManager(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        return employee != null ? employee.getManager() : null;
    }

    /**
     * Find the next available approver in the hierarchy
     * If the immediate manager is not available, find their manager
     * @param employeeId The employee ID
     * @return The next available approver
     */
    public Employee findNextAvailableApprover(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee == null) {
            return null;
        }

        Employee manager = employee.getManager();
        // If no manager exists, return null
        if (manager == null) {
            return null;
        }

        // If manager is available, return the manager
        if (manager.isAvailable()) {
            return manager;
        }

        // If manager is not available, recursively find the manager's manager
        return findNextAvailableApprover(manager.getId());
    }

    /**
     * Update employee availability
     * @param id The employee ID
     * @param available The availability status
     * @return The updated employee
     */
    public Employee updateAvailability(Long id, boolean available) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee != null) {
            employee.setAvailable(available);
            return employeeRepository.save(employee);
        }
        return null;
    }
}