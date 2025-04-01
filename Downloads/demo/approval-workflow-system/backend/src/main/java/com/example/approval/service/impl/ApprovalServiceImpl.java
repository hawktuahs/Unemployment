package com.example.approval.service.impl;

import com.example.approval.dto.ApprovalRequestDTO;
import com.example.approval.entity.ApprovalRequest;
import com.example.approval.entity.Employee;
import com.example.approval.repository.ApprovalRequestRepository;
import com.example.approval.repository.EmployeeRepository;
import com.example.approval.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRequestRepository approvalRequestRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public ApprovalServiceImpl(ApprovalRequestRepository approvalRequestRepository, EmployeeRepository employeeRepository) {
        this.approvalRequestRepository = approvalRequestRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public ApprovalRequestDTO createRequest(ApprovalRequestDTO requestDTO) {
        // Find the requester
        Employee requester = employeeRepository.findById(requestDTO.getRequesterId())
                .orElseThrow(() -> new EntityNotFoundException("Requester not found with ID: " + requestDTO.getRequesterId()));

        // Create new request
        ApprovalRequest request = ApprovalRequest.builder()
                .requester(requester)
                .description(requestDTO.getDescription())
                .status(ApprovalRequest.RequestStatus.PENDING)
                .build();

        // Assign approver (immediate manager)
        if (requester.getManager() != null) {
            Employee approver = requester.getManager();
            
            // If the manager is unavailable, escalate to their manager if exists
            if (approver.getAvailabilityStatus() == Employee.AvailabilityStatus.UNAVAILABLE) {
                if (approver.getManager() != null) {
                    approver = approver.getManager();
                    request.setStatus(ApprovalRequest.RequestStatus.ESCALATED);
                } else {
                    // If no higher level manager exists, leave as pending but without an approver
                    request.setStatus(ApprovalRequest.RequestStatus.ESCALATED);
                    approver = null;
                }
            }
            
            request.setApprover(approver);
        }

        ApprovalRequest savedRequest = approvalRequestRepository.save(request);
        return ApprovalRequestDTO.fromEntity(savedRequest);
    }

    @Override
    @Transactional
    public ApprovalRequestDTO approveRequest(Long requestId, Long approverId) {
        ApprovalRequest request = approvalRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found with ID: " + requestId));

        // Verify the approver is the assigned approver
        if (request.getApprover() == null || !request.getApprover().getId().equals(approverId)) {
            throw new IllegalArgumentException("Only the assigned approver can approve this request");
        }

        // Only pending or escalated requests can be approved
        if (request.getStatus() != ApprovalRequest.RequestStatus.PENDING && 
            request.getStatus() != ApprovalRequest.RequestStatus.ESCALATED) {
            throw new IllegalStateException("Only pending or escalated requests can be approved");
        }

        request.setStatus(ApprovalRequest.RequestStatus.APPROVED);
        ApprovalRequest updatedRequest = approvalRequestRepository.save(request);
        return ApprovalRequestDTO.fromEntity(updatedRequest);
    }

    @Override
    @Transactional
    public ApprovalRequestDTO rejectRequest(Long requestId, Long approverId) {
        ApprovalRequest request = approvalRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found with ID: " + requestId));

        // Verify the approver is the assigned approver
        if (request.getApprover() == null || !request.getApprover().getId().equals(approverId)) {
            throw new IllegalArgumentException("Only the assigned approver can reject this request");
        }

        // Only pending or escalated requests can be rejected
        if (request.getStatus() != ApprovalRequest.RequestStatus.PENDING && 
            request.getStatus() != ApprovalRequest.RequestStatus.ESCALATED) {
            throw new IllegalStateException("Only pending or escalated requests can be rejected");
        }

        request.setStatus(ApprovalRequest.RequestStatus.REJECTED);
        ApprovalRequest updatedRequest = approvalRequestRepository.save(request);
        return ApprovalRequestDTO.fromEntity(updatedRequest);
    }

    @Override
    @Transactional
    public ApprovalRequestDTO escalateRequest(Long requestId) {
        ApprovalRequest request = approvalRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found with ID: " + requestId));

        // Only pending requests can be escalated
        if (request.getStatus() != ApprovalRequest.RequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be escalated");
        }

        // Verify that current approver has a manager
        if (request.getApprover() == null || request.getApprover().getManager() == null) {
            throw new IllegalStateException("Request cannot be escalated - no higher level manager");
        }

        request.setApprover(request.getApprover().getManager());
        request.setStatus(ApprovalRequest.RequestStatus.ESCALATED);
        
        ApprovalRequest updatedRequest = approvalRequestRepository.save(request);
        return ApprovalRequestDTO.fromEntity(updatedRequest);
    }

    @Override
    public List<ApprovalRequestDTO> getAllRequests() {
        return approvalRequestRepository.findAll().stream()
                .map(ApprovalRequestDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApprovalRequestDTO> getRequestsByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));
        
        // Get both requests by this employee and requests assigned to this employee
        List<ApprovalRequest> requesterRequests = approvalRequestRepository.findByRequester(employee);
        List<ApprovalRequest> approverRequests = approvalRequestRepository.findByApprover(employee);
        
        // Combine and convert to DTOs
        return requesterRequests.stream()
                .filter(r -> !approverRequests.contains(r)) // Avoid duplicates
                .collect(Collectors.toList())
                .stream()
                .map(ApprovalRequestDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApprovalRequestDTO> getPendingRequestsByApprover(Long approverId) {
        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new EntityNotFoundException("Approver not found with ID: " + approverId));
        
        return approvalRequestRepository.findByApproverAndStatus(approver, ApprovalRequest.RequestStatus.PENDING).stream()
                .map(ApprovalRequestDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public ApprovalRequestDTO getRequestById(Long requestId) {
        ApprovalRequest request = approvalRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found with ID: " + requestId));
        return ApprovalRequestDTO.fromEntity(request);
    }
} 