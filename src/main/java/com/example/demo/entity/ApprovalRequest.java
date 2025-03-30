package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "approval_requests")
public class ApprovalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private Employee requester;

    @ManyToOne
    @JoinColumn(name = "current_approver_id")
    private Employee currentApprover;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String comments;

    public enum ApprovalStatus {
        PENDING,
        APPROVED,
        REJECTED,
        ESCALATED
    }

    public ApprovalRequest() {
        this.createdAt = LocalDateTime.now();
        this.status = ApprovalStatus.PENDING;
    }

    public ApprovalRequest(String title, String description, Employee requester, Employee currentApprover) {
        this();
        this.title = title;
        this.description = description;
        this.requester = requester;
        this.currentApprover = currentApprover;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Employee getRequester() {
        return requester;
    }

    public void setRequester(Employee requester) {
        this.requester = requester;
    }

    public Employee getCurrentApprover() {
        return currentApprover;
    }

    public void setCurrentApprover(Employee currentApprover) {
        this.currentApprover = currentApprover;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}