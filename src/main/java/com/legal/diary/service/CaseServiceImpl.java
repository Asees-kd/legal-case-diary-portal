package com.legal.diary.service;

import com.legal.diary.model.CaseStatus;
import com.legal.diary.model.CaseStatusHistory;
import com.legal.diary.model.LegalCase;
import com.legal.diary.repository.CaseRepository;
import com.legal.diary.repository.CaseStatusHistoryRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Business logic implementation enforcing role-based transition state machine:
 * FILED -> UNDER_REVIEW -> HEARING_SCHEDULED -> CLOSED
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
@Service
@Transactional
public class CaseServiceImpl implements CaseService {

    private final CaseRepository caseRepository;
    private final CaseStatusHistoryRepository historyRepository;

    public CaseServiceImpl(CaseRepository caseRepository, CaseStatusHistoryRepository historyRepository) {
        this.caseRepository = caseRepository;
        this.historyRepository = historyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LegalCase> getAllCases() {
        return caseRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LegalCase> getCaseById(Long id) {
        return caseRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LegalCase> getCaseByNumber(String caseNumber) {
        return caseRepository.findByCaseNumber(caseNumber);
    }

    @Override
    public LegalCase createCase(LegalCase legalCase, String username) {
        if (caseRepository.existsByCaseNumber(legalCase.getCaseNumber())) {
            throw new IllegalArgumentException("A case with case number '" + legalCase.getCaseNumber() + "' is already registered.");
        }

        legalCase.setStatus(CaseStatus.FILED);
        legalCase.setCreatedBy(username != null ? username : "system");
        legalCase.setCreatedAt(LocalDateTime.now());
        legalCase.setUpdatedAt(LocalDateTime.now());
        if (legalCase.getFiledDate() == null) {
            legalCase.setFiledDate(LocalDate.now());
        }

        LegalCase savedCase = caseRepository.save(legalCase);

        CaseStatusHistory initialHistory = new CaseStatusHistory(
                savedCase,
                null,
                CaseStatus.FILED,
                username != null ? username : "system",
                "Case initially filed in registry"
        );
        historyRepository.save(initialHistory);

        return savedCase;
    }

    @Override
    public LegalCase updateCase(Long id, LegalCase updatedCase, String username) {
        LegalCase existing = caseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Legal case not found with ID: " + id));

        existing.setTitle(updatedCase.getTitle());
        existing.setCaseType(updatedCase.getCaseType());
        existing.setCourtName(updatedCase.getCourtName());
        existing.setPetitionerName(updatedCase.getPetitionerName());
        existing.setRespondentName(updatedCase.getRespondentName());
        if (updatedCase.getHearingDate() != null) {
            existing.setHearingDate(updatedCase.getHearingDate());
        }
        if (updatedCase.getHearingNotes() != null) {
            existing.setHearingNotes(updatedCase.getHearingNotes());
        }
        existing.setUpdatedAt(LocalDateTime.now());

        return caseRepository.save(existing);
    }

    @Override
    public LegalCase transitionStatus(Long id, CaseStatus targetStatus, LocalDate hearingDate,
                                       String hearingNotes, String remarks, String username,
                                       Collection<? extends GrantedAuthority> authorities) {
        LegalCase legalCase = caseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Legal case not found with ID: " + id));

        CaseStatus currentStatus = legalCase.getStatus();

        if (currentStatus == CaseStatus.CLOSED) {
            throw new IllegalStateException("Case is already CLOSED and cannot undergo further state transitions.");
        }

        boolean isLawyer = hasRole(authorities, "ROLE_LAWYER");
        boolean isAdmin = hasRole(authorities, "ROLE_ADMIN");

        // Validate state machine sequence and role authorizations
        if (currentStatus == CaseStatus.FILED && targetStatus == CaseStatus.UNDER_REVIEW) {
            if (!isLawyer && !isAdmin) {
                throw new AccessDeniedException("Unauthorized: Only advocates (LAWYER) or ADMIN can transition a case to UNDER REVIEW.");
            }
        } else if (currentStatus == CaseStatus.UNDER_REVIEW && targetStatus == CaseStatus.HEARING_SCHEDULED) {
            if (!isLawyer && !isAdmin) {
                throw new AccessDeniedException("Unauthorized: Only advocates (LAWYER) or ADMIN can schedule a court hearing.");
            }
            if (hearingDate == null) {
                throw new IllegalArgumentException("A valid hearing date is mandatory when scheduling a hearing.");
            }
            legalCase.setHearingDate(hearingDate);
            if (hearingNotes != null && !hearingNotes.isBlank()) {
                legalCase.setHearingNotes(hearingNotes);
            }
        } else if (currentStatus == CaseStatus.HEARING_SCHEDULED && targetStatus == CaseStatus.CLOSED) {
            if (!isAdmin) {
                throw new AccessDeniedException("Unauthorized: Exclusively Administrators (ADMIN) have authority to formally close case proceedings.");
            }
            if (remarks == null || remarks.isBlank()) {
                remarks = "Case disposed and formally closed by administrator.";
            }
        } else {
            throw new IllegalStateException("Illegal workflow transition: Cannot move from " + currentStatus + " directly to " + targetStatus + ".");
        }

        legalCase.setStatus(targetStatus);
        legalCase.setUpdatedAt(LocalDateTime.now());
        if (hearingNotes != null && !hearingNotes.isBlank()) {
            legalCase.setHearingNotes(hearingNotes);
        }

        LegalCase saved = caseRepository.save(legalCase);

        CaseStatusHistory history = new CaseStatusHistory(
                saved,
                currentStatus,
                targetStatus,
                username != null ? username : "system",
                remarks != null && !remarks.isBlank() ? remarks : "Status transitioned to " + targetStatus.getDisplayName()
        );
        historyRepository.save(history);

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LegalCase> searchCases(String query, CaseStatus status) {
        String trimmedQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        return caseRepository.searchCases(trimmedQuery, status);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<CaseStatus, Long> getCaseCountsByStatus() {
        Map<CaseStatus, Long> counts = new EnumMap<>(CaseStatus.class);
        for (CaseStatus status : CaseStatus.values()) {
            counts.put(status, caseRepository.countByStatus(status));
        }
        return counts;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LegalCase> getUpcomingHearings() {
        return caseRepository.findByHearingDateGreaterThanEqualOrderByHearingDateAsc(LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseStatusHistory> getStatusHistory(Long caseId) {
        return historyRepository.findByLegalCaseIdOrderByChangedAtAsc(caseId);
    }

    private boolean hasRole(Collection<? extends GrantedAuthority> authorities, String roleName) {
        if (authorities == null) return false;
        return authorities.stream().anyMatch(a -> a.getAuthority().equals(roleName));
    }
}
