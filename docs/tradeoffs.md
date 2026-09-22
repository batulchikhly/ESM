# Architecture Trade-offs

**Scope:** ACME Employee Salary Management MVP  
**Source of truth:** [Product Requirements Document](requirements.md)

## Modular monolith over microservices

A modular monolith gives the MVP clear feature boundaries without distributed deployments, network failure modes, service discovery, duplicated DTOs, or cross-service transactions. Approximately 10,000 employees does not justify independently scaling employee, salary, audit, and dashboard services. Feature packages and explicit service boundaries preserve a future extraction path if a real operational need appears.

The trade-off is that modules share one runtime and database, so package boundaries must be enforced by review and tests. This is appropriate for a take-home assessment and keeps local setup demonstrable.

## PostgreSQL over a document database

Salary history, audit records, users, employees, uniqueness, foreign keys, date-range consistency, and aggregations are relational concerns. PostgreSQL provides transactions, constraints, indexed filtering, JSONB for limited metadata, and strong aggregation support in one dependency. The trade-off is schema migration discipline, which Flyway addresses.

## Server-side pagination over loading all employees

The PRD requires pagination, search, filtering, and sorting for approximately 10,000 employees. Database-side pagination bounds API payloads, Java memory, and React rendering, and remains the correct shape if the dataset grows. The trade-off is more query and URL-state handling and the need for suitable indexes.

## Database-side analytics over application aggregation

`COUNT`, `AVG`, `MIN`, `MAX`, grouping, and salary-band calculations belong close to the data. Database-side aggregation avoids transferring thousands of rows and prevents accidentally using different filtering rules in multiple clients. The trade-off is that query projections and SQL become more deliberate; repository integration tests should protect them.

## Salary history over an overwriteable salary field

An append-oriented salary record model preserves prior values, supports effective-dated changes, enables audit comparisons, and answers historical questions. A single mutable salary field would be simpler initially but would destroy business history and make correction/audit behavior unreliable. The trade-off is range validation, current-record selection, and transaction locking.

## Static reference FX over live FX

The PRD explicitly keeps live exchange-rate APIs out of scope. Versioned static rates make reports deterministic, testable, available without an external dependency, and reproducible across runs. The trade-off is that normalized values are not current market values. The product must label normalized reports with the reporting currency and rate date, retain original salary/currency values, and never present normalized figures as payroll values.

## Basic JWT authentication over enterprise SSO

A single HR Manager persona needs authenticated protected APIs, not an identity-provider integration. Spring Security plus hashed passwords and signed short-lived JWTs is understandable and runnable in a take-home environment. The trade-off is that token revocation, MFA, lifecycle management, and enterprise identity policies are limited. Those belong in a later SSO phase, not in the MVP architecture.

## Deterministic seed data over ad hoc fixtures

A fixed seed creates exactly 10,000 synthetic employees with repeatable distributions and makes performance and dashboard behavior demonstrable. Stable IDs and a seed-version marker make reruns safe. The trade-off is less realism than production data and the need to treat seed versions as controlled data operations. No real people's data should be used.

## One deployment over separate frontend/backend platforms

A static React host plus one Spring Boot container and managed PostgreSQL is enough for the assessment. Docker Compose gives local reproducibility, while managed PostgreSQL avoids spending the assessment on database operations. The trade-off is fewer independent scaling knobs, which is acceptable for the expected workload.
