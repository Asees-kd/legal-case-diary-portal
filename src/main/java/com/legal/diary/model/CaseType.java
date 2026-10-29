package com.legal.diary.model;

/**
 * Categories of legal proceedings.
 *
 * Student: Asees Kaur Dham (Roll No. 23102C0050)
 */
public enum CaseType {
    CIVIL("Civil Dispute"),
    CRIMINAL("Criminal Proceedings"),
    CONSTITUTIONAL("Constitutional Matter"),
    CORPORATE("Corporate & Commercial"),
    FAMILY("Family & Matrimonial"),
    LABOUR("Labour & Industrial");

    private final String displayName;

    CaseType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
