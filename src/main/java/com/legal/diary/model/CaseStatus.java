package com.legal.diary.model;

/**
 * Legal case workflow lifecycle states.
 * Allowed flow: FILED -> UNDER_REVIEW -> HEARING_SCHEDULED -> CLOSED
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
public enum CaseStatus {
    FILED("Filed", "status-filed"),
    UNDER_REVIEW("Under Review", "status-review"),
    HEARING_SCHEDULED("Hearing Scheduled", "status-hearing"),
    CLOSED("Closed", "status-closed");

    private final String displayName;
    private final String badgeClass;

    CaseStatus(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
