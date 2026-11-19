package com.schohub.service;

import java.util.List;

/**
 * The shared checklists from the flowchart: 6 standard core requirements for a
 * new application and 4 standard requirements for a renewal. Program-specific
 * documents live on each scholarship record.
 */
public final class DocumentCatalog {

    private DocumentCatalog() {
    }

    public static final List<String> CORE_DOCUMENTS = List.of(
            "Accomplished scholarship application form",
            "Report Card (Form 138) or Transcript of Records (TOR)",
            "Certificate of Enrollment / Registration (COR)",
            "Certificate of Indigency or proof of family income (ITR)",
            "Valid School ID",
            "Parent's / Guardian's valid ID"
    );

    public static final List<String> RENEWAL_DOCUMENTS = List.of(
            "Certificate of Registration (COR)",
            "Certificate of Grades (COG)",
            "Validated School ID",
            "Accomplished renewal form"
    );
}
