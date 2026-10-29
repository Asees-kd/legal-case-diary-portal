package com.legal.diary.service;

import com.legal.diary.model.CaseStatus;
import com.legal.diary.model.CaseType;
import com.legal.diary.model.LegalCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit & Integration test suite validating CaseService lifecycle and RBAC state transitions:
 * FILED -> UNDER_REVIEW -> HEARING_SCHEDULED -> CLOSED
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
@SpringBootTest
@Transactional
class CaseServiceTest {

    @Autowired
    private CaseService caseService;

    @Test
    @DisplayName("Should create case in FILED status with initial history")
    void testCreateCase() {
        LegalCase testCase = new LegalCase(
                "TEST/2026/001",
                "Sharma vs. State of Punjab",
                CaseType.CIVIL,
                "Punjab & Haryana High Court",
                "Ramesh Sharma",
                "State of Punjab",
                LocalDate.now()
        );

        LegalCase created = caseService.createCase(testCase, "clerk");
        assertNotNull(created.getId());
        assertEquals("TEST/2026/001", created.getCaseNumber());
        assertEquals(CaseStatus.FILED, created.getStatus());
        assertEquals("clerk", created.getCreatedBy());

        var histories = caseService.getStatusHistory(created.getId());
        assertFalse(histories.isEmpty());
        assertEquals(CaseStatus.FILED, histories.get(0).getToStatus());
    }

    @Test
    @DisplayName("Lawyer can transition case from FILED to UNDER_REVIEW")
    void testLawyerTransitionToReview() {
        LegalCase testCase = new LegalCase(
                "TEST/2026/002",
                "Verma vs. Verma",
                CaseType.FAMILY,
                "District Court, Delhi",
                "Priya Verma",
                "Amit Verma",
                LocalDate.now()
        );
        LegalCase created = caseService.createCase(testCase, "clerk");

        var lawyerAuth = List.of(new SimpleGrantedAuthority("ROLE_LAWYER"));
        LegalCase reviewed = caseService.transitionStatus(
                created.getId(),
                CaseStatus.UNDER_REVIEW,
                null,
                null,
                "Advocate reviewing petition",
                "lawyer",
                lawyerAuth
        );

        assertEquals(CaseStatus.UNDER_REVIEW, reviewed.getStatus());
    }

    @Test
    @DisplayName("Clerk cannot transition case from FILED to UNDER_REVIEW (AccessDenied)")
    void testClerkCannotTransitionToReview() {
        LegalCase testCase = new LegalCase(
                "TEST/2026/003",
                "ABC Corp vs. XYZ Pvt Ltd",
                CaseType.CORPORATE,
                "NCLT New Delhi",
                "ABC Corp",
                "XYZ Pvt Ltd",
                LocalDate.now()
        );
        LegalCase created = caseService.createCase(testCase, "clerk");

        var clerkAuth = List.of(new SimpleGrantedAuthority("ROLE_CLERK"));
        assertThrows(AccessDeniedException.class, () ->
            caseService.transitionStatus(
                    created.getId(),
                    CaseStatus.UNDER_REVIEW,
                    null,
                    null,
                    "Attempt by clerk",
                    "clerk",
                    clerkAuth
            )
        );
    }

    @Test
    @DisplayName("Admin can close case from HEARING_SCHEDULED; Lawyer cannot")
    void testOnlyAdminCanCloseCase() {
        LegalCase testCase = new LegalCase(
                "TEST/2026/004",
                "State vs. Accused",
                CaseType.CRIMINAL,
                "Sessions Court",
                "State",
                "Accused",
                LocalDate.now()
        );
        LegalCase created = caseService.createCase(testCase, "clerk");

        var lawyerAuth = List.of(new SimpleGrantedAuthority("ROLE_LAWYER"));
        var adminAuth = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

        // Advance to UNDER_REVIEW
        caseService.transitionStatus(created.getId(), CaseStatus.UNDER_REVIEW, null, null, "Audited", "lawyer", lawyerAuth);

        // Advance to HEARING_SCHEDULED
        caseService.transitionStatus(created.getId(), CaseStatus.HEARING_SCHEDULED, LocalDate.now().plusDays(5), "Courtroom 2", "Scheduled", "lawyer", lawyerAuth);

        // Lawyer tries to close -> should fail
        assertThrows(AccessDeniedException.class, () ->
            caseService.transitionStatus(created.getId(), CaseStatus.CLOSED, null, null, "Lawyer close attempt", "lawyer", lawyerAuth)
        );

        // Admin closes -> should succeed
        LegalCase closed = caseService.transitionStatus(created.getId(), CaseStatus.CLOSED, null, null, "Final order passed", "admin", adminAuth);
        assertEquals(CaseStatus.CLOSED, closed.getStatus());
    }
}
