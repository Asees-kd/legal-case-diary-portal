package com.legal.diary.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Audit log recording every state transition for a legal case.
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
@Entity
@Table(name = "case_status_history")
public class CaseStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id", nullable = false)
    private LegalCase legalCase;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 30)
    private CaseStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 30)
    private CaseStatus toStatus;

    @Column(name = "changed_by", nullable = false, length = 50)
    private String changedBy;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt = LocalDateTime.now();

    @Column(length = 1000)
    private String remarks;

    public CaseStatusHistory() {}

    public CaseStatusHistory(LegalCase legalCase, CaseStatus fromStatus, CaseStatus toStatus,
                             String changedBy, String remarks) {
        this.legalCase = legalCase;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedBy = changedBy;
        this.remarks = remarks;
        this.changedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LegalCase getLegalCase() { return legalCase; }
    public void setLegalCase(LegalCase legalCase) { this.legalCase = legalCase; }

    public CaseStatus getFromStatus() { return fromStatus; }
    public void setFromStatus(CaseStatus fromStatus) { this.fromStatus = fromStatus; }

    public CaseStatus getToStatus() { return toStatus; }
    public void setToStatus(CaseStatus toStatus) { this.toStatus = toStatus; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
