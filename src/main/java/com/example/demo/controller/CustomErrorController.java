package com.example.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Custom error controller to handle errors and display custom error pages
 * instead of the default Whitelabel Error Page.
 */
@Controller
public class CustomErrorController implements ErrorController {

    /**
     * Handles all error requests and returns the appropriate error view
     * @param request The HTTP request
     * @param model The model to add attributes to
     * @return The name of the error view to render
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Get error status code
        Object status = request.getAttribute("jakarta.servlet.error.status_code");
        
        // Add error details to the model
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            model.addAttribute("status", statusCode);
            
            // Add error message based on status code
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("error", "Not Found");
                model.addAttribute("message", "The page you are looking for does not exist.");
                return "error/404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("error", "Internal Server Error");
                model.addAttribute("message", "Something went wrong on our end. Please try again later.");
                return "error/500";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("error", "Forbidden");
                model.addAttribute("message", "You don't have permission to access this resource.");
            } else {
                model.addAttribute("error", "Error");
                model.addAttribute("message", "An unexpected error occurred.");
            }
        } else {
            model.addAttribute("status", 500);
            model.addAttribute("error", "Unknown Error");
            model.addAttribute("message", "An unexpected error occurred.");
        }
        
        return "error";
    }
}