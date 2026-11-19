## About SchoHub

SchoHub is a web-based scholarship information and matching system built for
*students who live in Batangas City, Batangas*. In the Philippines, a student
often has to read through dozens of scholarship announcements — city programs,
national government programs, school grants and private foundations — each with
different grade, income, course and residency rules, and each asking for a
different set of documents. SchoHub puts that whole list in one place and does
the filtering for the student instead of the other way around.

A student creates an account, fills in one profile (personal details, address,
school, course, GWA and family income), and the system checks that profile
against every scholarship in the catalog. The result is a dashboard of programs
the student is eligible for, with a pass/fail explanation for each requirement,
the documents to prepare, and the link to the provider's own application page.
Students who already hold a scholarship use the same account to check their
renewal requirements.

### What it does

- *Accounts and sessions* — email and password sign-up with BCrypt-hashed
  passwords, and database-backed login tokens that stay valid after the browser
  is closed, so every browser on the machine sees the same account.
- *One profile, two paths* — a single base identity form, then either
  Path A: new application or Path B: renewal, so the student only answers
  the questions that apply to them.
- *Batangas City only* — the city field offers only Batangas City and the
  barangay field lists the city's 105 official barangays (PSA PSGC). Both are
  validated again on the server, so an address outside the city cannot be saved.
- *Multi-criteria matching* — 13 eligibility rules (GWA, residency, course,
  school, household income, citizenship, academic standing, class rank, gender,
  endorsement, parents' voter registration, sibling grantee, education level)
  filter a catalog of 19 scholarship programs and rank the matches.
- *Requirements you can act on* — every match shows each requirement as met
  or not met with a short reason, the 6 core application documents, the
  program-specific documents, and the provider's application link.
- *Renewal checking* — the renewal path validates enrolled units, the previous
  term's GWA and the 4 renewal documents, and reports what is still missing.

### How it is built

The backend is *Java 17 with Spring Boot 3* (Spring Web, Spring Data JPA,
Spring Security), organized into model, repository, service, matching-rule and
web layers, with all money/grade/housing criteria expressed as small
EligibilityRule implementations. The frontend is **plain HTML, CSS and
JavaScript** served by the same application — no build step and no separate
frontend server. Data is stored in a single *H2 database file*
(data/schohub.mv.db) that lives inside the project, so the whole system runs
offline on one computer at http://localhost:8080.

### Who it is for

- Students in Batangas City looking for a scholarship they qualify for.
- Students already enrolled as scholars who need to renew.
- Guidance offices and barangay staff who want a quick way to show students
  which programs and documents apply to them.
