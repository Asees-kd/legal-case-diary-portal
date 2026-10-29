package com.legal.diary.service;

import com.legal.diary.model.CaseStatus;
import com.legal.diary.model.CaseStatusHistory;
import com.legal.diary.model.LegalCase;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface defining business logic for legal case management,
 * workflow transitions, search, and audit reporting.
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
public interface CaseService {

    List<LegalCase> getAllCases();

    Optional<LegalCase> getCaseById(Long id);

    Optional<LegalCase> getCaseByNumber(String caseNumber);

    LegalCase createCase(LegalCase legalCase, String username);

    LegalCase updateCase(Long id, LegalCase updatedCase, String username);

    LegalCase transitionStatus(Long id, CaseStatus targetStatus, LocalDate hearingDate,
                               String hearingNotes, String remarks, String username,
                               Collection<? extends GrantedAuthority> authorities);

    List<LegalCase> searchCases(String query, CaseStatus status);

    Map<CaseStatus, Long> getCaseCountsByStatus();

    List<LegalCase> getUpcomingHearings();

    List<CaseStatusHistory> getStatusHistory(Long caseId);
}
