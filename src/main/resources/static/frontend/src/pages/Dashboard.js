import React, { useState, useEffect } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import axios from 'axios';
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardActions from '@mui/material/CardActions';

const Dashboard = () => {
  const [employeeCount, setEmployeeCount] = useState(0);
  const [pendingRequests, setPendingRequests] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        // Fetch employee count
        const employeeResponse = await axios.get('/api/employees');
        setEmployeeCount(employeeResponse.data.length);

        // Fetch all requests to count pending ones
        const requestsResponse = await axios.get('/api/approvals');
        const pending = requestsResponse.data.filter(req => req.status === 'PENDING').length;
        setPendingRequests(pending);

        setLoading(false);
      } catch (err) {
        setError('Failed to load dashboard data');
        setLoading(false);
        console.error(err);
      }
    };

    fetchDashboardData();
  }, []);

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Typography>Loading dashboard data...</Typography>
      </Container>
    );
  }

  if (error) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Typography color="error">{error}</Typography>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom>
        Dashboard
      </Typography>
      <Grid container spacing={3}>
        {/* Employee Summary */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h5" component="div">
                Employees
              </Typography>
              <Typography variant="h3" color="primary" sx={{ mt: 2 }}>
                {employeeCount}
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                Total employees in the organization
              </Typography>
            </CardContent>
            <CardActions>
              <Button size="small" component={RouterLink} to="/employees">
                View All Employees
              </Button>
              <Button size="small" component={RouterLink} to="/employees/new">
                Add New Employee
              </Button>
            </CardActions>
          </Card>
        </Grid>

        {/* Approval Requests Summary */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h5" component="div">
                Pending Approvals
              </Typography>
              <Typography variant="h3" color="secondary" sx={{ mt: 2 }}>
                {pendingRequests}
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                Requests waiting for approval
              </Typography>
            </CardContent>
            <CardActions>
              <Button size="small" component={RouterLink} to="/requests">
                View All Requests
              </Button>
              <Button size="small" component={RouterLink} to="/requests/new">
                Create New Request
              </Button>
            </CardActions>
          </Card>
        </Grid>

        {/* Quick Info */}
        <Grid item xs={12}>
          <Paper sx={{ p: 3, mt: 3 }}>
            <Typography variant="h6" gutterBottom>
              About the Approval Hierarchy System
            </Typography>
            <Typography variant="body1" paragraph>
              This system manages approval workflows in an organizational hierarchy. When an approval is required, it is routed to the immediate superior. If the manager is unavailable, the request is automatically escalated to the manager's superior.
            </Typography>
            <Box sx={{ mt: 2 }}>
              <Button variant="contained" component={RouterLink} to="/requests/new">
                Submit New Approval Request
              </Button>
            </Box>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
};

export default Dashboard;