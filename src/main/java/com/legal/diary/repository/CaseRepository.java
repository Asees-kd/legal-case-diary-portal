package com.legal.diary.repository;

import com.legal.diary.model.CaseStatus;
import com.legal.diary.model.LegalCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for LegalCase entity.
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
@Repository
public interface CaseRepository extends JpaRepository<LegalCase, Long> {

    Optional<LegalCase> findByCaseNumber(String caseNumber);

    boolean existsByCaseNumber(String caseNumber);

    List<LegalCase> findByStatus(CaseStatus status);

    long countByStatus(CaseStatus status);

    @Query("SELECT c FROM LegalCase c WHERE " +
           "(:query IS NULL OR :query = '' OR " +
           " LOWER(c.caseNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(c.courtName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(c.petitionerName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(c.respondentName) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:status IS NULL OR c.status = :status) " +
           "ORDER BY c.filedDate DESC, c.id DESC")
    List<LegalCase> searchCases(@Param("query") String query, @Param("status") CaseStatus status);

    List<LegalCase> findByHearingDateGreaterThanEqualOrderByHearingDateAsc(LocalDate date);
}
