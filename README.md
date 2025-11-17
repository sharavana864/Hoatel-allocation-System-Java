# Hostel Allocation System (Java + MySQL + Swing)

A desktop-based application for managing hostel room allocations using Java Swing and MySQL.
It allows administrators to allocate rooms, check availability, and view allocation history with a clean graphical interface.

## Overview

This system simplifies hostel management through features like:

Allocating rooms to students

Viewing all available rooms

Checking current room allocations

Automatic timestamp logging

MySQL database backend

Interactive Java Swing GUI

The software is ideal for small institutions, hostels, and training centers.

## Features

📝 Allocate Room — Assign rooms to students with one click

🏘️ View Available Rooms — Displays all free rooms

📄 View Allocations — Shows complete allocation log

💾 MySQL Integration — Persistent storage of all data

🖥️ GUI Based — Easy-to-use Swing interface

## Tech Stack
Component	Technology
Language	Java (Swing, JDBC)
Database	MySQL
Driver	MySQL Connector/J
UI	Java Swing
	Eclipse 
## Project Structure
HostelRoomAppMySQL.java      - Main Application
lib/mysql-connector-j.jar    - JDBC Driver
MySQL Database:
   - hostel_db
       - available_rooms
       - allocated_rooms

### Database Setup (MySQL)

Run the following SQL commands:


```
CREATE DATABASE hostel_db;

USE hostel_db;

CREATE TABLE available_rooms (
    room_number VARCHAR(10) PRIMARY KEY
);

CREATE TABLE allocated_rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_name VARCHAR(100),
    room_number VARCHAR(10),
    allocated_at VARCHAR(50)
);
```


### Insert sample rooms:

INSERT INTO available_rooms (room_number) VALUES
('101'), ('102'), ('103'), ('104'), ('105');

## How to Run
1. Compile the Java Program
javac -cp ".;lib/mysql-connector-j-9.5.0.jar" HostelRoomAppMySQL.java

2. Run the Program
java -cp ".;lib/mysql-connector-j-9.5.0.jar" HostelRoomAppMySQL


✔ Make sure mysql-connector-j-9.5.0.jar is inside the lib/ folder.

## Configuration

Update your database credentials inside the Java file:

String url = "jdbc:mysql://localhost:3306/hostel_db";
String user = "root";
String password = "$$$$$";

📸 GUI Preview (Optional)

You can add screenshots here (if you have images):

![Allocation Screen](screenshots/allocate.png)

Fork this repository

Create a feature branch

Commit your changes

Open a pull request
