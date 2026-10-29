package com.legal.diary.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Primary Legal Case entity representing registered court matters.
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
@Entity
@Table(name = "legal_cases", uniqueConstraints = {
    @UniqueConstraint(name = "uk_case_number", columnNames = "case_number")
})
public class LegalCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Case number is required")
    @Size(max = 50, message = "Case number cannot exceed 50 characters")
    @Column(name = "case_number", nullable = false, length = 50, unique = true)
    private String caseNumber;

    @NotBlank(message = "Case title is required")
    @Size(max = 200, message = "Case title cannot exceed 200 characters")
    @Column(nullable = false, length = 200)
    private String title;

    @NotNull(message = "Case category is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "case_type", nullable = false, length = 30)
    private CaseType caseType;

    @NotBlank(message = "Court name is required")
    @Size(max = 150, message = "Court name cannot exceed 150 characters")
    @Column(name = "court_name", nullable = false, length = 150)
    private String courtName;

    @NotBlank(message = "Petitioner name is required")
    @Size(max = 120, message = "Petitioner name cannot exceed 120 characters")
    @Column(name = "petitioner_name", nullable = false, length = 120)
    private String petitionerName;

    @NotBlank(message = "Respondent name is required")
    @Size(max = 120, message = "Respondent name cannot exceed 120 characters")
    @Column(name = "respondent_name", nullable = false, length = 120)
    private String respondentName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CaseStatus status = CaseStatus.FILED;

    @NotNull(message = "Filing date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "filed_date", nullable = false)
    private LocalDate filedDate = LocalDate.now();

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "hearing_date")
    private LocalDate hearingDate;

    @Size(max = 2000, message = "Hearing notes cannot exceed 2000 characters")
    @Column(name = "hearing_notes", length = 2000)
    private String hearingNotes;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "legalCase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("changedAt DESC")
    private List<CaseStatusHistory> statusHistories = new ArrayList<>();

    public LegalCase() {}

    public LegalCase(String caseNumber, String title, CaseType caseType, String courtName,
                     String petitionerName, String respondentName, LocalDate filedDate) {
        this.caseNumber = caseNumber;
        this.title = title;
        this.caseType = caseType;
        this.courtName = courtName;
        this.petitionerName = petitionerName;
        this.respondentName = respondentName;
        this.filedDate = filedDate != null ? filedDate : LocalDate.now();
        this.status = CaseStatus.FILED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = CaseStatus.FILED;
        }
        if (this.filedDate == null) {
            this.filedDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addStatusHistory(CaseStatusHistory history) {
        statusHistories.add(history);
        history.setLegalCase(this);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public CaseType getCaseType() { return caseType; }
    public void setCaseType(CaseType caseType) { this.caseType = caseType; }

    public String getCourtName() { return courtName; }
    public void setCourtName(String courtName) { this.courtName = courtName; }

    public String getPetitionerName() { return petitionerName; }
    public void setPetitionerName(String petitionerName) { this.petitionerName = petitionerName; }

    public String getRespondentName() { return respondentName; }
    public void setRespondentName(String respondentName) { this.respondentName = respondentName; }

    public CaseStatus getStatus() { return status; }
    public void setStatus(CaseStatus status) { this.status = status; }

    public LocalDate getFiledDate() { return filedDate; }
    public void setFiledDate(LocalDate filedDate) { this.filedDate = filedDate; }

    public LocalDate getHearingDate() { return hearingDate; }
    public void setHearingDate(LocalDate hearingDate) { this.hearingDate = hearingDate; }

    public String getHearingNotes() { return hearingNotes; }
    public void setHearingNotes(String hearingNotes) { this.hearingNotes = hearingNotes; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<CaseStatusHistory> getStatusHistories() { return statusHistories; }
    public void setStatusHistories(List<CaseStatusHistory> statusHistories) { this.statusHistories = statusHistories; }
}
