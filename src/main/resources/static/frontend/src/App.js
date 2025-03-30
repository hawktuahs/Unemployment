import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import Navbar from './components/Navbar';
import Dashboard from './pages/Dashboard';
import EmployeeList from './pages/EmployeeList';
import EmployeeForm from './pages/EmployeeForm';
import RequestList from './pages/RequestList';
import RequestForm from './pages/RequestForm';
import RequestDetails from './pages/RequestDetails';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
    background: {
      default: '#f5f5f5',
    },
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router>
        <Navbar />
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/employees" element={<EmployeeList />} />
          <Route path="/employees/new" element={<EmployeeForm />} />
          <Route path="/employees/edit/:id" element={<EmployeeForm />} />
          <Route path="/requests" element={<RequestList />} />
          <Route path="/requests/new" element={<RequestForm />} />
          <Route path="/requests/:id" element={<RequestDetails />} />
        </Routes>
      </Router>
    </ThemeProvider>
  );
}

export default App;