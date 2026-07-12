# EditaisGOV Frontend

Server-rendered web client for EditaisGOV, a government procurement (bidding) platform. Provides tender browsing, bid submission, tender creation, and role-based navigation. Consumes the [EditaisGOV Backend](https://github.com/jotapeor/biddingBackEnd) REST API.

## Tech Stack

- Java 21
- Spring Boot 4.0.6 (Web MVC, Thymeleaf, Validation)
- Bootstrap 5.3.8
- Vanilla JavaScript (AJAX validation, DOM handling)
- Maven

## Architecture

Classic MVC, server-side rendered with Thymeleaf. `ApiService` wraps a `RestClient` that calls the backend REST API. The JWT issued by the backend is stored in the HTTP session after login and attached to outgoing API calls; there is no client-side token storage. User role is decoded from the JWT payload to conditionally render UI elements (e.g. tender creation is only available to buyers).

## Routes

| Path | Access | Description |
|------|--------|--------------|
| `/` | Public | Landing page |
| `/login` | Public | Login form |
| `/registrar` | Public | Registration form, with live email/username availability checks |
| `/editais` | Authenticated | Tender listing; `?urgente=true` filters tenders closing within 48 hours |
| `/editais/{id}` | Authenticated | Tender detail and bid submission |
| `/editais/{id}/lances` | Authenticated | Bid list for a tender |
| `/novo-edital` | Authenticated (buyer) | Tender creation form |
| `/meus-lances` | Authenticated (supplier) | Bids placed by the current user |
| `/logout` | Authenticated | Invalidates the session |

## Requirements

- JDK 21+
- Maven 3.9+
- A running instance of [biddingBackEnd](https://github.com/jotapeor/biddingBackEnd) on `localhost:8080`

## Setup

```bash
./mvnw spring-boot:run
```

The application starts on `http://localhost:8081`. Start the backend first; this application has no data layer of its own and depends entirely on the API being reachable.

## Configuration

The backend base URL is currently hardcoded in `ApiService`. To point at a different backend instance, update the `baseUrl` value or externalize it via `application.properties` and an environment variable (e.g. `API_BASE_URL`).

## Related Project

[biddingBackEnd](https://github.com/jotapeor/biddingBackEnd) — Spring Boot REST API providing authentication, tender management, and bid processing for this client.
