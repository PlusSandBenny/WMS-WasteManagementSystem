# Waste Management System - User Manual

This user manual explains how to run and use the Waste Management System (WMS) application.

## 1. What This App Does
WMS is a Nigeria-focused residential waste management and billing platform. It supports:
- Resident onboarding + verified address registration (State → LGA → Street/House/Landmark)
- Waste collection scheduling with holiday week-shift rules
- Real-time pickup status and next pickup information
- Monthly billing, invoice generation, and unpaid dashboard (by LGA)
- Fleet/vehicle management, route marking, and issue reporting
- In-app payment flow (stubbed for Phase 1)


## 2. User Roles
The system supports several roles (assigned in the database seed data):
- **RESIDENT**: View home dashboard, schedules, invoices, and submit issues.
- **FINANCE_OFFICER**: Run monthly billing, view unpaid dashboards, manage invoices.
- **FLEET_MANAGER**: Manage vehicles and routes.
- **ROUTE_SUPERVISOR**: Mark collection status (picked/missed/reported).
- **SUPER_ADMIN**: Full access to all admin operations.

Default demo users are seeded for development (credentials in `backend/src/main/resources/data` or seed logic):
- `finance@demo.ng` (Finance)
- `superadmin@demo.ng` (Admin)
- `resident@demo.ng` (Resident)
- `fleet@demo.ng` (Fleet Manager)
- `route@demo.ng` (Route Supervisor)
- `contractor@demo.ng` (Contractor)


## 3. Running the Application
### Option A: Docker Compose (recommended)
1. Install Docker & Docker Compose.
2. From the repo root:
   ```bash
   docker-compose up --build
   ```
3. Open the app in a browser:
   - Frontend: `http://localhost`
   - Backend API: `http://localhost:8080`


### Option B: Vagrant (VM)
1. Install Vagrant + VirtualBox.
2. From the repo root:
   ```bash
   vagrant up
   ```
3. Access the app in a browser:
   - Frontend: `http://localhost:8082`
   - Backend API: `http://localhost:8083`


## 4. Rebuilding and Resetting Containers
If you make changes to the code or encounter issues with the database schema, you may need to rebuild the containers and reset the database volume. Follow these steps:

### Step-by-Step Rebuild Process
1. **Stop and remove all containers and volumes** (this deletes the old database data):
   ```bash
   docker-compose down -v
   ```

2. **Rebuild the images and start the containers**:
   ```bash
   docker-compose up --build
   ```

3. **Verify containers are running**:
   ```bash
   docker-compose ps
   ```
   You should see all services (db, backend, frontend) with status "Up".

4. **Check logs if containers fail to start**:
   - Backend logs: `docker-compose logs backend --tail=50`
   - Frontend logs: `docker-compose logs frontend --tail=50`
   - Database logs: `docker-compose logs db --tail=50`

5. **Access the application**:
   - Frontend: `http://localhost` (or `http://localhost:8082` in Vagrant)
   - Backend API: `http://localhost:8080` (or `http://localhost:8083` in Vagrant)

### Notes
- The `-v` flag in `docker-compose down -v` removes named volumes, including the database data. This is necessary when the schema changes.
- If using Vagrant, run these commands inside the VM after `vagrant ssh`.
- Rebuilding may take a few minutes the first time as it downloads dependencies and builds the JAR.


## 4. Logging In
1. Open the frontend URL (e.g., `http://localhost` or `http://localhost:8082`).
2. Click **Login**.
3. Enter an email and request an OTP.
4. The OTP is returned in the API response in the current development build (for demo purposes).
5. Enter the OTP to log in.


## 5. Resident Workflow
### 5.1 Home Dashboard
- View which pickups have occurred today.
- See the next scheduled pickup date.
- View overdue invoices and amount owed.

### 5.2 Address Registration
- Navigate to **My Profile / Address**.
- Enter State → LGA → Street + House + Landmark.
- Save to link the address to your account.


## 6. Billing & Finance
### 6.1 Generating Monthly Invoices (Finance Officer)
- Login as `finance@demo.ng`.
- Use the backend endpoint:
  `POST /api/billing/run-monthly?year=YYYY&month=M`
- The system generates invoices for all active addresses.

### 6.2 Checking Unpaid Dashboard
- Navigate to the finance dashboard.
- Choose an LGA and month to view unpaid accounts.

### 6.3 Payment Flow (Stub)
1. Create a payment intent: `POST /api/payments/intent`.
2. The stub webhook endpoint is used to mark it paid:
   `POST /api/payments/webhook/stub`.


## 7. Fleet & Route Management
### 7.1 Vehicles
- Access `/vehicles` endpoints to add/edit vehicles (FLEET_MANAGER).

### 7.2 Collection Marking
- Route supervisors mark collection status at `/api/collections/{addressId}/mark`.


## 8. Issues & Support
### 8.1 Report an Issue
- Use the **Issue Report** page to submit a problem with photo upload.

### 8.2 Messaging
- Each issue has a message thread for follow-up communication.


## 9. Developer Notes
- Backend runs on Java 21 + Spring Boot 3.
- Frontend is a React + Vite PWA.
- Database: MySQL (H2 for tests).
- Real-time updates use WebSocket STOMP (`/ws` endpoint).


## 10. Troubleshooting
### 10.1 Backend Not Starting
- Ensure the database is reachable (MySQL container running).
- Check `backend/src/main/resources/application.yml` for datasource URL.

### 10.2 Frontend Not Loading
- Make sure the backend is running on `http://localhost:8080` (or forwarded port).
- Check browser console for API errors.

### 10.3 Docker / Vagrant
- If ports are already in use, edit `docker-compose.yml` or `Vagrantfile` port mappings.
- For Vagrant, run `vagrant destroy -f` then `vagrant up` to fully reset.
