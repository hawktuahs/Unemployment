export interface Employee {
  id: number;
  name: string;
  email: string;
  managerId: number | null;
  managerName: string | null;
  position: string;
  availabilityStatus: string;
}

export interface ApprovalRequest {
  id: number;
  requesterId: number;
  requesterName: string;
  approverId: number | null;
  approverName: string | null;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'ESCALATED';
  description: string;
  createdAt: string;
  updatedAt: string;
} 