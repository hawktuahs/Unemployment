package com.example.demo.service;

import com.example.demo.entity.ApprovalRequest;
import com.example.demo.entity.Employee;
import com.example.demo.repository.ApprovalRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApprovalService {

    private final ApprovalRequestRepository approvalRequestRepository;
    private final EmployeeService employeeService;

    @Autowired
    public ApprovalService(ApprovalRequestRepository approvalRequestRepository, EmployeeService employeeService) {
        this.approvalRequestRepository = approvalRequestRepository;
        this.employeeService = employeeService;
    }

    /**
     * Get all approval requests
     * @return List of all approval requests
     */
    public List<ApprovalRequest> getAllRequests() {
        return approvalRequestRepository.findAll();
    }

    /**
     * Get an approval request by ID
     * @param id The request ID
     * @return The approval request if found, null otherwise
     */
    public ApprovalRequest getRequestById(Long id) {
        return approvalRequestRepository.findById(id).orElse(null);
    }

    /**
     * Create a new approval request
     * The request will be automatically assigned to the requester's manager
     * If the manager is not available, it will be escalated to the next available approver
     * @param request The request to create
     * @param requesterId The ID of the employee making the request
     * @return The created request
     */
    public ApprovalRequest createRequest(ApprovalRequest request, Long requesterId) {
        Employee requester = employeeService.getEmployeeById(requesterId);
        if (requester == null) {
            return null;
        }

        // Find the next available approver in the hierarchy
        Employee approver = employeeService.findNextAvailableApprover(requesterId);
        if (approver == null) {
            // If no approver is available, the request cannot be processed
            return null;
        }

        request.setRequester(requester);
        request.setCurrentApprover(approver);
        request.setStatus(ApprovalRequest.ApprovalStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());

        return approvalRequestRepository.save(request);
    }

    /**
     * Approve a request
     * @param id The request ID
     * @param comments Comments from the approver
     * @return The updated request
     */
    public ApprovalRequest approveRequest(Long id, String comments) {
        ApprovalRequest request = approvalRequestRepository.findById(id).orElse(null);
        if (request == null) {
            return null;
        }

        request.setStatus(ApprovalRequest.ApprovalStatus.APPROVED);
        request.setComments(comments);
        request.setUpdatedAt(LocalDateTime.now());

        return approvalRequestRepository.save(request);
    }

    /**
     * Reject a request
     * @param id The request ID
     * @param comments Comments from the approver
     * @return The updated request
     */
    public ApprovalRequest rejectRequest(Long id, String comments) {
        ApprovalRequest request = approvalRequestRepository.findById(id).orElse(null);
        if (request == null) {
            return null;
        }

        request.setStatus(ApprovalRequest.ApprovalStatus.REJECTED);
        request.setComments(comments);
        request.setUpdatedAt(LocalDateTime.now());

        return approvalRequestRepository.save(request);
    }

    /**
     * Escalate a request to the next available approver
     * This happens when the current approver becomes unavailable
     * @param id The request ID
     * @return The updated request
     */
    public ApprovalRequest escalateRequest(Long id) {
        ApprovalRequest request = approvalRequestRepository.findById(id).orElse(null);
        if (request == null) {
            return null;
        }

        Employee currentApprover = request.getCurrentApprover();
        if (currentApprover == null) {
            return null;
        }

        // Find the next available approver in the hierarchy
        Employee nextApprover = employeeService.findNextAvailableApprover(currentApprover.getId());
        if (nextApprover == null) {
            // If no approver is available, the request cannot be escalated
            return null;
        }

        request.setCurrentApprover(nextApprover);
        request.setStatus(ApprovalRequest.ApprovalStatus.ESCALATED);
        request.setUpdatedAt(LocalDateTime.now());

        return approvalRequestRepository.save(request);
    }

    /**
     * Get all requests for a specific requester
     * @param requesterId The requester ID
     * @return List of requests
     */
    public List<ApprovalRequest> getRequestsByRequester(Long requesterId) {
        Employee requester = employeeService.getEmployeeById(requesterId);
        if (requester == null) {
            return List.of();
        }
        return approvalRequestRepository.findByRequester(requester);
    }

    /**
     * Get all requests for a specific approver
     * @param approverId The approver ID
     * @return List of requests
     */
    public List<ApprovalRequest> getRequestsByApprover(Long approverId) {
        Employee approver = employeeService.getEmployeeById(approverId);
        if (approver == null) {
            return List.of();
        }
        return approvalRequestRepository.findByCurrentApprover(approver);
    }

    /**
     * Get all pending requests for a specific approver
     * @param approverId The approver ID
     * @return List of pending requests
     */
    public List<ApprovalRequest> getPendingRequestsByApprover(Long approverId) {
        Employee approver = employeeService.getEmployeeById(approverId);
        if (approver == null) {
            return List.of();
        }
        return approvalRequestRepository.findByCurrentApproverAndStatus(approver, ApprovalRequest.ApprovalStatus.PENDING);
    }
}