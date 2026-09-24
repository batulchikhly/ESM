# ACME Frontend

## Setup

```powershell
Copy-Item .env.example .env
npm install
npm run dev
```

Set `VITE_API_BASE_URL` to the Spring Boot API base URL, normally `http://localhost:8080/api`. Demo login credentials are created by the backend seed when enabled:

- Email: `seed-admin@example.test`
- Password: `DemoPassword123!`

The access token is stored in browser local storage for this assessment, attached by the centralized Axios client, and cleared on logout or a `401` response. Do not use this storage approach for higher-risk production deployments without reviewing the XSS threat model.

## Commands

```powershell
npm run test
npm run build
npm run dev
```
