# Copilot Instructions for SentinelIQ

## Project Overview
SentinelIQ is an AI-powered email analysis and spam detection platform. It integrates Gmail (OAuth), analyzes messages in real-time, and uses AI for spam detection, sender trust scoring, and behavioral profiling. The system is composed of three main components:

- **Backend** (`/backend`): Spring Boot API for Gmail integration, user/session management, and communication with the ML engine.
- **Engine** (`/engine`): FastAPI-based Python ML service for spam detection.
- **Frontend** (`/frontend`): Next.js app for user authentication, inbox UI, and dashboard.

## Key Architectural Patterns
- **RESTful API**: All communication between frontend and backend is via REST endpoints (see `controller` classes in backend).
- **JWT Auth**: Authentication is handled with JWT tokens, set as cookies. See `JwtAuthenticationFilter.java` and `JwtUtil.java`.
- **Gmail OAuth**: Gmail integration uses OAuth2, managed by `GmailOAuthController.java` and `GmailService.java`.
- **Repository Pattern**: Data access is abstracted via Spring Data JPA repositories (e.g., `UserRepository.java`, `EmailRepository.java`).
- **Frontend Auth State**: Managed in `AuthContext.tsx` and propagated via React context.

## Developer Workflows
### Backend
- **Build**: From `/backend`, use `./mvnw clean package` (or `mvnw.cmd` on Windows).
- **Run**: `./mvnw spring-boot:run` (or `mvnw.cmd spring-boot:run`).
- **Test**: `./mvnw test` (JUnit tests in `src/test/java`).
- **Key Files**: `pom.xml`, `src/main/java/com/sentineliq/backend/controller/`, `src/main/java/com/sentineliq/backend/service/`, `src/main/java/com/sentineliq/backend/util/JwtUtil.java`

### Frontend
- **Install**: `npm install` in `/frontend`
- **Dev Server**: `npm run dev` (Next.js, port 3000)
- **Key Files**: `src/app/`, `src/components/`, `src/context/AuthContext.tsx`, `middleware.ts`

## Project-Specific Conventions
- **Endpoints**: All API endpoints are prefixed with `/api/` (frontend) and mapped to controllers in backend.
- **Session**: JWT tokens are stored as cookies (`access_token`, `refresh_token`).
- **Gmail OAuth**: Initiated via `/auth/gmail` endpoint; callback handled in `GmailOAuthController.java`.
- **Email Sync**: Manual resync via `POST /api/gmail/resync?inboxId=...` (see `EmailController.java`).
- **Frontend Auth**: Use `AuthContext` for all session checks and user state.

## Integration Points
- **Gmail API**: Uses `google-api-client` and `google-oauth-client-jetty` dependencies (see `pom.xml`).
- **ML Engine**: Backend communicates with a Python FastAPI service (not included here) for spam verdicts.
- **Database**: Uses JPA/Hibernate with (likely) PostgreSQL (see `application.properties`).

## Examples
- **User Signup**: `POST /api/auth/signup` → `AuthenticationController.registerUser`
- **Login**: `POST /api/auth/login` → `AuthenticationController.loginUser`
- **Fetch Emails**: `GET /api/gmail/emails?inboxId=...` → `EmailController.getEmails`
- **Resync Inbox**: `POST /api/gmail/resync?inboxId=...` → `EmailController.resyncInbox`

## References
- See `README.md` (root) for a full system flow and file mapping.
- See backend controller/service/repository classes for API and data logic.
- See frontend `src/app/` and `src/context/AuthContext.tsx` for UI and auth logic.

---
If any conventions or flows are unclear, check the root `README.md` or ask for clarification.
