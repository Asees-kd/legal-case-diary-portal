package com.legal.diary.repository;

import com.legal.diary.model.CaseStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for CaseStatusHistory audit logs.
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
@Repository
public interface CaseStatusHistoryRepository extends JpaRepository<CaseStatusHistory, Long> {

    List<CaseStatusHistory> findByLegalCaseIdOrderByChangedAtAsc(Long caseId);
}
