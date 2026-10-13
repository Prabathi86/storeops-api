# StoreOps API — High-Level Design Brief

## 1. Purpose and scope

StoreOps API is a Spring Boot service that exposes REST endpoints for store
operations. The codebase models operational tasks, projects and project membership,
store staff, user notifications, and store/region reports. It also provides an
activity bulk-status endpoint for shift handover.

This brief describes the current implementation, not a proposed target architecture.
Where a capability is represented by configuration or a model but is not implemented
end to end, that boundary is called out explicitly.

## 2. Runtime and technology

- Java 17 application built with Maven and Spring Boot 3.3.
- Spring MVC REST controllers with JSON request/response DTOs.
- Spring Boot Actuator health endpoints and Springdoc OpenAPI UI dependency.
- JUnit 5 and Spring MockMvc test support through `spring-boot-starter-test`.
- JaCoCo Maven plugin configured for test coverage reports.
- Runtime container based on Red Hat UBI 9 OpenJDK 17, running as UID 1001 and
  listening on port 8080.
- Helm chart under `helm/storeops` for Kubernetes/OpenShift deployment.

## 3. Architecture overview

The application uses a conventional layered, package-by-feature structure:

```text
HTTP client
    |
    v
REST controllers  -- bind paths, headers, request bodies, and HTTP responses
    |
    v
Services          -- validate use cases, coordinate repository operations,
    |                map domain objects to response DTOs, publish events
    +-------> Repository interfaces
    |              |
    |              v
    |         In-memory repository implementations
    |
    +-------> EventBus interface
                   |
                   v
              Logging EventBus implementation
```

Spring component scanning starts at `com.storeops` through
`StoreOpsApplication`. Controllers use constructor-injected services; services use
constructor-injected repository interfaces and, where needed, the `EventBus`.
Domain models are kept in each feature package, and controllers return DTOs rather
than repository interfaces.

The current deployment is a single Spring Boot process. The repositories are
process-local in-memory collections/maps; data is not durable and is not shared
between replicas. Restarting a pod loses the data held by those repositories.

## 4. Main feature areas

### Tasks and activities

`/api/tasks` supports listing, creating, retrieving, patching, and deleting tasks.
Task fields include title, store, status, priority, category, and assignee. Supported
statuses include TODO, IN_PROGRESS, DONE, and BLOCKED.

`PATCH /api/activities/bulk-status` handles shift handover status updates by
delegating to the task service. It accepts multiple task IDs and permits only DONE or
BLOCKED as bulk status values. Expected per-item failures (for example, invalid IDs,
invalid status, or a missing task) are returned alongside successful updates so the
rest of a valid batch can be processed. Successfully updated tasks publish a
`tasks.status.bulk-updated` event with task ID, old/new status, and a timestamp.

### Projects

`/api/projects` lists projects filtered by the required `X-Store-Id` request header,
creates projects, and adds members to a project. The service publishes events for
project creation and member addition.

### Users

`/api/users` lists and retrieves user DTOs, including optional profile fields.
The current user repository is in-memory and seeds a sample user. The source contains
an `AuthToken` model, but this brief does not infer an authentication or authorization
flow from that model; no security filter or authentication controller is evident in
the current API structure.

### Notifications

`/api/notifications` returns notifications for the user named by the required
`X-User-Id` request header. The in-memory notification repository seeds a sample
inventory notification.

### Reports

`/api/reports/stores/{storeId}` and `/api/reports/regions/{regionId}` return report
DTOs from the report repository. The current in-memory implementation constructs
ready sample report records for the requested target ID.

## 5. API and error handling

Controllers expose JSON endpoints under `/api`. Services return typed response DTOs
and lists. The project-wide `GlobalExceptionHandler` handles `AppError` subclasses
and returns a JSON object containing `code`, `message`, and `statusCode`. Current
specializations include validation errors (HTTP 400) and not-found errors (HTTP 404).

Some endpoints identify a store or user via request headers (`X-Store-Id` and
`X-User-Id`). These headers are identifiers in the current API contract; they should
not be assumed to authenticate or authorize the caller.

## 6. Data and event flow

Repository interfaces isolate service logic from storage mechanics, but the current
implementations are in-memory only:

- Tasks and projects use maps keyed by ID.
- Users use a map and are initialized with a sample record.
- Notifications use a list and are initialized with a sample record.
- Reports are generated as sample objects when queried.

There is no database driver, ORM, schema migration, or durable message broker
configured in the Maven dependencies. The `EventBus` is an abstraction, but its
current `InMemoryEventBus` implementation writes event details to the application
log. It does not persist events or deliver them to an external consumer.

Consequently, the repository and event interfaces provide seams for future
integration, but they do not make the current service stateful or horizontally
consistent.

## 7. Observability and operational deployment

Spring Boot Actuator is included. `application.properties` exposes management
endpoints and enables health probes. The Dockerfile packages `target/storeops.jar`
into a UBI OpenJDK 17 image, runs as a non-root user, and starts the JAR with optional
`JAVA_OPTS` JVM sizing.

The Helm chart currently renders a Deployment and ClusterIP Service. The Deployment
uses port 8080 and Actuator liveness/readiness probe paths. It currently hard-codes
one replica and probe timing values and renders `CONSUMER_REFRESH_URL`. A Route is
not rendered by the current templates.

Some additional settings in `values.yaml`—including `replicaCount`, resource requests
and limits, Spring profile, Secret values, probe settings, and Route fields—are not
consumed by the current templates. Treat these as declared values, not effective
runtime configuration, until the templates are wired and rendered manifests verified.
See [deployment.md](./deployment.md) for the deployment steps and specific chart
limitations.

## 8. Testing and quality checks

Tests use JUnit 5. Controller tests use Spring Boot Test and MockMvc to exercise HTTP
mapping and JSON behavior; service tests exercise business behavior directly. JaCoCo
is configured to write coverage reports during Maven verification under
`target/site/jacoco`.

The focused bulk-status tests provide coverage for valid updates, partial failures,
request validation, audit publication, and unexpected repository/EventBus failures.
The complete project test suite should be run in CI before release; focused feature
test results are not a substitute for the full suite.

## 9. Current constraints and design considerations

1. **In-memory persistence:** state is ephemeral and pod-local. Production use that
   requires persistence or multiple replicas needs a durable shared data store and
   appropriate migrations.
2. **Logging-only event bus:** events are not durable, replayable, or delivered
   asynchronously. Use a durable event mechanism if consumers require delivery
   guarantees.
3. **Identity headers are not security:** user/store headers are request data, not
   proof of caller identity. An explicit authentication/authorization design is
   required before exposing sensitive operations.
4. **Incomplete Helm value wiring:** deployment configuration should be aligned
   between `values.yaml` and templates and validated with `helm template` before
   relying on environment-specific settings.
5. **Sample data behavior:** several in-memory repositories seed or synthesize
   records, so API responses currently demonstrate contract behavior rather than
   representing a durable operational system of record.

## 10. Source map

| Concern | Source location |
| --- | --- |
| Spring Boot entry point | `src/main/java/com/storeops/StoreOpsApplication.java` |
| Shared errors and event bus | `src/main/java/com/storeops/common/` |
| Activities and tasks | `src/main/java/com/storeops/activities/`, `src/main/java/com/storeops/tasks/` |
| Projects | `src/main/java/com/storeops/projects/` |
| Users | `src/main/java/com/storeops/users/` |
| Notifications | `src/main/java/com/storeops/notifications/` |
| Reports | `src/main/java/com/storeops/reports/` |
| Runtime image | `Dockerfile` |
| OCP/Kubernetes chart | `helm/storeops/` |
| Deployment procedure | `deployment.md` |
