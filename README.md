# Government Bidding System - Frontend

> 🖥️ Web Interface for the Government Bidding System (EditaisGOV) built with Spring Boot, Thymeleaf, and Bootstrap 5.

This is the frontend application for the Bidding System, designed to provide a secure, responsive, and highly interactive user experience for both Government Buyers (creating and managing tenders) and Suppliers (placing bids). It consumes a REST API (Backend) and manages state and security through JWT tokens.

## 🚀 Key Features

* **Real-time Form Validation:** Debounced AJAX requests to check for email and name availability instantly during registration, preventing unnecessary form submissions.
* **Dynamic Password Strength:** Visual feedback indicating missing requirements (uppercase, numbers, special characters) while typing.
* **Role-Based Navigation:** UI elements and action buttons adapt dynamically depending on whether the user is a `FORNECEDOR` (Supplier) or a Buyer.
* **JWT Integration:** Token-based authentication handling. Extracts user data (like name and role) directly from the token payload to personalize the UI (e.g., Navbar greetings).
* **Responsive Layout:** Mobile-first approach using Bootstrap 5, standardizing layouts via Thymeleaf fragments for headers, footers, and structural components.

## 🛠️ Tech Stack

* **Java 17+**
* **Spring Boot** (Spring Web, Spring MVC)
* **Thymeleaf** (Server-side rendering & template engine)
* **Bootstrap 5.3** (UI/UX Styling & Grid)
* **Vanilla JavaScript** (DOM manipulation and Fetch API)
* **HTML5 / CSS3**

## ⚙️ Prerequisites

Before you begin, ensure you have met the following requirements:
* You have installed **Java 17** or higher.
* You have **Maven** installed.
* You are running the **Bidding System Backend** application (the API) locally on `http://localhost:8080` (or update the RestClient base URL accordingly).

## 📥 How to Run the Application

1. **Clone the repository:**
   ```bash
   git clone <your-repository-url>
   cd biddingFrontEnd
   ```

2. **Ensure the Backend is running:**
   Since this is the client application, the backend must be active to handle authentication, tender creation, and bidding processes.

3. **Build and Run:**
   Using Maven wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
   Or running the `FrontendApplication.java` directly from your IDE (Eclipse / IntelliJ IDEA).

4. **Access the application:**
   Open your browser and navigate to:
   ```text
   http://localhost:8081 
   ```
   *(Note: Ensure this runs on a different port than the backend, usually 8081 if backend is 8080)*

## 📂 Project Structure

```text
src/main/
├── java/com/bidding/system/frontend/
│   ├── controller/      # Web Controllers (routing, UI rendering)
│   ├── model/           # DTOs mapping backend data
│   └── service/         # API integration layer (RestClient)
└── resources/
    ├── static/          # CSS, JS, Images
    └── templates/       # Thymeleaf HTML views (layout, login, registrar, editais...)
```

## 📝 License

This project is intended for educational purposes as part of a Java Web Development Course.
