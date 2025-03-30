package com.example.demo.repository;

import com.example.demo.entity.ApprovalRequest;
import com.example.demo.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {
    
    /**
     * Find all approval requests for a specific requester
     * @param requester The employee who made the request
     * @return List of approval requests
     */
    List<ApprovalRequest> findByRequester(Employee requester);
    
    /**
     * Find all approval requests assigned to a specific approver
     * @param currentApprover The employee who needs to approve
     * @return List of approval requests
     */
    List<ApprovalRequest> findByCurrentApprover(Employee currentApprover);
    
    /**
     * Find all approval requests with a specific status
     * @param status The status to filter by
     * @return List of approval requests
     */
    List<ApprovalRequest> findByStatus(ApprovalRequest.ApprovalStatus status);
    
    /**
     * Find all approval requests for a specific approver with a specific status
     * @param currentApprover The employee who needs to approve
     * @param status The status to filter by
     * @return List of approval requests
     */
    List<ApprovalRequest> findByCurrentApproverAndStatus(Employee currentApprover, ApprovalRequest.ApprovalStatus status);
}