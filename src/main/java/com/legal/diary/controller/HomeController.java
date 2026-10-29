package com.legal.diary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for authentication and login views.
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
@Controller
public class HomeController {

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid credentials. Please verify your username and password.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "Session terminated. You have been safely logged out.");
        }
        return "login";
    }
}
