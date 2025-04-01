package com.example.approval.controller;

import com.example.approval.dto.ApprovalRequestDTO;
import com.example.approval.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/approvals")
@CrossOrigin(origins = "*")
public class ApprovalController {

    private final ApprovalService approvalService;

    @Autowired
    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @PostMapping
    public ResponseEntity<ApprovalRequestDTO> createRequest(@Valid @RequestBody ApprovalRequestDTO requestDTO) {
        return new ResponseEntity<>(approvalService.createRequest(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ApprovalRequestDTO>> getAllRequests() {
        return ResponseEntity.ok(approvalService.getAllRequests());
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<List<ApprovalRequestDTO>> getRequestsByEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(approvalService.getRequestsByEmployee(id));
    }
    
    @GetMapping("/pending/{approverId}")
    public ResponseEntity<List<ApprovalRequestDTO>> getPendingRequestsByApprover(@PathVariable Long approverId) {
        return ResponseEntity.ok(approvalService.getPendingRequestsByApprover(approverId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApprovalRequestDTO> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(approvalService.getRequestById(id));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApprovalRequestDTO> approveRequest(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request) {
        
        Long approverId = request.get("approverId");
        if (approverId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        return ResponseEntity.ok(approvalService.approveRequest(id, approverId));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApprovalRequestDTO> rejectRequest(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request) {
        
        Long approverId = request.get("approverId");
        if (approverId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        return ResponseEntity.ok(approvalService.rejectRequest(id, approverId));
    }

    @PutMapping("/{id}/escalate")
    public ResponseEntity<ApprovalRequestDTO> escalateRequest(@PathVariable Long id) {
        return ResponseEntity.ok(approvalService.escalateRequest(id));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleExceptions(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
} 