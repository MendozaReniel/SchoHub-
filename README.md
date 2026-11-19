# SchoHub — Java backend & web pages

REST API plus a plain HTML/CSS/JS frontend for the SchoHub scholarship information and
matching system. The backend implements the three phases of the updated flowchart:

1. **Authentication & session persistence** - email/password sign-up, BCrypt hashing,
   database-backed tokens that survive closing the browser.
2. **Student profile & branching input** - one base identity form, then either
   *Path A (new application)* or *Path B (renewal)* details.
3. **Dashboard & smart matching** - a multi-criteria engine filters the scholarship
   catalog by GWA, residency, course, school, household income and more, then returns
   the 6 standard core documents plus program-specific documents and the provider link.
   The renewal path validates enrolled units, retention GWA and the 4 renewal documents.

## Who it is for

SchoHub is for **students who have lived in Batangas City, Batangas** and are applying for,
or renewing, a scholarship while studying there. The whole catalog, the matching rules and
the address fields are built around that one city:

- **City / municipality** is a dropdown containing only *Batangas City*. A municipality
  outside the city cannot be picked, and the server rejects one with a 400 and a clear
  message if it is sent anyway.
- **Barangay** is a dropdown of the 105 official barangays of Batangas City (PSA PSGC
  `041005000`), taken from `model/BatangasCityBarangays.java`. The same list validates the
  request server-side, so an unknown barangay is refused instead of saved.
- The residency rules in `matching/rules/ResidencyRule.java` then compare that saved city
  and barangay against each program's requirement.

## Running it

This machine only had a Java 8 JRE, so a JDK 17 and Maven were unpacked into `.tools/`
(not installed system-wide). Use the launcher that points at them:

```bash
# Windows
run-backend.cmd

# Git Bash / macOS / Linux
./run-backend.sh
```

Or with your own JDK 17+ and Maven:

```bash
mvn spring-boot:run
```

The API starts on **http://localhost:8080** and anything on this machine can open it with a
plain browser — it is not tied to a particular editor, preview panel or browser profile.

### Where accounts live

Accounts, profiles, scholarships and login sessions are stored in one file inside this
project: **`data/schohub.mv.db`**. The app resolves that path from its own location at
startup (not from the directory it was launched in), so the jar, `run-backend.cmd`,
`start-offline.cmd` and an IDE all open the *same* database, and the log prints the exact
path, e.g. `SchoHub accounts, profiles and sessions are stored in C:\...\OOP SCHOHUB\data`.
Set the `SCHOHUB_DATA_DIR` environment variable to keep the database somewhere else.
Copying that one file (with the server stopped) copies every account and profile; deleting
it starts over from an empty database.

Logging in is per browser — the session token is kept by the browser that signed in — but the
token is also a row in that database file, so the same email and password work in Chrome,
Edge, Firefox or the preview panel, and the profile, matches and renewal checklist are
identical everywhere. Only the file matters, never which app opened the site.

A browser console for the tables is at http://localhost:8080/h2-console with JDBC URL
`jdbc:h2:file:<path printed at startup>/schohub`, user `sa`, empty password.
### Starting offline (no Maven, no internet)

`target/schohub-backend-1.0.0.jar` is a self-contained build: the pages, CSS and JS are
inside it, the database is a local file and no external fonts or CDNs are used. So the
site can be started straight from the jar with the bundled JDK:

```bash
# Windows
start-offline.cmd

# Git Bash / macOS / Linux
./start-offline.sh
```

Neither script touches Maven, so nothing is downloaded. Extra arguments are passed to
the application, e.g. `start-offline.cmd --server.port=9090`. If the jar is missing,
build it once while online with `run-backend.cmd` / `./run-backend.sh` (or
`mvn -DskipTests package`), then the offline scripts work from then on.

The equivalent manual command is `java -jar target/schohub-backend-1.0.0.jar`.

### Starting automatically at Windows login

`start-schohub-hidden.vbs` starts the site in the background with no console window and
writes its output to `logs\autostart.log`. A shortcut to that script lives in the Windows
Startup folder (`%APPDATA%\Microsoft\Windows\Start Menu\Programs\Startup\SchoHub.lnk`),
so http://localhost:8080 is ready a few seconds after logging in, with or without Freebuff.

Stop it with `stop-schohub.cmd`, or the **Stop SchoHub** shortcut on the Desktop (it only
stops the SchoHub java process on port 8080 and leaves anything else alone). If the site is
already running, a second launch simply fails with a "port 8080 in use" line in
`logs\autostart.log`. To start it again without logging out, run
`wscript start-schohub-hidden.vbs`. Remove the Startup shortcut to turn the auto-start off.


## Authentication

Endpoints other than sign-up, login and the public catalog expect:

```
Authorization: Bearer <token>
```

The token is returned by `/api/auth/signup` and `/api/auth/login` and expires after
30 days (`app.session.ttl-days`). Sessions are rows in the `sessions` table, so they
are not lost when the browser or server restarts.

The password field on the log-in and sign-up pages has a **Show / Hide** button, so the
password can be read back while typing without switching to another field.

## Endpoints

| Method | Path | Auth | Purpose |
| --- | --- | --- | --- |
| POST | `/api/auth/signup` | no | Create account, returns token |
| POST | `/api/auth/login` | no | Log in, returns token |
| POST | `/api/auth/logout` | token | Delete the session |
| GET | `/api/me` | yes | Current student account and profile |
| PUT | `/api/me/profile` | yes | Base student identity form |
| PUT | `/api/me/path` | yes | Choose Path A or Path B and send its inputs |
| GET | `/api/me/profile-status` | yes | Missing fields and optional fields worth adding |
| GET | `/api/me/dashboard` | yes | Everything the dashboard needs for the chosen path |
| GET | `/api/me/matches` | yes | Matched scholarships (`?includeIneligible=true` shows the rest with reasons) |
| GET | `/api/me/renewal` | yes | Renewal validation and 4-document checklist |
| GET | `/api/scholarships` | no | Catalog, filters: `?q=`, `?providerType=`, `?level=` |
| GET | `/api/scholarships/{code}` | no | One program with requirements and documents |
| GET | `/api/meta` | no | Enum options, the allowed cities (`Batangas City`), the 105 barangays, core and renewal document lists |
| GET | `/api/health` | no | Liveness check |

## Example flow (curl)

```bash
BASE=http://localhost:8080

# 1. Sign up
TOKEN=$(curl -s -X POST $BASE/api/auth/signup -H 'Content-Type: application/json' \
  -d '{"email":"maria@example.com","username":"maria","password":"Password123"}' \
  | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')

# 2. Base identity form (GWA may be a percentage or the 1.00-5.00 college scale)
curl -X PUT $BASE/api/me/profile -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{
  "fullName":"Maria Santos","birthday":"2004-05-12","educationLevel":"COLLEGE",
  "school":"Batangas State University","schoolYear":"2026-2027",
  "program":"BS Information Technology","gwaValue":1.75,"gwaScale":"COLLEGE_1_TO_5",
  "city":"Batangas City","province":"Batangas","citizenship":"Filipino","sex":"FEMALE"}'

# 3a. Path A - new application
curl -X PUT $BASE/api/me/path -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{
  "applicationPath":"NEW_APPLICATION",
  "newApplication":{"barangay":"Barangay 1","yearsOfResidency":8,"parentsRegisteredVoter":true,
  "siblingEbdGrantee":false,"annualFamilyIncome":240000,"indigencyStatus":true,
  "listahananBeneficiary":true,"hasFailingDroppedIncompleteGrades":false,
  "lowestSubjectGrade":85,"specialTrack":"STEM_IT"}}'

curl -s $BASE/api/me/dashboard -H "Authorization: Bearer $TOKEN"

# 3b. Path B - renewal
curl -X PUT $BASE/api/me/path -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{
  "applicationPath":"RENEWAL",
  "renewal":{"activeScholarshipCode":"BATSTATEU-ABOITIZ","enrolledUnits":21,
  "previousTermGwaValue":1.60,"previousTermGwaScale":"COLLEGE_1_TO_5",
  "documentsOnHand":["Certificate of Registration (COR)"]}}'

curl -s $BASE/api/me/renewal -H "Authorization: Bearer $TOKEN"
```

## How matching works

`MatchingService` runs every `EligibilityRule` bean against every scholarship. Each
rule returns `RequirementCheck`s (requirement, met/unmet, explanation). A scholarship
is a match when no blocking check fails; advisory checks such as a department
endorsement are shown but never exclude a student. The dashboard returns each check
with its reason, so the frontend can explain *why* a grant did or did not match.

Rules currently registered: education level, GWA, residency (city / province /
barangay / years), parents' voter registration, sibling grantee, household income and
indigency, academic standing (no failing grades, lowest subject grade), priority
courses, school, citizenship, sex, academic rank, department endorsement.

GWA handling: requirements are stored as percentages. Values entered on the
1.00-5.00 scale are converted with the standard equivalence table
(1.00 = 97%, 1.50 = 91%, 1.75 = 88%, 2.00 = 85%, 2.50 = 79%, 3.00 = 72%) and
interpolated in between. So "2.50 or better" and "at least 85%" compare correctly.

## Scholarship data

The catalog is seeded from `src/main/java/com/schohub/config/DataSeeder.java`:
EBD, BPEAP (+ HIM merit track), five BatStateU programs, six University of Batangas
grants, four Westmead grants (board / non-board), and two St. Bridget College grants
- 19 records in total. Westmead's board and non-board tiers are separate records
because their GWA floors differ. The seeder rebuilds the catalog on every start, so
edits belong in that file.

`applicationUrl` values point at each provider's main website because no per-program
application portal URLs were provided; replace them with the exact links when known.

## Project layout

```
src/main/java/com/schohub
  config/       Security, CORS, password encoder, scholarship seeder
  model/        Student, Scholarship, GwaThreshold, SessionToken, enums
  repository/   Spring Data repositories
  service/      Auth, profile, matching, renewal, GWA conversion, DTO mapping
  matching/     EligibilityRule interface and the 13 rule implementations
  security/     Bearer token filter
  web/          REST controllers, DTOs, error handling

src/main/resources/static
  index.html, login.html, signup.html, profile.html, dashboard.html, scholarship.html
  css/styles.css   Shared design system for every page
  js/              api.js client plus one module per page
```
