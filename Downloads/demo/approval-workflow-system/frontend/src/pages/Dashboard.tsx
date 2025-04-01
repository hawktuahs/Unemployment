import React, { useState, useEffect } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import {
  Typography,
  Paper,
  Box,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Button,
  Stack,
  Chip,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  SelectChangeEvent,
  CircularProgress,
  Alert,
} from '@mui/material';
import { ApprovalRequest, Employee } from '../types';
import { getPendingRequestsByApprover, approveRequest, rejectRequest, escalateRequest } from '../services/approvalService';
import { getAllEmployees } from '../services/employeeService';

const Dashboard = () => {
  const [pendingRequests, setPendingRequests] = useState<ApprovalRequest[]>([]);
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [selectedApproverId, setSelectedApproverId] = useState<number | ''>('');
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>('');
  const [actionSuccess, setActionSuccess] = useState<string>('');

  useEffect(() => {
    const fetchEmployees = async () => {
      try {
        const data = await getAllEmployees();
        setEmployees(data);
        if (data.length > 0) {
          setSelectedApproverId(data[0].id);
        }
      } catch (err) {
        setError('Failed to load employees');
        console.error(err);
      }
    };

    fetchEmployees();
  }, []);

  useEffect(() => {
    if (selectedApproverId !== '') {
      fetchPendingRequests();
    }
  }, [selectedApproverId]);

  const fetchPendingRequests = async () => {
    if (!selectedApproverId) return;
    
    setLoading(true);
    setError('');
    try {
      const requests = await getPendingRequestsByApprover(selectedApproverId as number);
      setPendingRequests(requests);
    } catch (err) {
      setError('Failed to load pending requests');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleApproverChange = (event: SelectChangeEvent<number | ''>) => {
    setSelectedApproverId(event.target.value as number);
  };

  const handleApprove = async (requestId: number) => {
    if (!selectedApproverId) return;
    
    setLoading(true);
    setError('');
    setActionSuccess('');
    try {
      await approveRequest(requestId, selectedApproverId as number);
      setActionSuccess('Request approved successfully');
      // Refresh the list after approval
      await fetchPendingRequests();
    } catch (err) {
      setError('Failed to approve request');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleReject = async (requestId: number) => {
    if (!selectedApproverId) return;
    
    setLoading(true);
    setError('');
    setActionSuccess('');
    try {
      await rejectRequest(requestId, selectedApproverId as number);
      setActionSuccess('Request rejected successfully');
      // Refresh the list after rejection
      await fetchPendingRequests();
    } catch (err) {
      setError('Failed to reject request');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleEscalate = async (requestId: number) => {
    setLoading(true);
    setError('');
    setActionSuccess('');
    try {
      await escalateRequest(requestId);
      setActionSuccess('Request escalated successfully');
      // Refresh the list after escalation
      await fetchPendingRequests();
    } catch (err) {
      setError('Failed to escalate request');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const getStatusChipColor = (status: string) => {
    switch (status) {
      case 'PENDING':
        return 'warning';
      case 'APPROVED':
        return 'success';
      case 'REJECTED':
        return 'error';
      case 'ESCALATED':
        return 'info';
      default:
        return 'default';
    }
  };

  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        Dashboard
      </Typography>

      <Paper sx={{ p: 2, mb: 4 }}>
        <Typography variant="h6" gutterBottom>
          Pending Approval Requests
        </Typography>

        <FormControl fullWidth sx={{ mb: 3 }}>
          <InputLabel id="approver-select-label">View as Approver</InputLabel>
          <Select
            labelId="approver-select-label"
            id="approver-select"
            value={selectedApproverId}
            label="View as Approver"
            onChange={handleApproverChange}
          >
            {employees.map((employee) => (
              <MenuItem key={employee.id} value={employee.id}>
                {employee.name} ({employee.position})
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        {actionSuccess && <Alert severity="success" sx={{ mb: 2 }}>{actionSuccess}</Alert>}

        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
            <CircularProgress />
          </Box>
        ) : pendingRequests.length === 0 ? (
          <Alert severity="info">No pending requests found for this approver.</Alert>
        ) : (
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Requester</TableCell>
                  <TableCell>Description</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Created At</TableCell>
                  <TableCell>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {pendingRequests.map((request) => (
                  <TableRow key={request.id}>
                    <TableCell>{request.id}</TableCell>
                    <TableCell>{request.requesterName}</TableCell>
                    <TableCell>{request.description}</TableCell>
                    <TableCell>
                      <Chip 
                        label={request.status} 
                        color={getStatusChipColor(request.status) as any} 
                        size="small" 
                      />
                    </TableCell>
                    <TableCell>
                      {new Date(request.createdAt).toLocaleString()}
                    </TableCell>
                    <TableCell>
                      <Stack direction="row" spacing={1}>
                        <Button
                          size="small"
                          variant="contained"
                          color="success"
                          onClick={() => handleApprove(request.id)}
                        >
                          Approve
                        </Button>
                        <Button
                          size="small"
                          variant="contained"
                          color="error"
                          onClick={() => handleReject(request.id)}
                        >
                          Reject
                        </Button>
                        <Button
                          size="small"
                          variant="contained"
                          color="info"
                          onClick={() => handleEscalate(request.id)}
                        >
                          Escalate
                        </Button>
                        <Button
                          size="small"
                          component={RouterLink}
                          to={`/requests/${request.id}`}
                        >
                          Details
                        </Button>
                      </Stack>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </Paper>

      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
        <Button
          variant="contained"
          color="primary"
          component={RouterLink}
          to="/submit-request"
        >
          Submit New Request
        </Button>
      </Box>
    </Box>
  );
};

export default Dashboard; 