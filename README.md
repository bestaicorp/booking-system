# Booking System

REST API for property booking management built with Spring Boot 3, Java 21, and H2.

## Tech Stack

- **Java 21**, **Spring Boot 3.5**, **Spring Data JPA**
- **H2** in-memory database
- **Bean Validation** (Jakarta)
- **Lombok**
- **SpringDoc OpenAPI** (Swagger UI)
- **JUnit 5** integration tests

## How to Run

```bash
./gradlew bootRun
```

The app starts at `http://localhost:8080`.

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:bookingdb`)

## Live Demo

- **Swagger UI**: https://booking-system-e420.onrender.com/swagger-ui/index.html
- **Frontend**: https://booking-system-frontend-beta.vercel.app

> Note: The Render backend may take ~30 seconds to wake up on first request.

## API Endpoints

### Bookings `/api/v1/bookings`

| Method | Path           | Description                |
|--------|----------------|----------------------------|
| POST   | `/`            | Create a booking           |
| GET    | `/`            | List all (paginated)       |
| GET    | `/{id}`        | Get by ID                  |
| PUT    | `/{id}`        | Update a booking           |
| PATCH  | `/{id}/cancel` | Cancel                     |
| PATCH  | `/{id}/rebook` | Rebook a cancelled booking |
| DELETE | `/{id}`        | Delete                     |

### Blocks `/api/v1/blocks`

| Method | Path    | Description          |
|--------|---------|----------------------|
| POST   | `/`     | Create a block       |
| GET    | `/`     | List all (paginated) |
| GET    | `/{id}` | Get by ID            |
| PUT    | `/{id}` | Update a block       |
| DELETE | `/{id}` | Delete               |

### Properties `/api/v1/properties`

| Method | Path    | Description          |
|--------|---------|----------------------|
| POST   | `/`     | Create a property    |
| GET    | `/`     | List all (paginated) |
| GET    | `/{id}` | Get by ID            |
| PUT    | `/{id}` | Update               |
| DELETE | `/{id}` | Delete               |

### Guests `/api/v1/guests`

| Method | Path    | Description          |
|--------|---------|----------------------|
| POST   | `/`     | Create a guest       |
| GET    | `/`     | List all (paginated) |
| GET    | `/{id}` | Get by ID            |
| PUT    | `/{id}` | Update               |
| DELETE | `/{id}` | Delete               |

## Key Design Decisions

### Overlap Prevention

Bookings and blocks cannot overlap on the same property. The overlap check uses:

```sql
b.startDate < :endDate AND b.endDate > :startDate
```

Adjacent dates are allowed (checkout day = next check-in day).

### Pessimistic Locking

Concurrent booking requests for the same property are serialized using `SELECT ... FOR UPDATE` on the property row. This
prevents double bookings when two requests arrive simultaneously.

```java

@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT p FROM Property p WHERE p.id = :id")
Optional<Property> findAndLockProperty(@Param("id") Long id);
```

### Booking Lifecycle

```
BOOKED  -->  CANCELLED  -->  REBOOKED
```

- Only active bookings (BOOKED, REBOOKED) occupy dates
- Cancelled bookings free up their dates
- Rebooking re-validates dates and checks for overlaps

### Validation

- `startDate` must be before `endDate`
- Dates cannot be in the past
- `endDate` cannot be more than 2 years in the future
- `startDate` and `endDate` cannot be equal
- `propertyId` and `guestId` must be positive

### Error Handling

Centralized via `GlobalExceptionHandler` with consistent JSON responses:

| Status | When                                  |
|--------|---------------------------------------|
| 400    | Validation errors, invalid date range |
| 404    | Entity not found                      |
| 409    | Date overlap, invalid booking state   |
| 415    | Wrong content type                    |
| 500    | Unexpected errors                     |

## Project Structure

```
src/main/java/com/booking/system/
    controller/     4 REST controllers
    service/        6 services
    repository/     4 JPA repositories
    model/          4 entities (Booking, Block, Property, Guest)
    dto/            8 DTOs (request + response per entity)
    enumeration/    BookingStatus, PropertyType
    exception/      6 custom exceptions + GlobalExceptionHandler
    config/         OpenAPI configuration
```

## Tests

Integration tests using `MockMvc` + `@SpringBootTest` + H2:

```bash
./gradlew test
```

| Test Class                             | Coverage                                       |
|----------------------------------------|------------------------------------------------|
| BookingControllerIntegrationTest       | CRUD, validation, overlap, pessimistic locking |
| BlockControllerIntegrationTest         | CRUD, validation, overlap with bookings        |
| PropertyGuestControllerIntegrationTest | CRUD, validation, cascade protection           |

The pessimistic locking test uses `CountDownLatch` to guarantee two threads execute simultaneously, verifying that only
one booking succeeds while the other gets 409 Conflict.
