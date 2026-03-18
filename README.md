# Waste Management System (Nigeria) - Residential Waste Management & Billing Platform

## Overview (Phase 1)
This repository contains a Phase 1 implementation of a Nigeria Residential Waste Management & Billing Platform:
- Verified Nigerian address onboarding (State -> LGA -> Street + House Number + Landmark; no postcodes)
- Address-based monthly schedule calendar with holiday week-shift rules
- Resident view: "Has my waste been picked up today?" and "When is the next pickup?"
- Government finance unpaid dashboard by LGA (real-time updates)
- Fleet tools (vehicles) and route supervisor marking (picked/missed/reported)
- Issue reporting with photo upload + in-app message thread
- PDF invoice rendering
- PWA build with offline-friendly caching for low-connectivity areas

Docs: `docs/PHASE1_SCOPE.md`, `docs/WIREFRAMES.md`, `docs/SPRINT_PLAN.md`, `docs/JIRA_BACKLOG_PHASE1.csv`.

## What Is Implemented vs Planned

### Implemented (Phase 1)
- OTP login (dev OTP returned in API response)
- JWT auth + role-based access (RESIDENT, FINANCE_OFFICER, FLEET_MANAGER, ROUTE_SUPERVISOR, SUPER_ADMIN)
- Schedule calendar by LGA + public holiday week shift (+1 day for that week)
- Collection status recording and real-time resident updates (WebSocket STOMP)
- Monthly invoices + unpaid dashboard with real-time reconciliation updates
- Issue reporting + photo upload + message thread per issue
- PDF invoice generation (OpenPDF)
- PWA build with API runtime caching

### Planned / Provider Wiring (Phase 2)
- Payments via Paystack/Flutterwave/Remita (Phase 1 uses a stub webhook to simulate reconciliation)
- Push/SMS/Email notification provider integrations
- Multi-language UI (English-only in Phase 1)

## Technology Stack
- Frontend: React + Vite + PWA (Workbox runtime caching)
- Backend: Java 21 + Spring Boot 3 (Web, Security, JPA, WebSocket STOMP)
- Database: MySQL in dev/prod; H2 in-memory for tests
- Real-time: WebSocket endpoint `/ws` and topics under `/topic/...`

## Project Structure
```
WMS-WasteManagementSystem/
|-- frontend/          # React application (PWA)
|-- backend/           # Spring Boot application
|-- database/          # MySQL schema (baseline/reference)
|-- docs/              # Scope + wireframes + sprint plan + backlog
`-- .github/           # GitHub workflows and instructions
```

## Getting Started

### Prerequisites
- Node.js (v18+)
- Java (v21+)
- MySQL (v8+)
- Maven

### Database
- The backend is configured for MySQL by default in `backend/src/main/resources/application.properties`.
- A baseline schema is available at `database/schema.sql` (useful for provisioning/reference). The app also uses JPA schema management for local dev.

Example:
```bash
mysql -u root -p < database/schema.sql
```

### Backend
```bash
cd backend
mvn -s maven-settings.xml spring-boot:run
```

Notes:
- `backend/maven-settings.xml` pins Maven's local repo to `backend/.m2/repository` (helpful in restricted/sandboxed environments).
- Demo seeded users exist for local dev: `finance@demo.ng`, `superadmin@demo.ng` (login via OTP).
- Dev OTP is returned in the response by default (`app.otp.return-code=true`). Disable in production.

### Frontend
```bash
cd frontend
npm install
npm run dev
```

PowerShell note (Windows):
- If `npm` is blocked by execution policy, run via `cmd`:
  - `cmd /c "npm install"`
  - `cmd /c "npm run dev"`

## Running with Docker

### Prerequisites
- Docker and Docker Compose installed.

### Build and Run
```bash
docker-compose up --build
```

This will start:
- Database (MySQL) on port 3306
- Backend (Spring Boot) on port 8080
- Frontend (Nginx) on port 80

Access the application at `http://localhost`.

## Running with Vagrant

### Prerequisites
- Vagrant and VirtualBox installed.

### Setup
```bash
vagrant up
```

This will:
- Provision an Ubuntu VM with Docker, Docker Compose, and Git.
- Clone the repository from GitHub into the VM.
- Build and run the containers inside the VM.
- Forward ports: Frontend (VM:80 -> Host:8080), Backend (VM:8080 -> Host:8081), Database (VM:3306 -> Host:3307)

Access the application at `http://localhost:8080`.

To SSH into the VM:
```bash
vagrant ssh
```

To stop:
```bash
vagrant halt
```

To destroy the VM:
```bash
vagrant destroy
```

## Core Endpoints (Phase 1)
- Auth:
  - `POST /api/auth/request-otp`
  - `POST /api/auth/verify-otp`
- Resident:
  - `GET /api/me`
  - `PUT /api/me/address`
  - `GET /api/me/home`
  - `GET /api/schedule/month?year=YYYY&month=M`
- Billing/Finance:
  - `POST /api/billing/run-monthly?year=YYYY&month=M` (FINANCE_OFFICER/SUPER_ADMIN)
  - `GET /api/finance/unpaid?lga=...&year=YYYY&month=M` (FINANCE_OFFICER/SUPER_ADMIN)
  - `GET /api/invoices/{invoiceId}/pdf`
- Payments (stub):
  - `POST /api/payments/intent`
  - `POST /api/payments/webhook/stub`
- Operations:
  - `GET/POST/PUT /api/vehicles` (FLEET_MANAGER/SUPER_ADMIN)
  - `POST /api/collections/{addressId}/mark` (ROUTE_SUPERVISOR/SUPER_ADMIN)
- Issues:
  - `POST /api/issues` (multipart: JSON + optional photo)
  - `GET /api/issues/me`
  - `GET/POST /api/issues/{issueId}/messages`

## Running Tests (JUnit)
```bash
cd backend
mvn -s maven-settings.xml test
```
Tests use an in-memory H2 database via `backend/src/test/resources/application-test.properties`.

## Changelog
- **2026-03-16**: Upgraded Java runtime to version 21 LTS for improved security, performance, and long-term support. All tests pass with the new version.
- **2026-03-17**: Upgraded dependencies for security: JJWT to 0.12.6, H2 to 2.3.232. No CVEs detected in current dependencies.

## License
MIT License, see `LICENSE`.

