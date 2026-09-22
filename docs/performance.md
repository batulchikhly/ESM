# Performance and Capacity Notes

**Target:** Approximately 10,000 employees with responsive HR Manager workflows.  
**Principle:** Use measured query plans and representative seed data; do not promise a latency number before testing.

## PostgreSQL

- Use indexes for employee ID, normalized email, common department/country/status filters, salary history by employee and effective date, and audit history by employee and timestamp.
- Add `pg_trgm` name indexes only if substring search is required and query plans justify them.
- Keep currency and country as constrained codes so filters remain selective and consistent.
- Use `EXPLAIN (ANALYZE, BUFFERS)` against the deterministic 10,000-employee dataset for list, salary-history, and dashboard queries.
- Treat every index as a write and seed cost; remove indexes that do not support measured access patterns.

## API and query design

- Apply `LIMIT/OFFSET` or a bounded page request for the directory, with a maximum page size such as 100. Offset pagination is adequate at this scale; keyset pagination can be introduced later if deep-page performance becomes a measured problem.
- Allowlist sortable columns and build parameterized predicates through Spring Data specifications or explicit repository queries.
- Return summary DTOs for the directory rather than full entities, audit metadata, or unneeded fields.
- Fetch current salary through a projection/query designed for the list, not by loading each employee and lazily traversing salary records.
- Use one purpose-built aggregate query per dashboard view or a small set of grouped projections. Do not fetch all salaries into Java for grouping.
- Define transaction boundaries around salary mutation and audit insertion; avoid long-running transactions around read-only pages.

## ORM and N+1 avoidance

- Keep JPA associations conservative. Do not expose entities from controllers or rely on unrestricted lazy traversal during serialization.
- Use DTO projections, explicit joins, `@EntityGraph` where useful, and batch fetching only when it is measured to help.
- Verify Hibernate SQL in development and add tests for query count on high-risk endpoints such as employee lists and dashboard views.

## Dashboard aggregation

Use database-side grouping on the current effective salary record. Raw statistics are grouped by currency because monetary amounts are not comparable across currencies. If normalized reporting is requested, join the versioned static FX table and calculate `amount * rate` inside the query, returning the rate date and reporting currency. Never sum mixed raw currencies.

Salary-band definitions should be stable configuration, for example `0-49,999`, `50,000-99,999`, and `100,000+` in a stated currency context. Bands across currencies require normalization and must be labeled as reference figures. If the bands are intended to compare local salaries, calculate them separately per currency.

At 10,000 rows, live aggregation queries are reasonable for an MVP. If measured usage later makes them expensive, add a carefully invalidated summary table or cache as a later decision; do not introduce Redis or event-driven infrastructure preemptively.

## React rendering and payloads

- Keep employee query state in the URL and request only the current page.
- Debounce text search and cancel or ignore stale requests.
- Render a bounded table, not all 10,000 rows; use stable row keys and avoid unnecessary parent state updates.
- Split dashboard widgets so one failed query does not blank the entire screen.
- Format currency values at the display boundary while preserving numeric values in API types.
- Use skeleton/loading states without causing layout shifts, and make empty/error states explicit.

## Seeding

Generate deterministic data in batches using prepared inserts and a fixed random seed. Prefer database transactions sized to avoid excessive memory and transaction logs while retaining reproducibility. Verify row counts, uniqueness, salary-record counts, country/currency distribution, and rerun behavior as part of the seed test. The stable employee IDs and seed-version marker make repeated runs idempotent.

## Measurement plan

Before calling the MVP production-ready, exercise the seeded dataset with:

- employee search by ID, name/email, department, country, currency, status, and salary range;
- first, middle, and deep directory pages with sorting;
- current salary and salary-history reads;
- each dashboard aggregation;
- concurrent salary updates for one employee and independent employees.

Capture query plans, heap usage, request payload sizes, and browser rendering behavior. Set performance budgets only after these measurements and the chosen deployment size are known.
