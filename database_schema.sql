CREATE DATABASE IF NOT EXISTS ocean_view_resort;

USE ocean_view_resort;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'admin'
);

-- Default admin user
INSERT INTO
    users (username, password, role)
VALUES ('admin2', 'admin123', 'admin');

CREATE TABLE IF NOT EXISTS guests (
    guest_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    contact VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS reservations (
    res_no INT AUTO_INCREMENT PRIMARY KEY,
    guest_id INT NOT NULL,
    room_type VARCHAR(50) NOT NULL,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    room_rate DECIMAL(10, 2) NOT NULL,
    added_by INT,
    FOREIGN KEY (guest_id) REFERENCES guests (guest_id) ON DELETE CASCADE,
    FOREIGN KEY (added_by) REFERENCES users (id) ON DELETE SET NULL
);