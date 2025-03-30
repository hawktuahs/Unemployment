import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import Container from '@mui/material/Container';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import Alert from '@mui/material/Alert';
import Grid from '@mui/material/Grid';

const EmployeeForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEditMode = !!id;

  const [formData, setFormData] = useState({
    name: '',
    email: '',
    position: '',
    managerId: ''
  });
  
  const [managers, setManagers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [formErrors, setFormErrors] = useState({});

  useEffect(() => {
    const fetchManagers = async () => {
      try {
        const response = await axios.get('/api/employees');
        setManagers(response.data);
      } catch (err) {
        console.error('Failed to fetch managers:', err);
        setError('Failed to load managers');
      }
    };

    fetchManagers();

    if (isEditMode) {
      const fetchEmployee = async () => {
        try {
          setLoading(true);
          const response = await axios.get(`/api/employees/${id}`);
          const employee = response.data;
          
          setFormData({
            name: employee.name,
            email: employee.email,
            position: employee.position,
            managerId: employee.manager ? employee.manager.id : ''
          });
          setLoading(false);
        } catch (err) {
          console.error('Failed to fetch employee:', err);
          setError('Failed to load employee data');
          setLoading(false);
        }
      };

      fetchEmployee();
    }
  }, [id, isEditMode]);

  const validateForm = () => {
    const errors = {};
    if (!formData.name.trim()) errors.name = 'Name is required';
    if (!formData.email.trim()) {
      errors.email = 'Email is required';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      errors.email = 'Invalid email format';
    }
    if (!formData.position.trim()) errors.position = 'Position is required';
    
    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Clear error for this field when user types
    if (formErrors[name]) {
      setFormErrors(prev => ({
        ...prev,
        [name]: null
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;
    
    try {
      setLoading(true);
      setError(null);
      
      const employeeData = {
        name: formData.name,
        email: formData.email,
        position: formData.position,
        manager: formData.managerId ? { id: formData.managerId } : null,
        available: true
      };

      if (isEditMode) {
        await axios.put(`/api/employees/${id}`, employeeData);
      } else {
        await axios.post('/api/employees', employeeData);
      }

      setLoading(false);
      navigate('/employees');
    } catch (err) {
      console.error('Failed to save employee:', err);
      setError('Failed to save employee. Please try again.');
      setLoading(false);
    }
  };

  if (loading && isEditMode) {
    return (
      <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
        <Typography>Loading employee data...</Typography>
      </Container>
    );
  }

  return (
    <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
      <Paper sx={{ p: 3 }}>
        <Typography variant="h4" gutterBottom>
          {isEditMode ? 'Edit Employee' : 'Add New Employee'}
        </Typography>

        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        <Box component="form" onSubmit={handleSubmit} noValidate>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                label="Name"
                name="name"
                value={formData.name}
                onChange={handleChange}
                error={!!formErrors.name}
                helperText={formErrors.name}
                margin="normal"
              />
            </Grid>
            
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                label="Email"
                name="email"
                type="email"
                value={formData.email}
                onChange={handleChange}
                error={!!formErrors.email}
                helperText={formErrors.email}
                margin="normal"
              />
            </Grid>
            
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                label="Position"
                name="position"
                value={formData.position}
                onChange={handleChange}
                error={!!formErrors.position}
                helperText={formErrors.position}
                margin="normal"
              />
            </Grid>
            
            <Grid item xs={12}>
              <FormControl fullWidth margin="normal">
                <InputLabel id="manager-label">Manager</InputLabel>
                <Select
                  labelId="manager-label"
                  name="managerId"
                  value={formData.managerId}
                  onChange={handleChange}
                  label="Manager"
                >
                  <MenuItem value="">None</MenuItem>
                  {managers
                    .filter(manager => manager.id !== id) // Prevent selecting self as manager
                    .map(manager => (
                      <MenuItem key={manager.id} value={manager.id}>
                        {manager.name} - {manager.position}
                      </MenuItem>
                    ))
                  }
                </Select>
              </FormControl>
            </Grid>
          </Grid>

          <Box sx={{ mt: 3, display: 'flex', justifyContent: 'space-between' }}>
            <Button
              variant="outlined"
              onClick={() => navigate('/employees')}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              variant="contained"
              color="primary"
              disabled={loading}
            >
              {loading ? 'Saving...' : 'Save'}
            </Button>
          </Box>
        </Box>
      </Paper>
    </Container>
  );
};

export default EmployeeForm;