import api from './api';
import { ApprovalRequest } from '../types';

export const getAllRequests = async (): Promise<ApprovalRequest[]> => {
  const response = await api.get('/approvals');
  return response.data;
};

export const getRequestById = async (id: number): Promise<ApprovalRequest> => {
  const response = await api.get(`/approvals/${id}`);
  return response.data;
};

export const getRequestsByEmployee = async (employeeId: number): Promise<ApprovalRequest[]> => {
  const response = await api.get(`/approvals/employee/${employeeId}`);
  return response.data;
};

export const getPendingRequestsByApprover = async (approverId: number): Promise<ApprovalRequest[]> => {
  const response = await api.get(`/approvals/pending/${approverId}`);
  return response.data;
};

export const createRequest = async (
  request: Pick<ApprovalRequest, 'requesterId' | 'description'>
): Promise<ApprovalRequest> => {
  const response = await api.post('/approvals', request);
  return response.data;
};

export const approveRequest = async (requestId: number, approverId: number): Promise<ApprovalRequest> => {
  const response = await api.put(`/approvals/${requestId}/approve`, { approverId });
  return response.data;
};

export const rejectRequest = async (requestId: number, approverId: number): Promise<ApprovalRequest> => {
  const response = await api.put(`/approvals/${requestId}/reject`, { approverId });
  return response.data;
};

export const escalateRequest = async (requestId: number): Promise<ApprovalRequest> => {
  const response = await api.put(`/approvals/${requestId}/escalate`);
  return response.data;
};