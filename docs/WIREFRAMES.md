# Wireframes (Phase 1)

These wireframes are intentionally low-fidelity and focus on layout, information hierarchy, and required actions.

## 1. Resident: Onboarding (Register + Verify Address)

```
┌───────────────────────────────────────────────┐
│ Nigeria Waste Services                        │
│  Create Account                               │
├───────────────────────────────────────────────┤
│ Phone or Email                                │
│ [ +234 801 234 5678               ] [Send OTP]│
│ OTP                                           │
│ [  _  _  _  _  _  _ ]           [Verify OTP]  │
│                                               │
│ Address (Required)                            │
│ State        [ Lagos            v ]           │
│ LGA          [ Ikeja            v ]           │
│ Street       [ Allen Avenue          ]        │
│ House No.    [ 12B                  ]        │
│ Landmark     [ Near XYZ Pharmacy     ]        │
│                                               │
│ [ Save & Continue ]                           │
└───────────────────────────────────────────────┘
```

Acceptance anchors:
- No postcode field.
- Address required before schedule/payment access.

## 2. Resident: Home (Pickup Status + Next Pickup + Pay)

```
┌───────────────────────────────────────────────┐
│ Good morning, Amina                           │
│  Address: Lagos / Ikeja / Allen Ave 12B       │
├───────────────────────────────────────────────┤
│ TODAY'S STATUS                                │
│  General Waste: Picked up at 08:45            │
│  Recycling: Not scheduled today               │
│                                               │
│ NEXT PICKUP                                   │
│  Green bin (General) tomorrow, 6:00am         │
│                                               │
│ BILLING                                       │
│  This month: NGN 2,000 due in 3 days           │
│  [ Pay now ]   [ View invoice (PDF) ]         │
│                                               │
│ REPORT                                        │
│  [ Report missed bin ]                        │
└───────────────────────────────────────────────┘
```

## 3. Resident: Schedule Calendar (Monthly)

```
┌───────────────────────────────────────────────┐
│ Schedule (March 2026)                         │
│ Lagos / Ikeja                                 │
├───────────────────────────────────────────────┤
│ Legend: General (Green) | Recycling (Blue)    │
│         Garden (Brown)  | Food (Black)        │
│                                               │
│  Mo Tu We Th Fr Sa Su                          │
│         1G  2   3R  4  5  6  7                 │
│  8   9G 10  11R 12 13 14 15                    │
│  ...                                          │
│                                               │
│ Holiday Adjustment Notice                      │
│  Public holiday on Wed 11th -> week shifts +1  │
└───────────────────────────────────────────────┘
```

## 4. Resident: Report Issue (Photo + Chat)

```
┌───────────────────────────────────────────────┐
│ Report an Issue                               │
├───────────────────────────────────────────────┤
│ Issue Type  [ Missed Collection v ]           │
│ Details     [__________________________]      │
│ Photo       [ Choose photo ] [ Preview ]      │
│                                               │
│ [ Submit ]                                    │
├───────────────────────────────────────────────┤
│ Conversation                                  │
│  Supervisor: "We are checking your street..." │
│  You: "Bin is still outside."                 │
│  [ Message...________________ ] [Send]        │
└───────────────────────────────────────────────┘
```

## 5. Government: Finance Dashboard (Unpaid, Real-Time)

```
┌───────────────────────────────────────────────────────────────┐
│ Finance Dashboard                              LGA: [Ikeja v] │
├───────────────────────────────────────────────────────────────┤
│ KPI: Total owed NGN 12,400,000 | Collected NGN 8,900,000       │
│                                                               │
│ Unpaid this month                                              │
│ ┌───────────────────────────────────────────────────────────┐ │
│ │ Address                     Owing  Days Overdue  Actions   │ │
│ │ Allen Ave 12B (Landmark..)  2,000  3            [Remind]   │ │
│ │ ...                                                       │ │
│ └───────────────────────────────────────────────────────────┘ │
│                                                               │
│ Real-time stream: payments and status updates                  │
└───────────────────────────────────────────────────────────────┘
```

## 6. Government: Route Supervisor (Mark Picked/Missed)

```
┌───────────────────────────────────────────────┐
│ Route: Ikeja - Zone A (Today)                 │
├───────────────────────────────────────────────┤
│ Vehicle: KJA-123AA   Driver: Musa   Loaders: 2 │
│                                               │
│ 1) Allen Ave 12B   [Picked] [Missed] [Note]   │
│ 2) Opebi Rd 8      [Picked] [Missed] [Note]   │
│ ...                                           │
└───────────────────────────────────────────────┘
```

