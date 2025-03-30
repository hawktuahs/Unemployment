import React from 'react';
import { Link as RouterLink } from 'react-router-dom';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Container from '@mui/material/Container';

const Navbar = () => {
  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <Container>
          <Toolbar>
            <Typography variant="h6" component={RouterLink} to="/" sx={{ flexGrow: 1, textDecoration: 'none', color: 'white' }}>
              Approval Hierarchy System
            </Typography>
            <Button color="inherit" component={RouterLink} to="/employees">
              Employees
            </Button>
            <Button color="inherit" component={RouterLink} to="/requests">
              Requests
            </Button>
          </Toolbar>
        </Container>
      </AppBar>
    </Box>
  );
};

export default Navbar;