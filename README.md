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

Each folder has its own README:

- `/backend` — Spring Boot API for Gmail integration and FastAPI communication
- `/engine` — FastAPI-based ML engine for spam detection
- `/frontend` — Next.js app with Gmail login and inbox UI

> OAuth setup, API tokens, and deployment instructions coming soon 👀

---

## 🚀 Coming Soon

- 📥 Outlook and Yahoo integration
- 🛡️ Feedback loop to retrain spam model
- 📊 User-specific trust trend analytics
- 🌐 Chrome extension for live spam scanning

