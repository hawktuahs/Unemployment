import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class MockServer {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/employees", new EmployeesHandler());
        server.createContext("/api/approvals", new ApprovalsHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Mock server is running on port 8080...");
    }

    static class EmployeesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = """
                [
                  {
                    "id": 1,
                    "name": "Alice",
                    "email": "alice@example.com",
                    "managerId": null,
                    "managerName": null,
                    "position": "Principal",
                    "availabilityStatus": "AVAILABLE"
                  },
                  {
                    "id": 2,
                    "name": "Bob",
                    "email": "bob@example.com",
                    "managerId": 1,
                    "managerName": "Alice",
                    "position": "Supervisor",
                    "availabilityStatus": "AVAILABLE"
                  },
                  {
                    "id": 3,
                    "name": "Charlie",
                    "email": "charlie@example.com",
                    "managerId": 2,
                    "managerName": "Bob",
                    "position": "Teacher",
                    "availabilityStatus": "UNAVAILABLE"
                  }
                ]
                """;
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type,Authorization");
            
            if (exchange.getRequestMethod().equals("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    static class ApprovalsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = """
                [
                  {
                    "id": 1,
                    "requesterId": 3,
                    "requesterName": "Charlie",
                    "approverId": 2,
                    "approverName": "Bob",
                    "status": "PENDING",
                    "description": "Request for project funding",
                    "createdAt": "2023-03-01T10:00:00",
                    "updatedAt": "2023-03-01T10:00:00"
                  },
                  {
                    "id": 2,
                    "requesterId": 3,
                    "requesterName": "Charlie",
                    "approverId": 1,
                    "approverName": "Alice",
                    "status": "ESCALATED",
                    "description": "Request for time off",
                    "createdAt": "2023-03-02T14:30:00",
                    "updatedAt": "2023-03-02T15:45:00"
                  }
                ]
                """;
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type,Authorization");
            
            if (exchange.getRequestMethod().equals("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            
            if (path.matches("/api/approvals/pending/\\d+")) {
                // Handle pending approvals for a specific approver
                response = """
                    [
                      {
                        "id": 1,
                        "requesterId": 3,
                        "requesterName": "Charlie",
                        "approverId": 2,
                        "approverName": "Bob",
                        "status": "PENDING",
                        "description": "Request for project funding",
                        "createdAt": "2023-03-01T10:00:00",
                        "updatedAt": "2023-03-01T10:00:00"
                      }
                    ]
                    """;
            }
            
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
} 