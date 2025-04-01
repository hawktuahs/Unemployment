package com.example.approval.service;

import com.example.approval.dto.ApprovalRequestDTO;

import java.util.List;

public interface ApprovalService {
    
    ApprovalRequestDTO createRequest(ApprovalRequestDTO requestDTO);
    
    ApprovalRequestDTO approveRequest(Long requestId, Long approverId);
    
    ApprovalRequestDTO rejectRequest(Long requestId, Long approverId);
    
    ApprovalRequestDTO escalateRequest(Long requestId);
    
    List<ApprovalRequestDTO> getAllRequests();
    
    List<ApprovalRequestDTO> getRequestsByEmployee(Long employeeId);
    
    List<ApprovalRequestDTO> getPendingRequestsByApprover(Long approverId);
    
    ApprovalRequestDTO getRequestById(Long requestId);
} 