# SchoHub — Scholarship Information and Matching System for Batangas City Students

## 1. Project Title

**SchoHub: A Web-Based Scholarship Information and Matching System for Students of Batangas City**

---

## 2. Project Description

SchoHub is a web-based scholarship information and matching system built for
**students who live in Batangas City, Batangas**. In the Philippines a student has to
read through dozens of scholarship announcements — city programs, national government
programs, university grants and private foundations — each with a different grade
requirement, income ceiling, course preference, residency rule and document checklist.
SchoHub puts that whole catalog in one place and does the filtering for the student
instead of the other way around.

A student creates an account, fills in one profile (personal details, address, school,
course, GWA and family income), and the system checks that profile against every
scholarship in the catalog. The result is a dashboard of the programs the student
qualifies for, with a **passed / not passed explanation for every requirement**, the
documents to prepare, and the link to the provider's own application page. Students who
already hold a scholarship use the same account to check their renewal requirements.

The system follows three phases: **authentication and session persistence**, **student
profile with two branches (Path A new application / Path B renewal)**, and **dashboard
with multi-criteria matching and document checklists**.

The backend is Java 17 with Spring Boot 3; the frontend is plain HTML, CSS and
JavaScript served by the same application; all data is stored in one H2 database file,
so the whole system runs offline on a single computer at `http://localhost:8080`.

---

## 3. Problem Being Addressed

Students in Batangas City who want to continue studying rely on scholarships, but the
information is scattered and hard to compare:

1. **Announcements are spread out.** Scholarship posts appear on Facebook pages,
   barangay bulletin boards, school offices and group chats, so students miss programs
   they actually qualify for.
2. **Requirements are written differently for every program.** One program asks for
   "GWA 2.50 or better", another for "at least 85%", another for "no grade below 85 in
   any subject". Students cannot tell whether they pass without reading each one.
3. **Students apply to programs they are not eligible for**, which wastes their time and
   the provider's time, and sometimes makes them miss the deadline of a program they
   *were* eligible for.
4. **Document lists are unclear.** Each program asks for a different set of documents on
   top of the standard ones, and renewal has a separate checklist, so students are often
   sent home to complete papers.
5. **There is no single record of a student's scholarship status**, so renewals are
   handled by memory and paper.

SchoHub addresses this by storing the programs and their rules in one system, comparing a
student's profile against every rule, and returning a clear, explained result.

---

## 4. Project Objectives

**General objective:** To develop a web-based scholarship information and matching
system that helps students of Batangas City find the scholarships they are qualified
for and prepare the correct documents.

**Specific objectives:**

1. To provide a single, searchable catalog of the scholarship programs available to
   Batangas City students, including provider, benefits and application link.
2. To let a student create one account and one profile that can be reused for every
   application, with a persistent login session that survives closing the browser.
3. To support the two real student situations: **Path A — new application** and
   **Path B — renewal of an existing scholarship**.
4. To automatically match a student's profile (GWA, residency, barangay, course,
   school, family income, citizenship and other criteria) against every program using a
   multi-criteria matching engine, and to explain the result of each criterion.
5. To restrict the address fields to **Batangas City and its 105 official barangays**, so
   only the intended users can use the system and the residency rules stay accurate.
6. To generate the correct **document checklist** for each matched program (6 core
   application documents plus program-specific documents, and 4 renewal documents).
7. To give the student a direct link to the scholarship provider for the actual
   application or renewal.
8. To apply object-oriented programming principles — encapsulation, abstraction,
   inheritance, polymorphism and interfaces — in a maintainable Java codebase.

---

## 5. Target Users

| User | How they use SchoHub |
|---|---|
| **Students who have lived in Batangas City** (senior high and college) | Create an account, complete one profile, and see the scholarships they qualify for with the requirements and documents they need. **This is the primary user.** |
| **Existing scholars renewing a grant** | Use Path B to check enrolled units, the previous term's GWA and the four renewal documents. |
| **Guidance counselors / scholarship coordinators** | Show a student, on screen, which programs match and what to prepare, instead of reading each announcement. |
| **Barangay staff assisting students** | Use the same site to explain the city programs and the required documents. |

The system is deliberately limited to **Batangas City residents**: the city field offers
only *Batangas City*, the barangay field lists only the city's 105 official barangays,
and both are validated again on the server.

---

## 6. Main Features

### Account and session
- Sign up with email, username and password; passwords are hashed with **BCrypt** and
  never stored as plain text.
- Log in and receive a database-backed session token valid for 30 days, so the session
  survives closing the browser or restarting the server.
- **Show / Hide password** button on the login and sign-up forms, so the user can check
  what they typed.
- Automatic redirect to the login page when the session is missing or expired.

### Student profile and branching
- One **base identity form**: name, birthday, education level, school, school year,
  course, GWA (percentage *or* the 1.00–5.00 college scale), city, province,
  citizenship and sex.
- **City dropdown with only Batangas City**; **barangay dropdown with the 105 official
  Batangas City barangays**. Anything else is rejected with a clear message, both in the
  browser and on the server.
- **Path A — New Application:** barangay, years of residency, parents' voter
  registration, sibling grantee, annual family income, indigency and Listahanan status,
  failing/dropped grades, lowest subject grade and special track.
- **Path B — Renewal:** active scholarship, enrolled units, previous term GWA and the
  renewal documents already on hand.

### Matching engine
- **13 eligibility rules** run against a catalog of **19 scholarship programs**:
  education level, GWA, residency, parents' voter registration, sibling grantee, income
  and indigency, academic standing, course/strand, school, citizenship, sex, class rank
  and department endorsement.
- GWA requirements are normalised to percentages, so "2.50 or better" and "at least 85%"
  are compared correctly.
- Every criterion is returned as **met / not met with a short explanation**; advisory
  checks (e.g. a department endorsement) are shown but never block a match.
- A toggle shows the programs that did not match, together with the reason.

### Documents and provider
- 6 core application documents plus each program's specific documents, marked as
  prepared or missing.
- 4 renewal documents with the same checklist view.
- Direct link to the provider's application page for every program.

### Catalog and pages
- Landing page with live featured programs, "how it works" and partner/agency logos.
- Public program detail page (`/scholarship.html?code=EBD`) showing requirements,
  documents and provider link without logging in.
- Catalog filters by keyword, provider type and education level.

---

## 7. Technologies Used

| Layer | Technology | Purpose |
|---|---|---|
| Language | **Java 17** | Backend implementation |
| Framework | **Spring Boot 3.3.5** | Application container, REST API, auto-configuration |
| Web | **Spring Web (MVC)** | REST controllers and static page serving |
| Security | **Spring Security** + **BCrypt** | Route protection, password hashing, bearer-token authentication |
| Persistence | **Spring Data JPA / Hibernate** | Object-relational mapping and repositories |
| Database | **H2 (file mode)** | Single-file SQL database, `data/schohub.mv.db` |
| Build | **Apache Maven** | Dependency management and packaging into a runnable jar |
| Utilities | **Lombok** | Getters, setters and constructors for model classes |
| Frontend | **HTML5, CSS3, JavaScript (ES modules)** | Six pages, one shared stylesheet, one script per page |
| Frontend storage | **Browser `localStorage`** | Keeps the session token between visits |
| Tooling | Bundled **JDK 17 + Maven 3.9.9** in `.tools/`, Windows batch/VBS launchers | Runs offline without installing anything |

No external CDN, web font or npm build step is used — the site works with no internet
connection once the jar is built.

---

## 8. Team Members and Roles


| **Back-end Developer** | Mendoza, Reniel |
| **Front-end Developer** | Delos Santos, Nathaniel O. |
| **Documentation** | Magnaye, Reian |

Subject: Object-Oriented Programming
Instructor: Glenn Guinoban
School year / section: 1st sem SY 2026-2027 / IT-2114

---

## 9. OOP Concepts Demonstrated

| OOP concept | Where it is applied in SchoHub |
|---|---|
| **Encapsulation** | `Student`, `Scholarship`, `SessionToken` and `GwaThreshold` keep their fields `private` and expose controlled access through getters/setters; validation of the address and profile lives inside `ProfileService`, not in the controllers or the pages. |
| **Abstraction** | `matching/EligibilityRule.java` defines a single `evaluate(student, scholarship)` method and hides *how* each criterion is checked. Controllers never touch the database directly — they call service methods such as `MatchingService.matchesFor(...)`. |
| **Inheritance** | `web/ApiException.java` **extends** `RuntimeException`; `security/TokenAuthenticationFilter` **extends** `OncePerRequestFilter`; the repositories **extend** Spring Data's `JpaRepository`; `DataSeeder` **implements** `ApplicationRunner`. |
| **Polymorphism** | The 13 rules (`GwaRule`, `IncomeRule`, `ResidencyRule`, `CourseRule`, …) each implement `EligibilityRule`. `MatchingService` holds a `List<EligibilityRule>` and calls `evaluate(...)` on each one without knowing which concrete class it is — the correct behaviour is chosen at runtime. |
| **Interfaces** | `EligibilityRule` (one method, 13 implementations) and the repository interfaces (`StudentRepository`, `ScholarshipRepository`, `SessionTokenRepository`), whose SQL is generated by Spring Data. |
| **Composition** | A `Student` owns its renewal documents (`Set<String> renewalDocumentsOnHand`) and an `ApplicationPath`; a `Scholarship` owns its `List<GwaThreshold>` and sets of course/school keywords. Services are composed of repositories and rules through constructor injection. |
| **Enumerations** | `ApplicationPath`, `EducationLevel`, `GwaScale`, `Sex`, `ProviderType`, `ClassRank`, `SpecialTrack` replace magic strings and make invalid values impossible. |
| **Records (immutable data)** | `RequirementCheck` and the DTOs in `web/dto` are `record`s: one criterion result, one request, one response — created once and never mutated. |
| **Exception handling** | `ApiException` is thrown for bad input; `web/ApiExceptionHandler` (`@RestControllerAdvice`) converts it into a clean JSON error with the right HTTP status, so no stack trace reaches the user. |
| **Design patterns** | *Strategy* (one class per eligibility rule), *Repository* (data access behind interfaces), *DTO + Mapper* (`StudentMapper`, `ScholarshipMapper`), *Dependency Injection* (Spring constructs and wires the services). |

---

## 10. Instructions for Running the Program

### Requirements

- **JDK 17 or newer** (`java -version`) — the project is compiled for Java 17.
- **Apache Maven 3.6+** if you want to build from source.
- A modern browser (Chrome, Edge or Firefox).

### Option A — Run from source with Maven (recommended for groupmates)

```bash
git clone https://github.com/<your-username>/<your-repo>.git
cd <your-repo>
mvn spring-boot:run
```

Then open **http://localhost:8080**.

The first run downloads the dependencies from Maven Central, so it needs an internet
connection; later runs work offline.

### Option B — Run the prebuilt jar (no Maven needed)

Download `schohub-backend-1.0.0.jar` from the repository's **Releases** page, then:

```bash
java -jar schohub-backend-1.0.0.jar
```

### Option C — The launchers included in this project

```bash
# Windows — builds and runs with the bundled JDK 17 and Maven in .tools\
run-backend.cmd

# Git Bash / macOS / Linux
./run-backend.sh
```

`start-offline.cmd` / `./start-offline.sh` start the already-built jar without Maven.
**These scripts expect `.tools\jdk-17.0.21+9` to exist**, which is local to the original
developer's machine and is *not* uploaded to GitHub — on another PC use Option A or B.

### Starting automatically at Windows login (developer machine only)

`start-schohub-hidden.vbs` starts the site with no console window and writes its output
to `logs\autostart.log`; a shortcut in the Windows Startup folder runs it at log-in.
`stop-schohub.cmd` (or the **Stop SchoHub** Desktop shortcut) stops only the SchoHub
process listening on port 8080.

### First use on a new computer

1. Start the application and open **http://localhost:8080**.
2. Click **Sign up** and create an account — the database on a fresh machine is empty.
3. The 19 scholarship programs are loaded automatically at startup by
   `config/DataSeeder.java`, so the catalog is available immediately.
4. Complete the profile form, then choose **Path A (new application)** or
   **Path B (renewal)** to see the dashboard.

The application creates `data/schohub.mv.db` on first run. The exact path is printed in
the console at startup, e.g.
`SchoHub accounts, profiles and sessions are stored in C:\...\data`.
Set the `SCHOHUB_DATA_DIR` environment variable to store the database elsewhere.

### Demo account (only on the original developer's database)

`maria@example.com` / `Password123` — BatStateU BS IT, GWA 1.75, Batangas City.
A fresh clone will not have this account; register a new one instead.

---

## 11. Sample Screenshots

Screenshots of the six pages are stored in the `screenshots/` folder:

| File | Page | What it shows |
|---|---|---|
| `screenshots/01-landing.png` | Landing page | Hero, how it works, featured programs |
| `screenshots/02-login.png` | Login page | Login form with the Show/Hide password button |
| `screenshots/03-signup.png` | Sign-up page | Account form with the Show/Hide password button |
| `screenshots/04-profile.png` | Profile page | Base identity form, city and 105-barangay dropdowns |
| `screenshots/05-dashboard.png` | Dashboard | Matched programs with per-requirement explanations |
| `screenshots/06-scholarship.png` | Program detail | Requirements, document checklist and provider link |

To display them, save the images with the names above and add one line per image, e.g.:




---

## 12. Known Limitations

1. **The website has no update system for the upcoming scholars.** There is no
   announcement, notification or auto-update feature that informs students about new
   scholarship programs, newly opened application periods or deadlines. The catalog only
   changes when the developer edits `config/DataSeeder.java` and rebuilds/restarts the
   application; students must open the site and check the list themselves. There is no
   email, SMS or in-app notification, and no "what's new" section.
2. **No admin panel.** Programs, requirements and documents cannot be edited from the
   website. They are seeded from code, so a non-programmer cannot maintain the catalog.
3. **No online submission.** SchoHub only prepares the student (matching, requirements,
   checklist); the actual application and document submission happen on the provider's
   website through the provided link.
4. **Documents are not uploaded.** The checklist only records which documents the student
   has on hand; no file or image upload is stored.
5. **No password reset or email verification.** A forgotten password cannot be recovered
   from the site.
6. **Batangas City only.** The address fields intentionally reject any other city or
   municipality, and the barangay list only covers the city's 105 barangays.
7. **Notification of match changes is manual.** If a student edits their profile, the
   dashboard must be reopened to see the updated matches.
8. **Local, single-computer deployment.** The database is one H2 file, so the system is
   meant to run on one machine (or be copied with the server stopped); it is not deployed
   to a public web host.
9. **The H2 database console is enabled** at `/h2-console`, which is convenient for the
   demonstration but must be disabled or protected before any real deployment.
10. **No automated tests.** Verification during development was done manually through the
    web pages and the API.

---

## 13. Future Improvements

1. **Add an update and announcement system for upcoming scholars** — an admin-managed
   "New scholarships / opening soon" feed, with email or in-app notifications and
   deadline reminders. This directly addresses the main limitation above.
2. **Admin dashboard** for adding and editing programs, requirements and documents
   without touching the code, including open/close dates.
3. **Document upload** (PDF/image) with verification status per requirement.
4. **Application tracker** showing submitted / under review / approved per program.
5. **Password reset via email** and email verification at sign-up.
6. **Notifications for renewal deadlines** and a history of past renewals.
7. **Expand to the whole province of Batangas**, with the city/barangay lists driven by
   a database table instead of a hard-coded list.
8. **Mobile-friendly PWA** so students can check matches on a phone.
9. **Automated tests** (unit tests for the 13 rules and integration tests for the API).
10. **Deploy to a hosted server** with HTTPS and a production database (MySQL or
    PostgreSQL), and disable the H2 console.

---

## 14. Project Contributors

| Name | Role / contribution |
|---|---|
| Mendoza, Reniel | **Back-end Developer** — Java/Spring Boot API, database models, matching engine |
| Delos Santos, Nathaniel O. | **Front-end Developer** — HTML, CSS, JavaScript pages and the API client |
| Magnaye, Reian | **Documentation** — proposal, README, screenshots, presentation |
| Glenn Guinoban | Instructor / Adviser |



---

## Appendix A — Where the data is stored, and how to view or edit it

### The data file

Everything the website creates — accounts, profiles, chosen path, renewal documents and
login sessions — is stored in **one file**:

```
data/schohub.mv.db
```

The exact path is printed in the console at startup. To back up or move the system, stop
the server and copy that one file. To start over with an empty database, stop the server,
delete `data/schohub.mv.db`, and start again (the 19 programs are reloaded automatically).

The file is excluded from Git by `.gitignore` because it contains real accounts and
BCrypt password hashes.

### Database login (H2 web console)

The application ships with the H2 browser console enabled:

1. Start SchoHub, then open **http://localhost:8080/h2-console**
2. Fill in:

| Field | Value |
|---|---|
| Driver Class | `org.h2.Driver` |
| JDBC URL | `jdbc:h2:file:C:/Users/user/Desktop/OOP SCHOHUB/data/schohub` (forward slashes; the path printed at startup) |
| User Name | `sa` |
| Password | *(leave empty)* |

3. Click **Connect**. You can now browse and run SQL on the same database the website is
   using, even while the site is running (`AUTO_SERVER=TRUE`).

### Yes — you can manipulate the data

The tables are `STUDENTS`, `SCHOLARSHIPS`, `SESSIONS` and the two collection tables for
documents/keywords. Useful statements:

```sql
-- see every registered student
SELECT ID, EMAIL, USERNAME, FULL_NAME, APPLICATION_PATH, CITY, BARANGAY
FROM STUDENTS;

-- see the scholarships loaded by DataSeeder
SELECT CODE, NAME, PROVIDER, PROVIDER_TYPE, MAX_ANNUAL_INCOME
FROM SCHOLARSHIPS;

-- see active login sessions
SELECT * FROM SESSIONS;

-- correct a student's profile data (column names are snake_case)
UPDATE STUDENTS
SET ANNUAL_FAMILY_INCOME = 180000,
    BARANGAY = 'Barangay 1',
    YEARS_OF_RESIDENCY = 8
WHERE EMAIL = 'maria@example.com';

-- delete a test account
DELETE FROM STUDENTS WHERE EMAIL = 'test@example.com';

-- log everyone out
DELETE FROM SESSIONS;
```

Notes:

- `PASSWORD_HASH` is a **BCrypt hash**; you cannot type a plain password into it. To reset
  a password, delete the student row and sign up again, or replace the hash with a BCrypt
  hash generated for the new password.
- `APPLICATION_PATH`, `EDUCATION_LEVEL`, `SEX`, `GWA_SCALE` and similar columns store the
  **enum names as text** (e.g. `NEW_APPLICATION`, `COLLEGE`, `FEMALE`), not numbers.
- Edits made in the console are visible on the website immediately on the next request;
  the student may need to refresh the page or log in again after a session is deleted.
- The console is open to anyone who can reach `http://localhost:8080`, so keep it for
  demonstration and disable `spring.h2.console.enabled` before any public deployment.

---

## Project layout

```
src/main/java/com/schohub
  SchoHubApplication.java   Spring Boot entry point (also fixes the data folder path)
  config/                   Security, CORS, password encoder, scholarship seeder
  model/                    Student, Scholarship, SessionToken, GwaThreshold, enums,
                            BatangasCityBarangays (the 105-barangay master list)
  repository/               Spring Data JPA repositories
  service/                  Auth, profile, matching, renewal, GWA conversion, mappers
  matching/                 EligibilityRule interface + 13 rule implementations
  security/                 Bearer-token filter
  web/                      REST controllers, DTOs, error handling

src/main/resources
  application.properties    Port 8080, H2 file database, session TTL
  static/                   index, login, signup, profile, dashboard, scholarship pages
    css/styles.css          Shared design system
    js/                     api.js client + one module per page

data/schohub.mv.db          All accounts, profiles and sessions (not committed)
screenshots/                Sample screenshots used in section 11
```

## API reference

| Method | Path | Auth | Purpose |
| --- | --- | --- | --- |
| POST | `/api/auth/signup` | no | Create account, returns token |
| POST | `/api/auth/login` | no | Log in, returns token |
| POST | `/api/auth/logout` | token | Delete the session |
| GET | `/api/me` | yes | Current student account and profile |
| PUT | `/api/me/profile` | yes | Base student identity form |
| PUT | `/api/me/path` | yes | Choose Path A or Path B and send its inputs |
| GET | `/api/me/profile-status` | yes | Missing and optional profile fields |
| GET | `/api/me/dashboard` | yes | Everything the dashboard needs |
| GET | `/api/me/matches` | yes | Matched scholarships (`?includeIneligible=true` shows the rest) |
| GET | `/api/me/renewal` | yes | Renewal validation and 4-document checklist |
| GET | `/api/scholarships` | no | Catalog (`?q=`, `?providerType=`, `?level=`) |
| GET | `/api/scholarships/{code}` | no | One program with requirements and documents |
| GET | `/api/meta` | no | Enum options, `Batangas City`, the 105 barangays, document lists |
| GET | `/api/health` | no | Liveness check |

Other endpoints expect `Authorization: Bearer <token>`, valid for 30 days
(`app.session.ttl-days`).
