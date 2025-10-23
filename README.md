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




🔄 SentinelIQ — System Flow Overview

This section describes the end-to-end flow of how SentinelIQ operates — from user authentication to Gmail integration and email management.
It connects the frontend (React/TypeScript) and backend (Spring Boot) components to show how data moves through the system.

1. 🧾 User Sign-Up (Frontend → Backend)
Frontend

User fills the registration form in SignUp.tsx.

The frontend sends a POST request to:

POST /api/auth/signup

Backend

Request handled by AuthenticationController.registerUser

The backend:

Encodes the user’s password with PasswordEncoder

Generates two JWTs via:

JwtUtil.generateAccessToken

JwtUtil.generateRefreshToken

Sets both tokens as secure cookies: access_token, refresh_token

Response includes the new user and authentication cookies.
📄 File: AuthenticationController.java

2. 🔐 Login Flow
Frontend

User logs in through LogIn.tsx.

The frontend sends:

POST /api/auth/login

Backend

Handled by AuthenticationController.loginUser

Validates user credentials

On success:

Creates JWT tokens (access + refresh)

Sets authentication cookies for session tracking
📄 File: AuthenticationController.java

3. 🧠 Session Validation & Global Auth State
Frontend

The app maintains a global authentication context defined in AuthContext.tsx.

On app load or page refresh, it calls:

GET /api/auth/check


to validate the session using stored cookies.

Auth state is:

Managed by AuthProvider

Consumed by Navbar.tsx and page-level components to show logged-in state
📄 File: AuthContext.tsx

4. 🗂️ Dashboard & Inbox Loading
Frontend

The dashboard view (DashboardView.tsx) fetches user details and inboxes using:

GET /api/me

Backend

Handled by UserController.getCurrentUser

Extracts user data from the access_token cookie

Returns:

User profile info

Connected inbox list

Retrieves inboxes via EmailAccountRepository.findAllByUser
📄 Files: UserController.java, EmailAccountRepository.java

5. 📧 Linking a Gmail Inbox (OAuth 2.0 Flow)
Frontend

User clicks “Link Gmail” in DashboardView.tsx.

Redirects to:

GET /auth/gmail

Backend

Step 1:

GmailOAuthController.startGmailOAuth constructs the Google OAuth URL and redirects the user for consent.
📄 File: GmailOAuthController.java

Step 2:

After consent, Google redirects to:

/auth/gmail/callback


Handled by GmailOAuthController.handleGmailCallback which:

Exchanges the OAuth code for tokens

Saves an EmailAccount linked to the user

Triggers an initial email sync
📄 File: GmailOAuthController.java

6. 📥 Fetching & Persisting Gmail Messages

Triggered by GmailService.fetchAndSaveEmails

Fetches Gmail messages using the connected account tokens

Converts raw Gmail data into Email entities

Persists them in the database for retrieval and analysis
📄 File: GmailService.java

7. 💬 Email Retrieval (Inbox View)
Frontend

Dashboard requests user emails:

GET /api/gmail/emails?inboxId=…


Components:

EmailList.tsx → Renders email list view

EmailCard.tsx → Displays email previews

EmailViewer.tsx → Opens full email content

Backend

Endpoints handled by EmailController

Fetches emails linked to the user’s inbox and returns JSON responses
📄 File: EmailController.java

8. 📄 Viewing a Single Email
Frontend

EmailViewer.tsx requests a specific email:

GET /api/gmail/emails/{id}


Uses credentials: 'include' to send cookies for authentication.

Backend

Served by EmailController.getEmailById

Returns the full email details and metadata
📄 File: EmailController.java

9. 🔁 Manual Inbox Resync
Frontend

“Resync Inbox” button in EmailList.tsx or DashboardView.tsx triggers:

POST /api/gmail/resync?inboxId=…

Backend

Handled by EmailController.resyncInbox

Calls GmailService.fetchAndSaveEmails to pull new emails from Gmail and update the database
📄 Files: EmailController.java, GmailService.java

10. 🛡️ Authentication Enforcement (JWT Filter)
Backend

All requests are filtered through:

JwtAuthenticationFilter

JwtUtil

Responsibilities:

Extract JWT from cookies

Validate token integrity and expiration

Set authenticated user context (SecurityContext) for downstream controllers
📄 Files: JwtAuthenticationFilter.java, JwtUtil.java

11. 🚪 Logout Flow
Frontend

Navbar.tsx triggers logout by calling:

POST /api/auth/logout

Backend

Handled by AuthenticationController.logoutUser

Clears cookies and invalidates tokens to end the session
📄 Files: Navbar.tsx, AuthenticationController.java






## 🚀 Coming Soon

- 📥 Outlook and Yahoo integration
- 🛡️ Feedback loop to retrain spam model
- 📊 User-specific trust trend analytics
- 🌐 Chrome extension for live spam scanning

