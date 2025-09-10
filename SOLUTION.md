### Input Validation & Error Handling
- All incoming requests are validated with `jakarta.validation` annotations (`@NotNull`, `@Positive`, etc.).
- Validation errors return `400 Bad Request` with field-level messages.
- Entity not found errors return `404 Not Found`.
- Business rule violations (e.g., insufficient funds) return `422 Unprocessable Entity`.
- Unexpected errors return `500 Internal Server Error`.

### Future Authentication
- Services are designed with SOLID principles (thin controllers, services for business logic).
- Spring Security configuration is modular (`SecurityFilterChain`) and currently allows all requests.
- Authentication can be added later by enabling JWT/OAuth2 and updating the filter chain, without modifying services.