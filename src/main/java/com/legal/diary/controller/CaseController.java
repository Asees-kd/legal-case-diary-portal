package com.legal.diary.controller;

import com.legal.diary.model.CaseStatus;
import com.legal.diary.model.CaseStatusHistory;
import com.legal.diary.model.CaseType;
import com.legal.diary.model.LegalCase;
import com.legal.diary.service.CaseService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Controller managing legal case registration, search, case details,
 * and role-governed state transitions.
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
@Controller
@RequestMapping("/cases")
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @GetMapping
    public String listCases(@RequestParam(value = "query", required = false) String query,
                            @RequestParam(value = "status", required = false) CaseStatus status,
                            Model model, Authentication auth) {
        List<LegalCase> cases = caseService.searchCases(query, status);
        model.addAttribute("cases", cases);
        model.addAttribute("searchQuery", query);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("caseStatuses", CaseStatus.values());
        model.addAttribute("activeNav", "cases");

        boolean canCreate = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLERK") || a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("canCreate", canCreate);

        return "cases";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('CLERK', 'ADMIN')")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("legalCase")) {
            LegalCase legalCase = new LegalCase();
            legalCase.setFiledDate(LocalDate.now());
            model.addAttribute("legalCase", legalCase);
        }
        model.addAttribute("caseTypes", CaseType.values());
        model.addAttribute("activeNav", "new-case");
        return "create";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLERK', 'ADMIN')")
    public String processCreateCase(@Valid @ModelAttribute("legalCase") LegalCase legalCase,
                                    BindingResult bindingResult,
                                    Authentication auth,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("caseTypes", CaseType.values());
            model.addAttribute("activeNav", "new-case");
            return "create";
        }

        try {
            String username = auth != null ? auth.getName() : "clerk";
            LegalCase created = caseService.createCase(legalCase, username);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Case '" + created.getCaseNumber() + "' filed successfully in registry.");
            return "redirect:/cases";
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("caseNumber", "error.caseNumber", ex.getMessage());
            model.addAttribute("caseTypes", CaseType.values());
            model.addAttribute("activeNav", "new-case");
            return "create";
        }
    }

    @GetMapping("/{id}")
    public String viewCaseDetail(@PathVariable("id") Long id, Model model, Authentication auth) {
        LegalCase legalCase = caseService.getCaseById(id)
                .orElseThrow(() -> new NoSuchElementException("Legal case not found with ID: " + id));

        List<CaseStatusHistory> histories = caseService.getStatusHistory(id);

        boolean isLawyer = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_LAWYER"));
        boolean isAdmin = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isClerk = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLERK"));

        model.addAttribute("caseItem", legalCase);
        model.addAttribute("histories", histories);
        model.addAttribute("isLawyer", isLawyer);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isClerk", isClerk);
        model.addAttribute("activeNav", "cases");

        return "detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable("id") Long id,
                               @RequestParam("targetStatus") CaseStatus targetStatus,
                               @RequestParam(value = "hearingDate", required = false)
                               @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate hearingDate,
                               @RequestParam(value = "hearingNotes", required = false) String hearingNotes,
                               @RequestParam(value = "remarks", required = false) String remarks,
                               Authentication auth,
                               RedirectAttributes redirectAttributes) {
        try {
            String username = auth != null ? auth.getName() : "user";
            caseService.transitionStatus(id, targetStatus, hearingDate, hearingNotes, remarks, username, auth.getAuthorities());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Case workflow updated successfully to " + targetStatus.getDisplayName() + ".");
        } catch (AccessDeniedException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/cases/" + id;
    }
}
