package com.example.approval.controller;

import com.example.approval.dto.EmployeeDTO;
import com.example.approval.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        return new ResponseEntity<>(employeeService.createEmployee(employeeDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/supervisor")
    public ResponseEntity<EmployeeDTO> updateSupervisor(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request) {
        
        Long newSupervisorId = request.get("newSupervisorId");
        if (newSupervisorId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        return ResponseEntity.ok(employeeService.updateSupervisor(id, newSupervisorId));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @PutMapping("/{id}/toggle-availability")
    public ResponseEntity<EmployeeDTO> toggleAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.toggleAvailability(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleExceptions(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
} 