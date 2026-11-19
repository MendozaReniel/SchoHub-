package com.schohub.config;

import com.schohub.model.ClassRank;
import com.schohub.model.EducationLevel;
import com.schohub.model.GwaThreshold;
import com.schohub.model.ProviderType;
import com.schohub.model.Scholarship;
import com.schohub.repository.ScholarshipRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.schohub.model.EducationLevel.COLLEGE;
import static com.schohub.model.EducationLevel.JUNIOR_HIGH;
import static com.schohub.model.EducationLevel.SENIOR_HIGH;

/**
 * Pre-populates the scholarship catalog from the compiled list of Batangas
 * programs. The catalog is editorial data, not student data, so it is rebuilt
 * from this file on every start.
 */
@Component
public class DataSeeder implements ApplicationRunner {

    private final ScholarshipRepository scholarships;

    public DataSeeder(ScholarshipRepository scholarships) {
        this.scholarships = scholarships;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // Deletes are flushed before the new rows are inserted, otherwise a
        // restart against an existing database violates the unique code index.
        scholarships.deleteAll();
        scholarships.flush();

        seedLocalGovernment();
        seedBatStateU();
        seedUniversityOfBatangas();
        seedWestmead();
        seedStBridget();
    }

    // ==================================================================
    // A. Local Government & Provincial Scholarships
    // ==================================================================

    private void seedLocalGovernment() {
        Scholarship ebd = create("EBD",
                "Batangas City Government Scholarship Program (EBD)",
                "Batangas City Government",
                ProviderType.LOCAL_GOVERNMENT,
                "City-funded tuition and miscellaneous fee assistance",
                "https://www.batangascity.gov.ph/");
        ebd.setDescription("City scholarship for high school and college students who are residents of Batangas City.");
        ebd.setLevels(levels(JUNIOR_HIGH, SENIOR_HIGH, COLLEGE));
        ebd.setGwaThresholds(gwa(level(JUNIOR_HIGH, 85), level(SENIOR_HIGH, 85), level(COLLEGE, 79)));
        ebd.setRequiredCity("Batangas City");
        ebd.setRequiredProvince("Batangas");
        ebd.setMinYearsResidency(5);
        ebd.setRequiresRegisteredVoterParents(true);
        ebd.setRequiresNoSiblingGrantee(true);
        ebd.setRetentionGwaPercent(85.0);
        ebd.setSpecificDocuments(documents(
                "Endorsement letter from the Barangay Captain",
                "Application letter addressed to the City Mayor",
                "Parent's valid ID / Voter's ID"));
        ebd.setNotes("Must be enrolled in any public or private school in Batangas City. "
                + "Only one grantee per family is allowed. Parents must be registered voters in Batangas City.");
        scholarships.save(ebd);

        Scholarship bpeap = create("BPEAP",
                "Batangas Province Educational Assistance Program (BPEAP)",
                "Provincial Government of Batangas",
                ProviderType.PROVINCIAL_GOVERNMENT,
                "Educational assistance grant per semester",
                "https://www.batangas.gov.ph/");
        bpeap.setDescription("Provincial educational assistance for senior high school and college students "
                + "of Batangas Province, including the Gov. Vilma Santos-Recto scholarship tracks.");
        bpeap.setLevels(levels(SENIOR_HIGH, COLLEGE));
        bpeap.setGwaThresholds(gwa(all(85)));
        bpeap.setRequiredProvince("Batangas");
        bpeap.setRetentionGwaPercent(85.0);
        bpeap.setSpecificDocuments(documents(
                "Certificate of Indigency from the Barangay",
                "Certificate of Enrollment / Registration",
                "Valid School ID",
                "Parent's valid ID and proof of income (Barangay Certificate of Income or ITR)"));
        bpeap.setNotes("Merit tracks such as the HIM Scholarship may require a GWA of 1.50 or better for higher grant tiers.");
        scholarships.save(bpeap);

        Scholarship merit = create("BPEAP-MERIT",
                "BPEAP Merit Track (HIM Scholarship)",
                "Provincial Government of Batangas",
                ProviderType.PROVINCIAL_GOVERNMENT,
                "Higher-tier provincial grant for top-performing students",
                "https://www.batangas.gov.ph/");
        merit.setDescription("Merit-based component of the provincial assistance program for students "
                + "with a GWA of 1.50 or better.");
        merit.setLevels(levels(SENIOR_HIGH, COLLEGE));
        merit.setGwaThresholds(gwa(all(91)));
        merit.setRequiredProvince("Batangas");
        merit.setRetentionGwaPercent(91.0);
        merit.setSpecificDocuments(documents(
                "Certificate of Indigency from the Barangay",
                "Certificate of Enrollment / Registration",
                "Valid School ID",
                "Proof of GWA (1.50 or better) for the merit track"));
        merit.setNotes("Requires a GWA of 1.50 or better (about 91%).");
        scholarships.save(merit);
    }

    // ==================================================================
    // B. University & Institutional Scholarships - Batangas State University
    // ==================================================================

    private void seedBatStateU() {
        Set<String> batStateU = keywords("Batangas State University", "BatStateU");

        Scholarship tes = create("BATSTATEU-TES",
                "Tertiary Education Subsidy (TES) - BatStateU",
                "Batangas State University",
                ProviderType.UNIVERSITY,
                "Subsidy for tuition and other school fees",
                "https://batstate-u.edu.ph/");
        tes.setDescription("TES support for officially enrolled BatStateU students from low-income families.");
        tes.setLevels(levels(COLLEGE));
        tes.setGwaThresholds(gwa(all(79)));
        tes.setRequiresListahanan(true);
        tes.setSchoolKeywords(batStateU);
        tes.setRetentionGwaPercent(79.0);
        tes.setSpecificDocuments(documents(
                "Transcript of Records (TOR)",
                "Listahanan certification (DSWD)",
                "Certificate of Indigency / ITR",
                "Certificate of Enrollment",
                "Valid School ID"));
        tes.setNotes("Requires a GWA of 2.50 or better and inclusion in Listahanan 2.0.");
        scholarships.save(tes);

        Scholarship aboitiz = create("BATSTATEU-ABOITIZ",
                "Aboitiz Scholarship Program - BatStateU",
                "Batangas State University",
                ProviderType.PRIVATE_ORGANIZATION,
                "Tuition and allowance support for priority courses",
                "https://batstate-u.edu.ph/");
        aboitiz.setDescription("Industry-sponsored grant for BatStateU students in engineering, IT, and science courses.");
        aboitiz.setLevels(levels(COLLEGE));
        aboitiz.setGwaThresholds(gwa(all(91)));
        aboitiz.setSchoolKeywords(batStateU);
        aboitiz.setCourseKeywords(keywords("Engineering", "Information Technology", "Computer Science",
                "Data Science", "Mathematics", "Biology", "Chemistry", "Physics"));
        aboitiz.setRetentionGwaPercent(91.0);
        aboitiz.setSpecificDocuments(documents(
                "Transcript of Records (TOR)",
                "Certificate of Enrollment",
                "Valid School ID",
                "Program-specific endorsement form"));
        aboitiz.setNotes("Requires a GWA of 1.50 or better.");
        scholarships.save(aboitiz);

        Scholarship malampaya = create("BATSTATEU-MALAMPAYA",
                "Malampaya Sustainable Development Scholarship - BatStateU",
                "Batangas State University",
                ProviderType.PRIVATE_ORGANIZATION,
                "Grant for sustainable development-related programs",
                "https://batstate-u.edu.ph/");
        malampaya.setDescription("Grant for BatStateU students in sustainable development-related courses "
                + "with good academic standing.");
        malampaya.setLevels(levels(COLLEGE));
        malampaya.setGwaThresholds(gwa(all(85)));
        malampaya.setSchoolKeywords(batStateU);
        malampaya.setCourseKeywords(keywords("Environmental", "Sustainable", "Development",
                "Marine", "Biology", "Ecology"));
        malampaya.setRequiresDepartmentEndorsement(true);
        malampaya.setRequiresNoFailingGrades(true);
        malampaya.setRetentionGwaPercent(85.0);
        malampaya.setSpecificDocuments(documents(
                "Transcript of Records (TOR)",
                "Certificate of Enrollment",
                "Department endorsement form"));
        malampaya.setNotes("Requires good academic standing (GWA of 2.00 or better) and a department endorsement.");
        scholarships.save(malampaya);

        Scholarship ched = create("BATSTATEU-CHED",
                "CHED-Sponsored Grants (ESTATISTIKOLAR, CoScho, SIScholar, EASE, AHEAD)",
                "Commission on Higher Education / BatStateU",
                ProviderType.UNIVERSITY,
                "CHED scholarship grant per program guidelines",
                "https://ched.gov.ph/");
        ched.setDescription("CHED-sponsored scholarship programs administered through BatStateU for priority courses.");
        ched.setLevels(levels(COLLEGE));
        ched.setGwaThresholds(gwa(all(85)));
        ched.setSchoolKeywords(batStateU);
        ched.setRequiresPhilippineCitizenship(true);
        ched.setCourseKeywords(keywords("Engineering", "Information Technology", "Computer Science",
                "Mathematics", "Statistics", "Biology", "Chemistry", "Physics", "Education", "Nursing",
                "Agriculture"));
        ched.setRetentionGwaPercent(85.0);
        ched.setSpecificDocuments(documents(
                "Transcript of Records (TOR)",
                "Certificate of Enrollment",
                "Valid School ID",
                "Certificate of Indigency / ITR",
                "Program-specific endorsement form"));
        ched.setNotes("Generally requires a GWA of 2.00 or better, Philippine citizenship, "
                + "and enrollment in a CHED priority course.");
        scholarships.save(ched);
    }

    // ==================================================================
    // B. University & Institutional Scholarships - University of Batangas
    // ==================================================================

    private void seedUniversityOfBatangas() {
        Set<String> ub = keywords("University of Batangas");
        Set<String> priorityCourses = keywords("Engineering", "Information Technology", "Computer Science",
                "Education", "Nursing", "Accountancy", "Agriculture", "Mathematics");

        Scholarship valedictorian = create("UB-VALEDICTORIAN",
                "University of Batangas Academic Scholarship - Valedictorian",
                "University of Batangas",
                ProviderType.UNIVERSITY,
                "100% tuition discount",
                "https://ub.edu.ph/");
        valedictorian.setDescription("Entrance academic scholarship for incoming UB students who graduated valedictorian.");
        valedictorian.setLevels(levels(COLLEGE));
        valedictorian.setSchoolKeywords(ub);
        valedictorian.setRequiredClassRank(ClassRank.VALEDICTORIAN);
        valedictorian.setSpecificDocuments(documents(
                "Report Card / TOR",
                "Rank Certificate (for academic scholarships)",
                "Certificate of Enrollment",
                "Valid ID"));
        valedictorian.setNotes("Requires Rank 1 / GWA 1.00.");
        scholarships.save(valedictorian);

        Scholarship salutatorian = create("UB-SALUTATORIAN",
                "University of Batangas Academic Scholarship - Salutatorian",
                "University of Batangas",
                ProviderType.UNIVERSITY,
                "50% tuition discount",
                "https://ub.edu.ph/");
        salutatorian.setDescription("Entrance academic scholarship for incoming UB students who graduated salutatorian.");
        salutatorian.setLevels(levels(COLLEGE));
        salutatorian.setSchoolKeywords(ub);
        salutatorian.setRequiredClassRank(ClassRank.SALUTATORIAN);
        salutatorian.setSpecificDocuments(documents(
                "Report Card / TOR",
                "Rank Certificate (for academic scholarships)",
                "Certificate of Enrollment",
                "Valid ID"));
        salutatorian.setNotes("Requires Rank 2 / GWA of about 1.00-1.25.");
        scholarships.save(salutatorian);

        Scholarship honorable = create("UB-FIRST-HONORABLE-MENTION",
                "University of Batangas Academic Scholarship - First Honorable Mention",
                "University of Batangas",
                ProviderType.UNIVERSITY,
                "25% tuition discount",
                "https://ub.edu.ph/");
        honorable.setDescription("Entrance academic scholarship for incoming UB students "
                + "who graduated with First Honorable Mention.");
        honorable.setLevels(levels(COLLEGE));
        honorable.setSchoolKeywords(ub);
        honorable.setRequiredClassRank(ClassRank.FIRST_HONORABLE_MENTION);
        honorable.setSpecificDocuments(documents(
                "Report Card / TOR",
                "Rank Certificate (for academic scholarships)",
                "Certificate of Enrollment",
                "Valid ID"));
        honorable.setNotes("Requires a GWA of 1.25-1.50.");
        scholarships.save(honorable);

        Scholarship tdp = create("UB-TULONG-DUNONG",
                "CHED Tulong Dunong Program (TDP) & TES - University of Batangas",
                "University of Batangas",
                ProviderType.UNIVERSITY,
                "Tuition subsidy under CHED Tulong Dunong / TES",
                "https://ub.edu.ph/");
        tdp.setDescription("CHED-funded assistance for UB students from low-income families in priority courses.");
        tdp.setLevels(levels(COLLEGE));
        tdp.setGwaThresholds(gwa(all(85)));
        tdp.setSchoolKeywords(ub);
        tdp.setMaxAnnualIncome(new java.math.BigDecimal("300000"));
        tdp.setCourseKeywords(priorityCourses);
        tdp.setRetentionGwaPercent(85.0);
        tdp.setSpecificDocuments(documents(
                "Report Card / TOR",
                "Certificate of Indigency / ITR",
                "Certificate of Enrollment",
                "Valid ID"));
        tdp.setNotes("Requires a GWA of 2.00 or better and annual family income below the CHED threshold "
                + "(usually PHP 300,000).");
        scholarships.save(tdp);

        Scholarship ubBpeap = create("UB-BPEAP",
                "Gov. Vilma Santos-Recto Educational Assistance - University of Batangas",
                "University of Batangas",
                ProviderType.UNIVERSITY,
                "Provincial educational assistance processed through UB",
                "https://ub.edu.ph/");
        ubBpeap.setDescription("Provincial assistance program for UB students, processed by the university's "
                + "scholarship office with the same requirements as BPEAP.");
        ubBpeap.setLevels(levels(COLLEGE));
        ubBpeap.setGwaThresholds(gwa(all(85)));
        ubBpeap.setSchoolKeywords(ub);
        ubBpeap.setRequiredProvince("Batangas");
        ubBpeap.setRetentionGwaPercent(85.0);
        ubBpeap.setSpecificDocuments(documents(
                "Certificate of Indigency from the Barangay",
                "Certificate of Enrollment",
                "Valid School ID",
                "Parent's valid ID and proof of income"));
        ubBpeap.setNotes("Same requirements as BPEAP, but filed through the UB scholarship office.");
        scholarships.save(ubBpeap);

        Scholarship soroptimist = create("UB-SOROPTIMIST",
                "Soroptimist International Grants - University of Batangas",
                "Soroptimist International / University of Batangas",
                ProviderType.PRIVATE_ORGANIZATION,
                "Tuition grant for female students in need",
                "https://ub.edu.ph/");
        soroptimist.setDescription("Grant for female UB students who demonstrate financial need "
                + "and good academic standing.");
        soroptimist.setLevels(levels(COLLEGE));
        soroptimist.setGwaThresholds(gwa(all(85)));
        soroptimist.setSchoolKeywords(ub);
        soroptimist.setFemaleOnly(true);
        soroptimist.setRequiresIndigency(true);
        soroptimist.setRequiresNoFailingGrades(true);
        soroptimist.setRetentionGwaPercent(85.0);
        soroptimist.setSpecificDocuments(documents(
                "Report Card / TOR",
                "Certificate of Indigency / ITR",
                "Certificate of Enrollment",
                "Valid ID"));
        soroptimist.setNotes("Requires good academic standing (GWA of 2.00 or better) and proven financial need.");
        scholarships.save(soroptimist);
    }

    // ==================================================================
    // B. University & Institutional Scholarships - Westmead International School
    // ==================================================================

    private void seedWestmead() {
        Set<String> westmead = keywords("Westmead International School", "Westmead");
        List<String> documents = documents(
                "SHS Report Card (Form 138) showing GWA and individual grades",
                "Certificate of Good Moral Character",
                "Enrollment Form");

        Scholarship wastfiBoard = create("WESTMEAD-WASTFI-BOARD",
                "WASTFI EDC Scholarship - Westmead (Board Programs)",
                "Westmead International School",
                ProviderType.UNIVERSITY,
                "100% miscellaneous fees and 80% tuition",
                "https://www.westmead.edu.ph/");
        wastfiBoard.setDescription("Grant for incoming or current Westmead students in board programs.");
        wastfiBoard.setLevels(levels(COLLEGE));
        wastfiBoard.setGwaThresholds(gwa(all(85)));
        wastfiBoard.setMinLowestSubjectGrade(83.0);
        wastfiBoard.setSchoolKeywords(westmead);
        wastfiBoard.setRetentionGwaPercent(85.0);
        wastfiBoard.setSpecificDocuments(new ArrayList<>(documents));
        wastfiBoard.setNotes("Requires SHS GWA of at least 85% with no grade lower than 83%. "
                + "Retention: maintain 85% GWA.");
        scholarships.save(wastfiBoard);

        Scholarship wastfiNonBoard = create("WESTMEAD-WASTFI-NONBOARD",
                "WASTFI EDC Scholarship - Westmead (Non-Board Programs)",
                "Westmead International School",
                ProviderType.UNIVERSITY,
                "100% miscellaneous fees and 80% tuition",
                "https://www.westmead.edu.ph/");
        wastfiNonBoard.setDescription("Grant for incoming or current Westmead students in non-board programs.");
        wastfiNonBoard.setLevels(levels(COLLEGE));
        wastfiNonBoard.setGwaThresholds(gwa(all(80)));
        wastfiNonBoard.setMinLowestSubjectGrade(83.0);
        wastfiNonBoard.setSchoolKeywords(westmead);
        wastfiNonBoard.setRetentionGwaPercent(83.0);
        wastfiNonBoard.setSpecificDocuments(new ArrayList<>(documents));
        wastfiNonBoard.setNotes("Requires SHS GWA of at least 80% with no grade lower than 83%. "
                + "Retention: maintain 83% GWA.");
        scholarships.save(wastfiNonBoard);

        Scholarship gawadBoard = create("WESTMEAD-GAWAD-BOARD",
                "Gawad Kabataan Scholarship - Westmead (Board Programs)",
                "Westmead International School",
                ProviderType.UNIVERSITY,
                "100% tuition and 50% miscellaneous fees",
                "https://www.westmead.edu.ph/");
        gawadBoard.setDescription("Merit grant for incoming or current Westmead students in board programs.");
        gawadBoard.setLevels(levels(COLLEGE));
        gawadBoard.setGwaThresholds(gwa(all(91)));
        gawadBoard.setMinLowestSubjectGrade(85.0);
        gawadBoard.setSchoolKeywords(westmead);
        gawadBoard.setRetentionGwaPercent(85.0);
        gawadBoard.setSpecificDocuments(new ArrayList<>(documents));
        gawadBoard.setNotes("Requires SHS GWA of at least 91% with no grade lower than 85%. "
                + "Retention: maintain 85% GWA.");
        scholarships.save(gawadBoard);

        Scholarship gawadNonBoard = create("WESTMEAD-GAWAD-NONBOARD",
                "Gawad Kabataan Scholarship - Westmead (Non-Board Programs)",
                "Westmead International School",
                ProviderType.UNIVERSITY,
                "100% tuition and 50% miscellaneous fees",
                "https://www.westmead.edu.ph/");
        gawadNonBoard.setDescription("Merit grant for incoming or current Westmead students in non-board programs.");
        gawadNonBoard.setLevels(levels(COLLEGE));
        gawadNonBoard.setGwaThresholds(gwa(all(88)));
        gawadNonBoard.setMinLowestSubjectGrade(85.0);
        gawadNonBoard.setSchoolKeywords(westmead);
        gawadNonBoard.setRetentionGwaPercent(83.0);
        gawadNonBoard.setSpecificDocuments(new ArrayList<>(documents));
        gawadNonBoard.setNotes("Requires SHS GWA of at least 88% with no grade lower than 85%. "
                + "Retention: maintain 83% GWA.");
        scholarships.save(gawadNonBoard);
    }

    // ==================================================================
    // B. University & Institutional Scholarships - St. Bridget College
    // ==================================================================

    private void seedStBridget() {
        Set<String> stBridget = keywords("St. Bridget College", "St Bridget College", "St. Bridget");

        Scholarship academic = create("STBRIDGET-ACADEMIC",
                "St. Bridget College Academic Scholarship",
                "St. Bridget College Batangas City",
                ProviderType.UNIVERSITY,
                "Partial to full tuition discount depending on the grant tier",
                "https://www.stbridgetcollege.edu.ph/");
        academic.setDescription("Academic scholarship for incoming or currently enrolled St. Bridget College students.");
        academic.setLevels(levels(COLLEGE));
        academic.setGwaThresholds(gwa(all(91)));
        academic.setSchoolKeywords(stBridget);
        academic.setRetentionGwaPercent(91.0);
        academic.setSpecificDocuments(documents(
                "Report Card / TOR",
                "Certificate of Enrollment",
                "Valid ID"));
        academic.setNotes("Typically requires a GWA of 1.50 to 1.75 or better, depending on the grant tier.");
        scholarships.save(academic);

        Scholarship sap = create("STBRIDGET-SAP",
                "Student Assistance Program (SAP / Kabahagi Scholarship Fund)",
                "St. Bridget College Batangas City",
                ProviderType.UNIVERSITY,
                "Need-based financial assistance",
                "https://www.stbridgetcollege.edu.ph/");
        sap.setDescription("Need-based assistance for currently enrolled St. Bridget College students.");
        sap.setLevels(levels(COLLEGE));
        sap.setGwaThresholds(gwa(all(85)));
        sap.setSchoolKeywords(stBridget);
        sap.setRequiresIndigency(true);
        sap.setRetentionGwaPercent(85.0);
        sap.setSpecificDocuments(documents(
                "Report Card / TOR",
                "Certificate of Indigency",
                "Parent's ITR or Barangay Certificate of Income",
                "Application Form"));
        sap.setNotes("Need-based: requires proof of financial hardship and a satisfactory GWA "
                + "(usually 2.00 or better).");
        scholarships.save(sap);
    }

    // ==================================================================
    // Helpers
    // ==================================================================

    private static Scholarship create(String code,
                                      String name,
                                      String provider,
                                      ProviderType providerType,
                                      String benefit,
                                      String applicationUrl) {
        Scholarship scholarship = new Scholarship();
        scholarship.setId(UUID.randomUUID().toString());
        scholarship.setCode(code);
        scholarship.setName(name);
        scholarship.setProvider(provider);
        scholarship.setProviderType(providerType);
        scholarship.setBenefit(benefit);
        scholarship.setApplicationUrl(applicationUrl);
        scholarship.setRenewalEnabled(true);
        scholarship.setMinUnitsForRenewal(15);
        return scholarship;
    }

    private static Set<EducationLevel> levels(EducationLevel... levels) {
        return new LinkedHashSet<>(List.of(levels));
    }

    private static List<GwaThreshold> gwa(GwaThreshold... thresholds) {
        return new ArrayList<>(List.of(thresholds));
    }

    private static GwaThreshold all(double minPercent) {
        return new GwaThreshold(null, minPercent);
    }

    private static GwaThreshold level(EducationLevel level, double minPercent) {
        return new GwaThreshold(level, minPercent);
    }

    private static Set<String> keywords(String... keywords) {
        return new LinkedHashSet<>(List.of(keywords));
    }

    private static List<String> documents(String... documents) {
        return new ArrayList<>(List.of(documents));
    }
}
