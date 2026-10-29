package com.legal.diary.controller;

import com.legal.diary.model.CaseStatus;
import com.legal.diary.model.LegalCase;
import com.legal.diary.service.CaseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

/**
 * Controller serving the executive dashboard and judicial statistics overview.
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
@Controller
public class DashboardController {

    private final CaseService caseService;

    public DashboardController(CaseService caseService) {
        this.caseService = caseService;
    }

    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        Map<CaseStatus, Long> counts = caseService.getCaseCountsByStatus();
        long total = counts.values().stream().mapToLong(Long::longValue).sum();
        List<LegalCase> upcomingHearings = caseService.getUpcomingHearings();

        model.addAttribute("counts", counts);
        model.addAttribute("totalCases", total);
        model.addAttribute("upcomingHearings", upcomingHearings);
        model.addAttribute("activeNav", "dashboard");

        return "dashboard";
    }
}
