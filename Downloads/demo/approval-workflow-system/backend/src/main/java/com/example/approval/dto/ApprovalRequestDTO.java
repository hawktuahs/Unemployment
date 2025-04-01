package com.example.approval.dto;

import com.example.approval.entity.ApprovalRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalRequestDTO {

    private Long id;

    @NotNull(message = "Requester ID is required")
    private Long requesterId;

    private String requesterName;

    private Long approverId;

    private String approverName;

    private String status;

    @NotBlank(message = "Description is required")
    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static ApprovalRequestDTO fromEntity(ApprovalRequest request) {
        ApprovalRequestDTO dto = new ApprovalRequestDTO();
        dto.setId(request.getId());
        dto.setRequesterId(request.getRequester().getId());
        dto.setRequesterName(request.getRequester().getName());
        if (request.getApprover() != null) {
            dto.setApproverId(request.getApprover().getId());
            dto.setApproverName(request.getApprover().getName());
        }
        dto.setStatus(request.getStatus().name());
        dto.setDescription(request.getDescription());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());
        return dto;
    }
} 