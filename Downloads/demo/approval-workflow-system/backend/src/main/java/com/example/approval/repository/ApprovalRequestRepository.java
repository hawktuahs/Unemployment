package com.example.approval.repository;

import com.example.approval.entity.ApprovalRequest;
import com.example.approval.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {
    
    List<ApprovalRequest> findByApproverAndStatus(Employee approver, ApprovalRequest.RequestStatus status);
    
    List<ApprovalRequest> findByRequester(Employee requester);
    
    List<ApprovalRequest> findByApprover(Employee approver);
} 