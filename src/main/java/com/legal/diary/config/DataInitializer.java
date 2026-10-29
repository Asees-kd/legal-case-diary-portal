package com.legal.diary.config;

import com.legal.diary.model.CaseStatus;
import com.legal.diary.model.CaseStatusHistory;
import com.legal.diary.model.CaseType;
import com.legal.diary.model.LegalCase;
import com.legal.diary.repository.CaseRepository;
import com.legal.diary.repository.CaseStatusHistoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Seeds 6 realistic legal case records with full status audit trails
 * if the persistent repository is empty upon startup.
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final CaseRepository caseRepository;
    private final CaseStatusHistoryRepository historyRepository;

    public DataInitializer(CaseRepository caseRepository, CaseStatusHistoryRepository historyRepository) {
        this.caseRepository = caseRepository;
        this.historyRepository = historyRepository;
    }

    @Override
    public void run(String... args) {
        if (caseRepository.count() > 0) {
            return;
        }

        // Case 1: FILED - Civil dispute
        LegalCase case1 = new LegalCase(
                "DL/HC/2026/0114",
                "Mehta Infrastructure Ltd. vs. Union of India",
                CaseType.CIVIL,
                "High Court of Delhi",
                "Mehta Infrastructure Ltd.",
                "Union of India & CPWD",
                LocalDate.now().minusDays(3)
        );
        case1.setCreatedBy("clerk");
        caseRepository.save(case1);
        historyRepository.save(new CaseStatusHistory(case1, null, CaseStatus.FILED, "clerk", "Petition registered through registry desk"));

        // Case 2: FILED - Criminal proceedings
        LegalCase case2 = new LegalCase(
                "MH/CC/2026/0389",
                "State of Maharashtra vs. R. K. Singhania",
                CaseType.CRIMINAL,
                "Sessions Court, Mumbai",
                "State of Maharashtra (CBI Special Cell)",
                "R. K. Singhania & Ors.",
                LocalDate.now().minusDays(1)
        );
        case2.setCreatedBy("clerk");
        caseRepository.save(case2);
        historyRepository.save(new CaseStatusHistory(case2, null, CaseStatus.FILED, "clerk", "Charge sheet submitted and registered"));

        // Case 3: UNDER_REVIEW - Corporate matter
        LegalCase case3 = new LegalCase(
                "KA/CA/2026/0512",
                "Apex Cloud Solutions Ltd. vs. Commissioner of Income Tax",
                CaseType.CORPORATE,
                "High Court of Karnataka, Bengaluru",
                "Apex Cloud Solutions Ltd.",
                "Commissioner of Income Tax (Appeals)",
                LocalDate.now().minusDays(18)
        );
        case3.setStatus(CaseStatus.UNDER_REVIEW);
        case3.setCreatedBy("clerk");
        case3.setHearingNotes("Audit of transfer pricing documents and appellate tribunal records in progress.");
        caseRepository.save(case3);
        historyRepository.save(new CaseStatusHistory(case3, null, CaseStatus.FILED, "clerk", "Filing completed"));
        historyRepository.save(new CaseStatusHistory(case3, CaseStatus.FILED, CaseStatus.UNDER_REVIEW, "lawyer", "Senior counsel reviewing written submissions and precedent citations"));

        // Case 4: HEARING_SCHEDULED - Constitutional writ
        LegalCase case4 = new LegalCase(
                "SC/WP/2026/0045",
                "Citizens for Clean Air Foundation vs. Ministry of Environment",
                CaseType.CONSTITUTIONAL,
                "Supreme Court of India",
                "Citizens for Clean Air Foundation",
                "Ministry of Environment, Forest and Climate Change",
                LocalDate.now().minusDays(25)
        );
        case4.setStatus(CaseStatus.HEARING_SCHEDULED);
        case4.setCreatedBy("clerk");
        case4.setHearingDate(LocalDate.now().plusDays(6));
        case4.setHearingNotes("Courtroom 3, Bench: Hon'ble Chief Justice. Focus on compliance affidavit regarding stubble burning.");
        caseRepository.save(case4);
        historyRepository.save(new CaseStatusHistory(case4, null, CaseStatus.FILED, "clerk", "Public interest litigation filed"));
        historyRepository.save(new CaseStatusHistory(case4, CaseStatus.FILED, CaseStatus.UNDER_REVIEW, "lawyer", "Advocate audited PIL petition and index"));
        historyRepository.save(new CaseStatusHistory(case4, CaseStatus.UNDER_REVIEW, CaseStatus.HEARING_SCHEDULED, "lawyer", "Matter listed before Bench for preliminary admission hearing"));

        // Case 5: HEARING_SCHEDULED - Family matter
        LegalCase case5 = new LegalCase(
                "DL/FC/2026/0208",
                "Kaur vs. Singh & Anr.",
                CaseType.FAMILY,
                "Family Court, Saket, New Delhi",
                "Harleen Kaur",
                "Simranjit Singh & Anr.",
                LocalDate.now().minusDays(12)
        );
        case5.setStatus(CaseStatus.HEARING_SCHEDULED);
        case5.setCreatedBy("clerk");
        case5.setHearingDate(LocalDate.now().plusDays(11));
        case5.setHearingNotes("Mediation report awaited; listing before Principal Judge for custodial arrangement arguments.");
        caseRepository.save(case5);
        historyRepository.save(new CaseStatusHistory(case5, null, CaseStatus.FILED, "clerk", "Petition admitted"));
        historyRepository.save(new CaseStatusHistory(case5, CaseStatus.FILED, CaseStatus.UNDER_REVIEW, "lawyer", "Pleadings scrutinized"));
        historyRepository.save(new CaseStatusHistory(case5, CaseStatus.UNDER_REVIEW, CaseStatus.HEARING_SCHEDULED, "lawyer", "Hearing fixed post mediation cell report submission"));

        // Case 6: CLOSED - Labour dispute
        LegalCase case6 = new LegalCase(
                "TN/LC/2025/1190",
                "Chennai Dockworkers Union vs. Port Authority",
                CaseType.LABOUR,
                "High Court of Madras",
                "Chennai Dockworkers Union",
                "Board of Trustees, Chennai Port Authority",
                LocalDate.now().minusDays(60)
        );
        case6.setStatus(CaseStatus.CLOSED);
        case6.setCreatedBy("clerk");
        case6.setHearingDate(LocalDate.now().minusDays(10));
        case6.setHearingNotes("Final judgment delivered on wage revision settlement. Settlement agreement marked as Exhibit A.");
        caseRepository.save(case6);
        historyRepository.save(new CaseStatusHistory(case6, null, CaseStatus.FILED, "clerk", "Dispute petition registered"));
        historyRepository.save(new CaseStatusHistory(case6, CaseStatus.FILED, CaseStatus.UNDER_REVIEW, "lawyer", "Collective bargaining agreement analyzed"));
        historyRepository.save(new CaseStatusHistory(case6, CaseStatus.UNDER_REVIEW, CaseStatus.HEARING_SCHEDULED, "lawyer", "Final arguments heard"));
        historyRepository.save(new CaseStatusHistory(case6, CaseStatus.HEARING_SCHEDULED, CaseStatus.CLOSED, "admin", "Consent decree issued by Court; matter resolved and archived"));
    }
}
