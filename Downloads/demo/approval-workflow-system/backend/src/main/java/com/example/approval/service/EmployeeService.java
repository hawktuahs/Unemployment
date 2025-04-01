package com.example.approval.service;

import com.example.approval.dto.EmployeeDTO;

import java.util.List;

public interface EmployeeService {
    
    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);
    
    EmployeeDTO updateSupervisor(Long employeeId, Long newSupervisorId);
    
    List<EmployeeDTO> getAllEmployees();
    
    EmployeeDTO toggleAvailability(Long employeeId);
    
    EmployeeDTO getEmployeeById(Long id);
} 