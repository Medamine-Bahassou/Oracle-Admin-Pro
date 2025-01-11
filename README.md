<div align="center">
  <img src="https://github.com/user-attachments/assets/9b92324b-06ea-4e30-a2cf-4a1644d3e6b4" />
</div>
 


# Oracle Admin Pro

![image](https://github.com/user-attachments/assets/7a5b5154-51c8-40ec-931e-d86a6677aa06)

## Project Overview

Oracle Admin Pro is a web-based administration application designed to streamline database management tasks for Oracle databases. Built using Java EE (Spring Boot) for the backend and Angular for the frontend, this application aims to provide a user-friendly interface for key administrative operations.

![de](https://github.com/user-attachments/assets/d1b89aa0-3a6b-442f-8568-6d413800d469)

## Key Features

Based on the provided document, the project aims to provide the following functionalities:

*   **User Management:**
    *   Create, modify, and delete Oracle users.
    *   Manage user roles and access privileges via a web interface.
    *   Configure space quotas and password policies.
*   **Backup and Restore:**
    *   Initiate full and incremental backups using RMAN directly from the application.
    *   View backup history and restore options for specific dates.
    *   Schedule automated backups with customizable plans.
*   **Data Security:**
    *   Configure Transparent Data Encryption (TDE) policies.
    *   Manage Virtual Private Databases (VPD) to control data access.
    *   Enable security audits.
*   **Performance Monitoring:**
    *   Display AWR and ASH reports with graphical visualization (e.g., using Chart.js).
    *   Real-time dashboard for resource utilization (CPU, I/O, memory).
*   **Performance Optimization:**
    *   Interface to identify and optimize slow-running queries with SQL Tuning Advisor.
    *   Schedule the recalculation of table and index statistics.
*   **High Availability:**
    *   Configure and monitor Oracle Data Guard.
    *   Simulate switchovers and failovers with reports on availability.

## Team

*   **Mohamed Amine BAHASSOU**
*   **Hodaifa ECHFFANI**

## Supervisor

*   **Mohamed BEN AHMED**

## Technologies Used

*   **Database:** Oracle 19c
*   **Backend:**
    *   Java EE (Spring Boot)
    *   Hibernate (ORM)
    *   JDBC (for SQL command execution)
    *   Spring Security (authentication and session management)
    *   REST or SOAP (web service implementation)
*   **Frontend:**
    *   Angular
    *   Tailwind CSS (for styling)
    *   Daisy UI (for UI components)
    *   Chart.js (for visualizations)

## Project Structure

The project is divided into two main parts:

1.  **Backend:** A Spring Boot application responsible for handling database interactions, security, and business logic.
2.  **Frontend:** An Angular application that provides the user interface for the admin panel.

## Installation and Setup

### Prerequisites

*   Java Development Kit (JDK) 17 or higher
*   Maven
*   Node.js and npm (for Angular development)
*   Oracle 19c Database installed and accessible

### Backend Setup (Spring Boot)

1.  **Clone the repository:**
    ```bash
    git clone <repository_url>
    cd oracle-admin-pro/backend
    ```
2.  **Install Dependencies:**
    *   The `pom.xml` shows all necessary dependencies; Maven will automatically download them.
3.  **Configure Database:**
    *   Update `application.properties` or `application.yml` with your Oracle database connection details.
4.  **Build and Run:**
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```
    The backend server will run on the port defined in `application.properties` or `application.yml`

### Frontend Setup (Angular)

1.  **Navigate to the Frontend Directory:**
    ```bash
    cd ../frontend
    ```
2. **Install Node Dependencies:**
      ```bash
      npm install
      ```
3. **Run the Angular App:**
      ```bash
      ng serve
     ```
     The frontend server will run on port `4200`.

## Usage

1.  Access the application in your web browser using the URL and port of the frontend server (e.g., `http://localhost:4200`).
2.  Log in with your credentials (currently `admin/admin` in the example).
3.  Navigate through the admin panel to explore the features.

## Contributing

If you wish to contribute to the project, please follow these guidelines:

1.  Fork the repository.
2.  Create a new branch for your feature or bug fix.
3.  Commit your changes with clear and descriptive messages.
4.  Submit a pull request to the main repository.
