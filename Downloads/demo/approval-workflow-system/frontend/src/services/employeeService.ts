import api from './api';
import { Employee } from '../types';

export const getAllEmployees = async (): Promise<Employee[]> => {
  const response = await api.get('/employees');
  return response.data;
};

export const getEmployeeById = async (id: number): Promise<Employee> => {
  const response = await api.get(`/employees/${id}`);
  return response.data;
};

export const createEmployee = async (employee: Omit<Employee, 'id'>): Promise<Employee> => {
  const response = await api.post('/employees', employee);
  return response.data;
};

export const updateSupervisor = async (employeeId: number, newSupervisorId: number): Promise<Employee> => {
  const response = await api.put(`/employees/${employeeId}/supervisor`, { newSupervisorId });
  return response.data;
};

export const toggleAvailability = async (employeeId: number): Promise<Employee> => {
  const response = await api.put(`/employees/${employeeId}/toggle-availability`);
  return response.data;
};