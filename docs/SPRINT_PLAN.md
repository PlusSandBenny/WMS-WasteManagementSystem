# Sprint Plan (Phase 1)

This sprint plan is designed to deliver the sponsor's 8 non-negotiables with demonstrable progress every sprint.
Target cadence: 2-week sprints.

## Epics

- EPIC-ONB: Resident onboarding + address verification
- EPIC-SCH: Address-based calendar + holiday shifting + pickup status
- EPIC-BIL: Billing, invoices (PDF), payment reconciliation
- EPIC-NOT: Notifications (push/SMS/email) for schedule + billing
- EPIC-GOV: Government dashboards (unpaid + collection status, real-time)
- EPIC-OPS: Fleet/staff/routes + daily assignments
- EPIC-ISS: Issue reporting + photo upload + in-app messaging + audit trail
- EPIC-SEC: Security, access control, audit logs, NDPA alignment

## Sprint 0 (Foundation)

- Define data model + migrations; seed LGAs and schedules
- Implement authentication skeleton (OTP + JWT), RBAC roles
- Define API contract (OpenAPI) and frontend routing skeleton

## Sprint 1 (First Vertical Slice: Resident + Finance)

- Resident: register/login (OTP), capture/verify address
- Resident: home shows today's pickup status + next pickup
- Resident: monthly schedule calendar for address + holiday week-shift
- Billing: generate monthly invoice on the 1st (manual trigger for dev)
- Payments: create payment intent + mark paid via stub webhook
- Finance: unpaid dashboard by LGA (address, owed, overdue, history)
- Real-time: push unpaid changes via WebSocket/SSE to dashboard

## Sprint 2 (Operations + Issues + Notifications)

- Route supervisor: daily route assignment and marking picked/missed
- Fleet manager: vehicle CRUD + status; live location placeholders
- Issue reporting: photo upload + ticket workflow + in-app chat
- Notifications: schedule reminders (T-24h and day-of), billing reminders
- PDF invoice generation and download

## Definition of Done (Phase 1)

- All Sponsor MUST items are demonstrably met in staging with auditability
- Security controls: RBAC enforced across all endpoints; short-lived sessions
- Performance targets met on key pages (<2s on typical broadband)
- Offline behavior: schedule and last payment status available offline in PWA

