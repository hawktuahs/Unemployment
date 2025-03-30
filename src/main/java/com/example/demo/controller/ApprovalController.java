package com.example.demo.controller;

import com.example.demo.entity.ApprovalRequest;
import com.example.demo.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/approvals")
@CrossOrigin(origins = "*") // Allow requests from any origin for development
public class ApprovalController {

    private final ApprovalService approvalService;

    @Autowired
    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    /**
     * Get all approval requests
     * @return List of all approval requests
     */
    @GetMapping
    public ResponseEntity<List<ApprovalRequest>> getAllRequests() {
        return ResponseEntity.ok(approvalService.getAllRequests());
    }

    /**
     * Get an approval request by ID
     * @param id The request ID
     * @return The approval request if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApprovalRequest> getRequestById(@PathVariable Long id) {
        ApprovalRequest request = approvalService.getRequestById(id);
        if (request != null) {
            return ResponseEntity.ok(request);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Create a new approval request
     * @param request The request to create
     * @param requesterId The ID of the employee making the request
     * @return The created request
     */
    @PostMapping
    public ResponseEntity<ApprovalRequest> createRequest(
            @RequestBody ApprovalRequest request,
            @RequestParam Long requesterId) {
        ApprovalRequest createdRequest = approvalService.createRequest(request, requesterId);
        if (createdRequest != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Approve a request
     * @param id The request ID
     * @param requestBody The request body containing comments
     * @return The updated request
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<ApprovalRequest> approveRequest(
            @PathVariable Long id,
            @RequestBody Map<String, String> requestBody) {
        String comments = requestBody.getOrDefault("comments", "");
        ApprovalRequest updatedRequest = approvalService.approveRequest(id, comments);
        if (updatedRequest != null) {
            return ResponseEntity.ok(updatedRequest);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Reject a request
     * @param id The request ID
     * @param requestBody The request body containing comments
     * @return The updated request
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<ApprovalRequest> rejectRequest(
            @PathVariable Long id,
            @RequestBody Map<String, String> requestBody) {
        String comments = requestBody.getOrDefault("comments", "");
        ApprovalRequest updatedRequest = approvalService.rejectRequest(id, comments);
        if (updatedRequest != null) {
            return ResponseEntity.ok(updatedRequest);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Escalate a request to the next available approver
     * @param id The request ID
     * @return The updated request
     */
    @PostMapping("/{id}/escalate")
    public ResponseEntity<ApprovalRequest> escalateRequest(@PathVariable Long id) {
        ApprovalRequest updatedRequest = approvalService.escalateRequest(id);
        if (updatedRequest != null) {
            return ResponseEntity.ok(updatedRequest);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get all requests for a specific requester
     * @param requesterId The requester ID
     * @return List of requests
     */
    @GetMapping("/by-requester/{requesterId}")
    public ResponseEntity<List<ApprovalRequest>> getRequestsByRequester(@PathVariable Long requesterId) {
        return ResponseEntity.ok(approvalService.getRequestsByRequester(requesterId));
    }

    /**
     * Get all requests for a specific approver
     * @param approverId The approver ID
     * @return List of requests
     */
    @GetMapping("/by-approver/{approverId}")
    public ResponseEntity<List<ApprovalRequest>> getRequestsByApprover(@PathVariable Long approverId) {
        return ResponseEntity.ok(approvalService.getRequestsByApprover(approverId));
    }

    /**
     * Get all pending requests for a specific approver
     * @param approverId The approver ID
     * @return List of pending requests
     */
    @GetMapping("/pending/by-approver/{approverId}")
    public ResponseEntity<List<ApprovalRequest>> getPendingRequestsByApprover(@PathVariable Long approverId) {
        return ResponseEntity.ok(approvalService.getPendingRequestsByApprover(approverId));
    }
}