import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Typography,
  Paper,
  Box,
  TextField,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  CircularProgress,
  SelectChangeEvent,
} from '@mui/material';
import { Employee } from '../types';
import { getAllEmployees } from '../services/employeeService';
import { createRequest } from '../services/approvalService';

const RequestForm = () => {
  const navigate = useNavigate();
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [requesterId, setRequesterId] = useState<number | ''>('');
  const [description, setDescription] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>('');
  const [success, setSuccess] = useState<boolean>(false);

  useEffect(() => {
    const fetchEmployees = async () => {
      try {
        const data = await getAllEmployees();
        setEmployees(data);
      } catch (err) {
        setError('Failed to load employees');
        console.error(err);
      }
    };

    fetchEmployees();
  }, []);

  const handleRequesterChange = (event: SelectChangeEvent<number | ''>) => {
    setRequesterId(event.target.value as number);
  };

  const handleDescriptionChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setDescription(event.target.value);
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    
    if (!requesterId || !description.trim()) {
      setError('Please fill in all required fields');
      return;
    }

    setLoading(true);
    setError('');
    try {
      await createRequest({
        requesterId: requesterId as number,
        description: description.trim(),
      });
      
      setSuccess(true);
      setTimeout(() => {
        navigate('/');
      }, 2000);
    } catch (err) {
      setError('Failed to submit request');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        Submit New Request
      </Typography>

      <Paper sx={{ p: 3, maxWidth: 600, mx: 'auto' }}>
        {success ? (
          <Alert severity="success" sx={{ mb: 2 }}>
            Request submitted successfully! Redirecting to dashboard...
          </Alert>
        ) : (
          <form onSubmit={handleSubmit}>
            {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

            <FormControl fullWidth sx={{ mb: 3 }}>
              <InputLabel id="requester-select-label">Requester</InputLabel>
              <Select
                labelId="requester-select-label"
                id="requester-select"
                value={requesterId}
                label="Requester"
                onChange={handleRequesterChange}
                required
              >
                {employees.map((employee) => (
                  <MenuItem key={employee.id} value={employee.id}>
                    {employee.name} ({employee.position})
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <TextField
              fullWidth
              id="description"
              label="Request Description"
              multiline
              rows={4}
              value={description}
              onChange={handleDescriptionChange}
              margin="normal"
              required
              sx={{ mb: 3 }}
            />

            <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
              <Button
                variant="outlined"
                onClick={() => navigate('/')}
              >
                Cancel
              </Button>
              <Button
                type="submit"
                variant="contained"
                color="primary"
                disabled={loading}
              >
                {loading ? <CircularProgress size={24} /> : 'Submit Request'}
              </Button>
            </Box>
          </form>
        )}
      </Paper>
    </Box>
  );
};

export default RequestForm; 