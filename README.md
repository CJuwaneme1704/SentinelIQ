# SentinelIQ

**AI-Powered Email Analysis and Spam Detection Platform**

SentinelIQ is a smart inbox companion that connects to your email, analyzes messages in real-time, and uses AI to detect spam, flag suspicious content, and evaluate sender trust. Built for developers and security-minded users, SentinelIQ combines Spring Boot, FastAPI, and Next.js into a seamless system for intelligent email screening.

---

## 💡 Features

- 📬 Gmail integration via OAuth
- 🧠 AI-driven spam detection with confidence scoring
- 🔍 Trust and intent analysis of incoming emails
- 🔐 Sender behavior profiling
- 📊 Admin dashboard for viewing flagged content
- 🔁 REST API for automated spam checks

---

## 🧱 Tech Stack

- **Spring Boot** – Backend email processor & API gateway
- **Python (FastAPI)** – Spam detection engine (ML)
- **Next.js (React)** – Frontend dashboard (inbox + insights)
- **Supabase/PostgreSQL** – User auth, email logs, spam verdicts
- **Tailwind CSS** – UI styling

---

## 🔧 Getting Started


- `/backend` — Spring Boot API for Gmail integration and FastAPI communication
- `/engine` — FastAPI-based ML engine for spam detection
- `/frontend` — Next.js app with Gmail login and inbox UI

> OAuth setup, API tokens, and deployment instructions coming soon 👀




🔄 System Flow Overview — SentinelIQ
1. 🧾 Sign Up (Frontend → Backend)

Frontend

User fills the form in SignUp.tsx.

Request

POST /api/auth/signup


Backend

Handled by AuthenticationController.registerUser

Password encoded using PasswordEncoder

Tokens created via:

JwtUtil.generateAccessToken

JwtUtil.generateRefreshToken

Sets cookies: access_token, refresh_token
📄 File: AuthenticationController.java

2. 🔐 Login

Frontend

User logs in via LogIn.tsx.

Request

POST /api/auth/login


Backend

Handled by AuthenticationController.loginUser

Verifies user credentials

Sets JWT cookies for session management
📄 File: AuthenticationController.java

3. 🧠 Frontend Session Check / Global Auth State

Frontend

Auth context checks session via:

GET /api/auth/check


(Implemented in AuthContext.tsx)

Auth state exposed through AuthProvider

Consumed by Navbar.tsx and page components
📄 File: AuthContext.tsx

4. 🗂️ Loading Dashboard & Inboxes

Frontend

DashboardView.tsx calls:

GET /api/me


Backend

Handled by UserController.getCurrentUser

Extracts JWT from access_token cookie

Returns user info + inbox list

Uses EmailAccountRepository.findAllByUser
📄 Files: UserController.java, EmailAccountRepository.java

5. 📧 Linking a Gmail Inbox (OAuth Flow)

Frontend

User clicks “Link Gmail” in DashboardView.tsx

Redirects to:

GET /auth/gmail


Backend
Step 1:
GmailOAuthController.startGmailOAuth builds the Google OAuth URL
📄 File: GmailOAuthController.java

Step 2:
After user consent, Google redirects to:

/auth/gmail/callback


Handled by GmailOAuthController.handleGmailCallback

Exchanges auth code for tokens

Saves an EmailAccount

Triggers initial email fetch
📄 File: GmailOAuthController.java

6. 📥 Fetching & Saving Gmail Messages

Triggered by GmailService.fetchAndSaveEmails

Converts Gmail messages → Email entities

Persists to database
📄 File: GmailService.java

7. 💬 Viewing Emails in the UI

Frontend

Dashboard requests:

GET /api/gmail/emails?inboxId=…


Components:

EmailList.tsx — lists emails

EmailCard.tsx — renders preview

EmailViewer.tsx — opens full email view

Backend

Handled by EmailController.java

8. 📄 Viewing a Single Email

Frontend

EmailViewer.tsx calls:

GET /api/gmail/emails/{id}


Uses credentials: 'include' to send cookies

Backend

Endpoint served by EmailController.java

9. 🔁 Manual Resync

Frontend

“Resync Inbox” button in EmailList.tsx or DashboardView.tsx
Sends:

POST /api/gmail/resync?inboxId=…


Backend

Handled by EmailController.resyncInbox

Calls GmailService.fetchAndSaveEmails
📄 Files: EmailController.java, GmailService.java

10. 🛡️ Auth Enforcement (Backend Filter)

Backend

Every incoming request passes through JwtAuthenticationFilter

Validates JWT via JwtUtil.validateToken

Sets SecurityContext for authenticated user
📄 Files: JwtAuthenticationFilter.java, JwtUtil.java

11. 🚪 Logout

Frontend

Navbar.tsx triggers:

POST /api/auth/logout


Backend

Handled by AuthenticationController.logoutUser
📄 Files: Navbar.tsx, AuthenticationController.java
---

## 🚀 Coming Soon

- 📥 Outlook and Yahoo integration
- 🛡️ Feedback loop to retrain spam model
- 📊 User-specific trust trend analytics
- 🌐 Chrome extension for live spam scanning

