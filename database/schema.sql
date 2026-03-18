-- MySQL Database Schema for Waste Management System

-- Create database
CREATE DATABASE IF NOT EXISTS waste_management;
USE waste_management;

-- Users table (residents, admins, contractors)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('RESIDENT', 'SUPER_ADMIN', 'ADMIN', 'CONTRACTOR', 'FLEET_MANAGER', 'FINANCE_OFFICER', 'ROUTE_SUPERVISOR') NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Addresses table (Nigerian address system)
CREATE TABLE addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    state VARCHAR(100) NOT NULL,
    lga VARCHAR(100) NOT NULL,
    street VARCHAR(255) NOT NULL,
    house_number VARCHAR(50) NOT NULL,
    landmark VARCHAR(255),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Waste collection schedules
CREATE TABLE collection_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lga VARCHAR(100) NOT NULL,
    bin_type ENUM('GENERAL_WASTE', 'RECYCLING', 'GARDEN', 'FOOD_WASTE') NOT NULL,
    collection_day ENUM('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY') NOT NULL,
    collection_time TIME NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Public holidays
CREATE TABLE public_holidays (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    holiday_date DATE NOT NULL,
    description VARCHAR(255),
    lga VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Payments and subscriptions
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    invoice_id BIGINT,
    amount DECIMAL(10, 2) NOT NULL,
    payment_type ENUM('MONTHLY_FEE', 'GARDEN_SUBSCRIPTION', 'MISSED_BIN_FEE', 'BULKY_WASTE') NOT NULL,
    payment_method ENUM('CARD', 'BANK_TRANSFER', 'USSD') NOT NULL,
    status ENUM('PENDING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING',
    provider VARCHAR(50),
    transaction_id VARCHAR(255),
    provider_reference VARCHAR(255),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    paid_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Collection records
CREATE TABLE collection_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    address_id BIGINT NOT NULL,
    bin_type ENUM('GENERAL_WASTE', 'RECYCLING', 'GARDEN', 'FOOD_WASTE') NOT NULL,
    scheduled_date DATE NOT NULL,
    actual_collection_time TIMESTAMP,
    status ENUM('SCHEDULED', 'PICKED_UP', 'MISSED', 'REPORTED') DEFAULT 'SCHEDULED',
    driver_id BIGINT,
    vehicle_id BIGINT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (address_id) REFERENCES addresses(id),
    FOREIGN KEY (driver_id) REFERENCES users(id)
);

-- Invoices (monthly billing)
CREATE TABLE invoices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    address_id BIGINT NOT NULL,
    lga VARCHAR(100) NOT NULL,
    billing_period VARCHAR(7) NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL DEFAULT 0,
    amount_paid DECIMAL(12, 2) NOT NULL DEFAULT 0,
    status ENUM('UNPAID', 'PARTIALLY_PAID', 'PAID') NOT NULL DEFAULT 'UNPAID',
    due_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_invoice_address_month (address_id, billing_period),
    FOREIGN KEY (address_id) REFERENCES addresses(id)
);

-- Add invoice FK after invoices exist
ALTER TABLE payments
    ADD CONSTRAINT fk_payments_invoice
    FOREIGN KEY (invoice_id) REFERENCES invoices(id);

-- Vehicles
CREATE TABLE vehicles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    license_plate VARCHAR(20) UNIQUE NOT NULL,
    vehicle_type VARCHAR(100),
    capacity INT,
    status ENUM('ACTIVE', 'MAINTENANCE', 'OUT_OF_SERVICE') DEFAULT 'ACTIVE',
    gps_latitude DECIMAL(10, 8),
    gps_longitude DECIMAL(11, 8),
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Issues and complaints
CREATE TABLE issues (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    address_id BIGINT,
    issue_type ENUM('MISSED_COLLECTION', 'DAMAGED_BIN', 'OVERFLOWING_BIN', 'OTHER') NOT NULL,
    description TEXT,
    photo_url VARCHAR(500),
    status ENUM('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') DEFAULT 'OPEN',
    assigned_to BIGINT,
    resolution TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (address_id) REFERENCES addresses(id),
    FOREIGN KEY (assigned_to) REFERENCES users(id)
);

-- Issue message thread (in-app messaging)
CREATE TABLE issue_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    issue_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (issue_id) REFERENCES issues(id),
    FOREIGN KEY (sender_id) REFERENCES users(id)
);

-- Notifications
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    lga VARCHAR(100),
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    notification_type ENUM('COLLECTION_REMINDER', 'PAYMENT_DUE', 'GENERAL_ANNOUNCEMENT', 'ISSUE_UPDATE') NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_addresses_lga ON addresses(lga);
CREATE INDEX idx_collection_records_address_date ON collection_records(address_id, scheduled_date);
CREATE INDEX idx_payments_user_status ON payments(user_id, status);
CREATE INDEX idx_issues_status ON issues(status);
CREATE INDEX idx_invoices_lga_month ON invoices(lga, billing_period);
