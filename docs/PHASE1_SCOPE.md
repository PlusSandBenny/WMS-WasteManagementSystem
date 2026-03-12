# Phase 1 Scope (Sponsor-Mandated)

This document is the Phase 1 scope baseline for the Nigeria Residential Waste Management & Billing Platform.
All items in **Sponsor MUST** are non-negotiable for Phase 1 delivery.

## Sponsor MUST (Traceability)

1. **Verified Nigerian address registration**
   - Address model: `State -> LGA -> Street + House Number + Landmark` (no postcodes).
   - Resident and Office/Business registrations are supported.

2. **In-app payments**
   - Monthly waste fee.
   - Optional Garden Waste subscription.

3. **Instant pickup status + next pickup**
   - Resident home screen shows:
     - "Has my waste been picked up today?"
     - "When is the next pickup?"

4. **Automatic notifications**
   - Push + SMS + Email notifications:
     - 24 hours before next collection.
     - On collection day.

5. **Government real-time unpaid dashboard**
   - Real-time view by LGA of:
     - Houses/offices that have not paid for the current month.
     - Exact amount owed per account.

6. **Government collection status per house**
   - Per address, real-time collection status:
     - Picked / Missed / Reported.

7. **Admin/contractor operational tools**
   - Fleet and staff management.
   - Route assignment.
   - Holiday adjustments with week-shift behavior.

8. **Issue reporting + invoices + reconciliation**
   - Issue reporting with photo upload and in-app messaging.
   - PDF invoice generation.
   - Payment reconciliation with real-time "owing" status updates.

## Phase 1 Constraints / Assumptions

- Payments: Provider integrations (Paystack/Flutterwave/Remita) ship behind a provider abstraction; Phase 1 includes webhook-driven reconciliation and at least one provider enabled in production.
- Notifications: Phase 1 includes event scheduling + delivery provider abstraction; push/SMS/email providers can be swapped per council/contractor.
- Encryption: Application-level encryption is used where feasible; full-at-rest encryption may be provided by infrastructure (cloud-managed database + disk encryption).

## Out of Scope (Phase 2)

- Multi-language UI (English-only UI in Phase 1; i18n wiring included if required).
- Advanced analytics and predictive routing.
- Citizen rewards/loyalty.
- Native-only mobile app (PWA is the primary Phase 1 mobile channel; React Native can be a Phase 2 channel).

