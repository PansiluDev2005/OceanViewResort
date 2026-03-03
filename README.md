# Ocean View Resort

A Java Web Application (Servlets & JSP) with Maven.

## How to Run Locally

You don't need to install a local Tomcat server. You can run the application directly using the Maven Cargo plugin.

1.  **Start MySQL and setup the database:**
    - Open your MySQL client.
    - Run the `database_schema.sql` script to create the database and tables.
    - Add the default user `admin2` with password `admin123`.
    - If your MySQL uses different credentials, update them in `src/main/java/com/oceanview/config/DatabaseConnection.java`.

2.  **Run the application using Maven:**
    Open your terminal in the project folder and run:

    ```bash
    mvn clean package cargo:run
    ```

3.  **Access the application:**
    Once the server starts (you'll see `Tomcat 9.x starting...`), open your browser and go to:
    ```text
    http://localhost:8080/
    ```

## How to Run with Docker

If you prefer using Docker:

1.  **Build the image:**

    ```bash
    docker build -t ocean-view-resort .
    ```

2.  **Run the container:**
    ```bash
    docker run -p 8080:8080 ocean-view-resort
    ```
    Then, access `http://localhost:8080/` in your browser.
