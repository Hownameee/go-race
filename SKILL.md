# 🚀 GLOBAL_SKILL.md - Workspace Root

This file serves as the primary rulebook and high-level context for the "Antigravity" AI agent working on this project. 

---

## 1. Project Overview
**Concept:** An "All-in-One" Sports Social Network. 
**The Problem:** Athletes currently use scattered tools (one app for GPS tracking, another for route building, and a different platform for community chat). Furthermore, sports clubs lack specialized tools to manage members, organize events, and track collective achievements transparently.
**The Solution:** A unified platform combining deep GPS tracking tools with a robust social network to break down the barriers between individual training and community movement.

**Target Audience:**
* **Individuals:** Amateur to professional runners and cyclists looking to track performance and find a community.
* **Organizations/Clubs:** Runclubs, companies, or groups wanting to manage internal sports movements, events, and leaderboards.

---

## 2. Core Pillars & Features

### Pillar A: Smart Tracking & Navigation (Core)
* **Map & Tracking:** Real-time GPS tracking to record time, distance, and visualize the actual route on a map.
* **Route Builder:** Allows users to design training routes in advance or discover popular community routes.
* **History & Analytics:** Detailed workout history for personal progress tracking.

### Pillar B: Community Connection (Social)
* **Club Ecosystem:** Create and join clubs. Admins can manage members, create private events, and build professional club profiles.
* **Events & Challenges:** System to organize virtual races or group challenges to boost competition.
* **Social Interaction:** Newsfeed to share achievements, interact with friends, and build a localized sports network.

### Pillar C: Utilities & System
* **Cross-Platform Auth:** Quick login via Google/Facebook.
* **Notifications & Mail:** Automated Push Notifications and Emails for important events, schedules, or club invitations.

---

## 3. Technology Stack (Context)
* **Frontend:** Android Mobile App (Java), structure in module, use mvvm, offline first.
* **Backend:** Node.js (Express).
* **Database:** SQLite (with FTS5 for search optimizations).

---

## 4. STRICT CONVENTIONS (CRITICAL RULES FOR AI)

Read and adhere to these rules before generating any code or executing any commands:

* **RULE 1 - NO AUTOMATIC COMMITS:** You must **NEVER** execute `git commit` or `git push` commands. The human developer wants to review all code and commit manually. You may suggest git commands in text, but do not run them.
* **RULE 2 - LANGUAGE:** All business logic comments, code documentation, variable names, and function names **MUST** be written in **English**. 
* **RULE 3 - ISOLATION:** Respect directory boundaries. Backend code goes in the backend folder, Android code goes in the Android folder. 
* **RULE 4 - CLARITY OVER CLEVERNESS:** Write clean, readable code. Avoid overly complex one-liners if a standard implementation is easier for a human to read and maintain.