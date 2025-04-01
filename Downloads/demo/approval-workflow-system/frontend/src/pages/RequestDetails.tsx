import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Typography,
  Paper,
  Box,
  Button,
  Chip,
  Grid,
  CircularProgress,
  Alert,
  Divider,
  Stack,
} from '@mui/material';
import { ApprovalRequest } from '../types';
import { getRequestById, approveRequest, rejectRequest, escalateRequest } from '../services/approvalService';

const RequestDetails = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [request, setRequest] = useState<ApprovalRequest | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [actionLoading, setActionLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>('');
  const [actionSuccess, setActionSuccess] = useState<string>('');

  useEffect(() => {
    fetchRequestDetails();
  }, [id]);

  const fetchRequestDetails = async () => {
    if (!id) return;
    
    setLoading(true);
    setError('');
    try {
      const data = await getRequestById(Number(id));
      setRequest(data);
    } catch (err) {
      setError('Failed to load request details');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async () => {
    if (!request || !request.approverId) return;
    
    setActionLoading(true);
    setError('');
    setActionSuccess('');
    try {
      const updatedRequest = await approveRequest(request.id, request.approverId);
      setRequest(updatedRequest);
      setActionSuccess('Request approved successfully');
    } catch (err) {
      setError('Failed to approve request');
      console.error(err);
    } finally {
      setActionLoading(false);
    }
  };

  const handleReject = async () => {
    if (!request || !request.approverId) return;
    
    setActionLoading(true);
    setError('');
    setActionSuccess('');
    try {
      const updatedRequest = await rejectRequest(request.id, request.approverId);
      setRequest(updatedRequest);
      setActionSuccess('Request rejected successfully');
    } catch (err) {
      setError('Failed to reject request');
      console.error(err);
    } finally {
      setActionLoading(false);
    }
  };

  const handleEscalate = async () => {
    if (!request) return;
    
    setActionLoading(true);
    setError('');
    setActionSuccess('');
    try {
      const updatedRequest = await escalateRequest(request.id);
      setRequest(updatedRequest);
      setActionSuccess('Request escalated successfully');
    } catch (err) {
      setError('Failed to escalate request');
      console.error(err);
    } finally {
      setActionLoading(false);
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

  const canTakeAction = (status: string) => {
    return status === 'PENDING' || status === 'ESCALATED';
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '300px' }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error && !request) {
    return (
      <Box sx={{ mt: 2 }}>
        <Alert severity="error">{error}</Alert>
        <Box sx={{ mt: 2, textAlign: 'center' }}>
          <Button variant="contained" onClick={() => navigate('/')}>
            Back to Dashboard
          </Button>
        </Box>
      </Box>
    );
  }

  if (!request) {
    return (
      <Box sx={{ mt: 2 }}>
        <Alert severity="error">Request not found</Alert>
        <Box sx={{ mt: 2, textAlign: 'center' }}>
          <Button variant="contained" onClick={() => navigate('/')}>
            Back to Dashboard
          </Button>
        </Box>
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        Request Details
      </Typography>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {actionSuccess && <Alert severity="success" sx={{ mb: 2 }}>{actionSuccess}</Alert>}

      <Paper sx={{ p: 3 }}>
        <Grid container spacing={2}>
          <Grid item xs={12} sm={6}>
            <Typography variant="subtitle2" color="text.secondary">
              Request ID
            </Typography>
            <Typography variant="body1" gutterBottom>
              {request.id}
            </Typography>
          </Grid>
          <Grid item xs={12} sm={6}>
            <Typography variant="subtitle2" color="text.secondary">
              Status
            </Typography>
            <Chip
              label={request.status}
              color={getStatusChipColor(request.status) as any}
              size="small"
              sx={{ mt: 0.5 }}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <Typography variant="subtitle2" color="text.secondary">
              Requester
            </Typography>
            <Typography variant="body1" gutterBottom>
              {request.requesterName}
            </Typography>
          </Grid>
          <Grid item xs={12} sm={6}>
            <Typography variant="subtitle2" color="text.secondary">
              Approver
            </Typography>
            <Typography variant="body1" gutterBottom>
              {request.approverName || 'Not assigned'}
            </Typography>
          </Grid>
          <Grid item xs={12} sm={6}>
            <Typography variant="subtitle2" color="text.secondary">
              Created At
            </Typography>
            <Typography variant="body1" gutterBottom>
              {new Date(request.createdAt).toLocaleString()}
            </Typography>
          </Grid>
          <Grid item xs={12} sm={6}>
            <Typography variant="subtitle2" color="text.secondary">
              Last Updated
            </Typography>
            <Typography variant="body1" gutterBottom>
              {new Date(request.updatedAt).toLocaleString()}
            </Typography>
          </Grid>
          <Grid item xs={12}>
            <Divider sx={{ my: 2 }} />
            <Typography variant="subtitle2" color="text.secondary">
              Description
            </Typography>
            <Typography variant="body1" sx={{ mt: 1, mb: 2 }}>
              {request.description}
            </Typography>
          </Grid>
        </Grid>

        <Box sx={{ mt: 3, display: 'flex', justifyContent: 'space-between' }}>
          <Button
            variant="outlined"
            onClick={() => navigate('/')}
          >
            Back to Dashboard
          </Button>
          
          {canTakeAction(request.status) && request.approverId && (
            <Stack direction="row" spacing={2}>
              <Button
                variant="contained"
                color="success"
                onClick={handleApprove}
                disabled={actionLoading}
              >
                {actionLoading ? <CircularProgress size={24} /> : 'Approve'}
              </Button>
              <Button
                variant="contained"
                color="error"
                onClick={handleReject}
                disabled={actionLoading}
              >
                {actionLoading ? <CircularProgress size={24} /> : 'Reject'}
              </Button>
              <Button
                variant="contained"
                color="info"
                onClick={handleEscalate}
                disabled={actionLoading || request.status === 'ESCALATED'}
              >
                {actionLoading ? <CircularProgress size={24} /> : 'Escalate'}
              </Button>
            </Stack>
          )}
        </Box>
      </Paper>
    </Box>
  );
};

export default RequestDetails; 