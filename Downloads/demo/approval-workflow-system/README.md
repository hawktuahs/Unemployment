# Approval Workflow System

A complete approval workflow system with a Spring Boot backend and React frontend that allows managing employee hierarchies and processing approval requests in an organization.

## Features

- Employee Management
  - Create employees with positions and supervisors
  - Update employee supervisors
  - Toggle employee availability
  - View all employees with their position and supervisor details

- Approval Workflow
  - Submit approval requests
  - View pending approval requests
  - Approve/Reject requests
  - Escalate requests to higher management
  - Automatic escalation when a supervisor is unavailable

## Project Structure

The project consists of two main parts:

1. Backend (Spring Boot)
   - REST API with proper layer separation (controller, service, repository, entity)
   - H2 in-memory database for easy setup
   - Built with Java 11 and Spring Boot 2.7

2. Frontend (React)
   - Modern UI with Material-UI components
   - TypeScript for better code quality
   - Responsive design for all screen sizes

## Prerequisites

- Java 11 or higher
- Node.js 14 or higher
- npm 6 or higher

## Getting Started

### Running the Backend

1. Navigate to the backend directory:
   ```
   cd approval-workflow-system/backend
   ```

2. Build and run the Spring Boot application:
   ```
   ./mvnw spring-boot:run
   ```
   Or on Windows:
   ```
   mvnw.cmd spring-boot:run
   ```

3. The backend API will be available at: http://localhost:8080/api

### Running the Frontend

1. Navigate to the frontend directory:
   ```
   cd approval-workflow-system/frontend
   ```

2. Install dependencies:
   ```
   npm install
   ```

3. Start the development server:
   ```
   npm start
   ```

4. The frontend application will be available at: http://localhost:3000

## Database

The application uses an H2 in-memory database that is initialized with sample data when the application starts:

- Employee 1: Alice (Principal, no manager)
- Employee 2: Bob (Supervisor, reports to Alice)
- Employee 3: Charlie (Teacher, reports to Bob, currently unavailable)

You can access the H2 console at http://localhost:8080/h2-console with the following credentials:
- JDBC URL: jdbc:h2:mem:approvaldb
- Username: sa
- Password: password

## API Endpoints

### Employee Endpoints

- `GET /api/employees` - Get all employees
- `GET /api/employees/{id}` - Get employee by ID
- `POST /api/employees` - Create a new employee
- `PUT /api/employees/{id}/supervisor` - Update employee's supervisor
- `PUT /api/employees/{id}/toggle-availability` - Toggle employee availability

### Approval Request Endpoints

- `GET /api/approvals` - Get all approval requests
- `GET /api/approvals/{id}` - Get approval request by ID
- `GET /api/approvals/employee/{id}` - Get requests for a specific employee
- `GET /api/approvals/pending/{approverId}` - Get pending requests for an approver
- `POST /api/approvals` - Submit a new approval request
- `PUT /api/approvals/{id}/approve` - Approve a request
- `PUT /api/approvals/{id}/reject` - Reject a request
- `PUT /api/approvals/{id}/escalate` - Escalate a request

## Workflow Example

1. Log in as different employees to see their dashboard
2. Submit a request as a teacher (Charlie)
3. The request will automatically be assigned to their supervisor (Bob)
4. If Bob is unavailable, the request is automatically escalated to Alice
5. Alice can approve, reject, or escalate the request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 