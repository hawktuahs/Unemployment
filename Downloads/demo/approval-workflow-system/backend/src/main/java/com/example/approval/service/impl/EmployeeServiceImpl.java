package com.example.approval.service.impl;

import com.example.approval.dto.EmployeeDTO;
import com.example.approval.entity.ApprovalRequest;
import com.example.approval.entity.Employee;
import com.example.approval.repository.ApprovalRequestRepository;
import com.example.approval.repository.EmployeeRepository;
import com.example.approval.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ApprovalRequestRepository approvalRequestRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, ApprovalRequestRepository approvalRequestRepository) {
        this.employeeRepository = employeeRepository;
        this.approvalRequestRepository = approvalRequestRepository;
    }

    @Override
    @Transactional
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        // Check if email already exists
        if (employeeRepository.existsByEmail(employeeDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Employee employee = Employee.builder()
                .name(employeeDTO.getName())
                .email(employeeDTO.getEmail())
                .position(employeeDTO.getPosition())
                .availabilityStatus(Employee.AvailabilityStatus.AVAILABLE)
                .build();

        // Assign manager if provided
        if (employeeDTO.getManagerId() != null) {
            Employee manager = employeeRepository.findById(employeeDTO.getManagerId())
                    .orElseThrow(() -> new EntityNotFoundException("Manager not found with ID: " + employeeDTO.getManagerId()));
            employee.setManager(manager);
        }

        Employee savedEmployee = employeeRepository.save(employee);
        return EmployeeDTO.fromEntity(savedEmployee);
    }

    @Override
    @Transactional
    public EmployeeDTO updateSupervisor(Long employeeId, Long newSupervisorId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));

        // Cannot set oneself as manager
        if (employeeId.equals(newSupervisorId)) {
            throw new IllegalArgumentException("Employee cannot be their own manager");
        }

        // Find the new supervisor
        Employee newSupervisor = employeeRepository.findById(newSupervisorId)
                .orElseThrow(() -> new EntityNotFoundException("Manager not found with ID: " + newSupervisorId));

        // Check if the new supervisor is not already reporting to this employee (to avoid loops)
        Employee current = newSupervisor;
        while (current != null && current.getManager() != null) {
            if (current.getManager().getId().equals(employeeId)) {
                throw new IllegalArgumentException("Cannot create a reporting loop in the hierarchy");
            }
            current = current.getManager();
        }

        employee.setManager(newSupervisor);
        Employee updatedEmployee = employeeRepository.save(employee);
        
        return EmployeeDTO.fromEntity(updatedEmployee);
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(EmployeeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmployeeDTO toggleAvailability(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));

        // Toggle availability
        if (employee.getAvailabilityStatus() == Employee.AvailabilityStatus.AVAILABLE) {
            employee.setAvailabilityStatus(Employee.AvailabilityStatus.UNAVAILABLE);
            
            // When an employee becomes unavailable, escalate all their pending approval requests
            List<ApprovalRequest> pendingRequests = approvalRequestRepository.findByApproverAndStatus(
                    employee, ApprovalRequest.RequestStatus.PENDING);
            
            for (ApprovalRequest request : pendingRequests) {
                // If the employee has a manager, assign the request to the manager
                if (employee.getManager() != null) {
                    request.setApprover(employee.getManager());
                    request.setStatus(ApprovalRequest.RequestStatus.ESCALATED);
                } else {
                    // If no manager exists, just mark as escalated without an approver
                    request.setStatus(ApprovalRequest.RequestStatus.ESCALATED);
                    request.setApprover(null);
                }
                approvalRequestRepository.save(request);
            }
        } else {
            employee.setAvailabilityStatus(Employee.AvailabilityStatus.AVAILABLE);
        }

        Employee updatedEmployee = employeeRepository.save(employee);
        return EmployeeDTO.fromEntity(updatedEmployee);
    }

    @Override
    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + id));
        return EmployeeDTO.fromEntity(employee);
    }
} 