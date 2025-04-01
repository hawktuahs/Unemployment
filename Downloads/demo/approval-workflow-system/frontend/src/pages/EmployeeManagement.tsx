import React, { useState, useEffect } from 'react';
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
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Switch,
  FormControlLabel,
  Alert,
  CircularProgress,
  Grid,
  SelectChangeEvent,
} from '@mui/material';
import { Employee } from '../types';
import {
  getAllEmployees,
  createEmployee,
  updateSupervisor,
  toggleAvailability,
} from '../services/employeeService';

const EmployeeManagement = () => {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>('');
  const [success, setSuccess] = useState<string>('');
  
  // Create employee dialog
  const [openCreate, setOpenCreate] = useState<boolean>(false);
  const [newEmployee, setNewEmployee] = useState({
    name: '',
    email: '',
    position: '',
    managerId: '',
  });
  
  // Update supervisor dialog
  const [openUpdateSupervisor, setOpenUpdateSupervisor] = useState<boolean>(false);
  const [updateData, setUpdateData] = useState({
    employeeId: '',
    newSupervisorId: '',
  });

  useEffect(() => {
    fetchEmployees();
  }, []);

  const fetchEmployees = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await getAllEmployees();
      setEmployees(data);
    } catch (err) {
      setError('Failed to load employees');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // Handle create employee dialog
  const handleOpenCreateDialog = () => {
    setOpenCreate(true);
  };

  const handleCloseCreateDialog = () => {
    setOpenCreate(false);
    setNewEmployee({
      name: '',
      email: '',
      position: '',
      managerId: '',
    });
  };

  const handleCreateInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setNewEmployee((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleCreateManagerChange = (event: SelectChangeEvent) => {
    setNewEmployee((prev) => ({
      ...prev,
      managerId: event.target.value,
    }));
  };

  const handleCreateEmployee = async () => {
    if (!newEmployee.name || !newEmployee.email || !newEmployee.position) {
      setError('Please fill in all required fields');
      return;
    }

    setLoading(true);
    setError('');
    setSuccess('');
    try {
      await createEmployee({
        name: newEmployee.name,
        email: newEmployee.email,
        position: newEmployee.position,
        managerId: newEmployee.managerId ? Number(newEmployee.managerId) : null,
        managerName: null,
        availabilityStatus: 'AVAILABLE',
      });
      setSuccess('Employee created successfully');
      handleCloseCreateDialog();
      await fetchEmployees();
    } catch (err) {
      setError('Failed to create employee');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // Handle update supervisor dialog
  const handleOpenUpdateSupervisorDialog = (employeeId: number) => {
    setUpdateData({
      employeeId: employeeId.toString(),
      newSupervisorId: '',
    });
    setOpenUpdateSupervisor(true);
  };

  const handleCloseUpdateSupervisorDialog = () => {
    setOpenUpdateSupervisor(false);
    setUpdateData({
      employeeId: '',
      newSupervisorId: '',
    });
  };

  const handleUpdateSupervisorChange = (event: SelectChangeEvent) => {
    setUpdateData((prev) => ({
      ...prev,
      newSupervisorId: event.target.value,
    }));
  };

  const handleUpdateSupervisor = async () => {
    if (!updateData.newSupervisorId) {
      setError('Please select a new supervisor');
      return;
    }

    setLoading(true);
    setError('');
    setSuccess('');
    try {
      await updateSupervisor(
        Number(updateData.employeeId),
        Number(updateData.newSupervisorId)
      );
      setSuccess('Supervisor updated successfully');
      handleCloseUpdateSupervisorDialog();
      await fetchEmployees();
    } catch (err) {
      setError('Failed to update supervisor');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // Handle toggle availability
  const handleToggleAvailability = async (employeeId: number) => {
    setLoading(true);
    setError('');
    setSuccess('');
    try {
      await toggleAvailability(employeeId);
      setSuccess('Availability toggled successfully');
      await fetchEmployees();
    } catch (err) {
      setError('Failed to toggle availability');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        Employee Management
      </Typography>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

      <Box sx={{ mb: 3, display: 'flex', justifyContent: 'flex-end' }}>
        <Button
          variant="contained"
          color="primary"
          onClick={handleOpenCreateDialog}
        >
          Create New Employee
        </Button>
      </Box>

      {loading && !employees.length ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
          <CircularProgress />
        </Box>
      ) : (
        <Paper>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Name</TableCell>
                  <TableCell>Email</TableCell>
                  <TableCell>Position</TableCell>
                  <TableCell>Manager/Supervisor</TableCell>
                  <TableCell>Availability</TableCell>
                  <TableCell>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {employees.map((employee) => (
                  <TableRow key={employee.id}>
                    <TableCell>{employee.id}</TableCell>
                    <TableCell>{employee.name}</TableCell>
                    <TableCell>{employee.email}</TableCell>
                    <TableCell>{employee.position}</TableCell>
                    <TableCell>{employee.managerName || 'None'}</TableCell>
                    <TableCell>
                      <FormControlLabel
                        control={
                          <Switch
                            checked={employee.availabilityStatus === 'AVAILABLE'}
                            onChange={() => handleToggleAvailability(employee.id)}
                            color="primary"
                          />
                        }
                        label={employee.availabilityStatus}
                      />
                    </TableCell>
                    <TableCell>
                      <Button
                        size="small"
                        variant="outlined"
                        onClick={() => handleOpenUpdateSupervisorDialog(employee.id)}
                      >
                        Change Supervisor
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Paper>
      )}

      {/* Create Employee Dialog */}
      <Dialog open={openCreate} onClose={handleCloseCreateDialog}>
        <DialogTitle>Create New Employee</DialogTitle>
        <DialogContent>
          <Box component="form" sx={{ mt: 1 }}>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField
                  autoFocus
                  required
                  fullWidth
                  id="name"
                  name="name"
                  label="Name"
                  value={newEmployee.name}
                  onChange={handleCreateInputChange}
                  margin="dense"
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  required
                  fullWidth
                  id="email"
                  name="email"
                  label="Email"
                  type="email"
                  value={newEmployee.email}
                  onChange={handleCreateInputChange}
                  margin="dense"
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  required
                  fullWidth
                  id="position"
                  name="position"
                  label="Position"
                  value={newEmployee.position}
                  onChange={handleCreateInputChange}
                  margin="dense"
                />
              </Grid>
              <Grid item xs={12}>
                <FormControl fullWidth margin="dense">
                  <InputLabel id="manager-select-label">Manager/Supervisor</InputLabel>
                  <Select
                    labelId="manager-select-label"
                    id="manager-select"
                    value={newEmployee.managerId}
                    label="Manager/Supervisor"
                    onChange={handleCreateManagerChange}
                  >
                    <MenuItem value="">
                      <em>None</em>
                    </MenuItem>
                    {employees.map((employee) => (
                      <MenuItem key={employee.id} value={employee.id.toString()}>
                        {employee.name} ({employee.position})
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
            </Grid>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseCreateDialog}>Cancel</Button>
          <Button onClick={handleCreateEmployee} variant="contained" disabled={loading}>
            {loading ? <CircularProgress size={24} /> : 'Create'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Update Supervisor Dialog */}
      <Dialog open={openUpdateSupervisor} onClose={handleCloseUpdateSupervisorDialog}>
        <DialogTitle>Update Supervisor</DialogTitle>
        <DialogContent>
          <Box sx={{ mt: 1 }}>
            <FormControl fullWidth margin="dense">
              <InputLabel id="new-supervisor-select-label">New Supervisor</InputLabel>
              <Select
                labelId="new-supervisor-select-label"
                id="new-supervisor-select"
                value={updateData.newSupervisorId}
                label="New Supervisor"
                onChange={handleUpdateSupervisorChange}
              >
                <MenuItem value="">
                  <em>None</em>
                </MenuItem>
                {employees
                  .filter((e) => e.id.toString() !== updateData.employeeId)
                  .map((employee) => (
                    <MenuItem key={employee.id} value={employee.id.toString()}>
                      {employee.name} ({employee.position})
                    </MenuItem>
                  ))}
              </Select>
            </FormControl>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseUpdateSupervisorDialog}>Cancel</Button>
          <Button onClick={handleUpdateSupervisor} variant="contained" disabled={loading}>
            {loading ? <CircularProgress size={24} /> : 'Update'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default EmployeeManagement; 