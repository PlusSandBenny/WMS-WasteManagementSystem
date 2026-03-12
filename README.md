# Waste Management System - Nigeria Residential Waste Management & Billing Platform

## Overview
This is a comprehensive SaaS solution for waste management in Nigeria, providing residents with easy access to waste collection services and government councils with real-time monitoring and control.

## Features
- Resident registration and address verification
- Real-time waste collection scheduling and tracking
- Payment processing (monthly fees, subscriptions, special collections)
- Government dashboard for monitoring unpaid households and collection status
- Fleet and employee management
- Issue reporting with photo upload
- Multi-language support (English, Yoruba, Hausa, Igbo)
- PWA for mobile access
- Offline functionality for low-connectivity areas

## Technology Stack
- **Frontend**: React with Vite (PWA support)
- **Backend**: Java Spring Boot
- **Database**: MySQL
- **Authentication**: JWT with OTP
- **Payments**: Integration with Paystack, Flutterwave, Remita
- **Notifications**: Push notifications, SMS, Email

## Project Structure
```
WMS-WasteManagementSystem/
├── frontend/          # React application
├── backend/           # Spring Boot application
├── database/          # MySQL scripts and schema
├── docs/              # Documentation
└── .github/           # GitHub workflows and instructions
```

## Getting Started

### Prerequisites
- Node.js (v18+)
- Java (v17+)
- MySQL (v8+)
- Maven

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd WMS-WasteManagementSystem
   ```

2. **Setup Database**
   ```bash
   mysql -u root -p < database/schema.sql
   ```

3. **Setup Backend**
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

4. **Setup Frontend**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

## API Documentation
API endpoints will be documented using Swagger/OpenAPI.

## Contributing
Please read CONTRIBUTING.md for details on our code of conduct and the process for submitting pull requests.

## License
This project is licensed under the MIT License - see the LICENSE file for details.