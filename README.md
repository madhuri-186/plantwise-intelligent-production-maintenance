# Intelligent Production & Predictive Maintenance Management System

This project has exactly two application folders: `frontend` and `backend`.

## Requirements
Java 17+, Maven 3.9+, Node.js 18+, and MySQL 8+.

## Database
Create the database with `CREATE DATABASE production_maintenance;`. Set `DB_USERNAME` and `DB_PASSWORD` in your environment, or edit `backend/src/main/resources/application.properties` locally. Never commit real passwords.

## Run backend
From `backend`: `mvn spring-boot:run`
Health endpoint: `http://localhost:8080/api/health`

## Run frontend
From `frontend`: `npm install` then `npm run dev`; open the URL shown by Vite (usually `http://localhost:5173`).

Demo login: `admin@plant.com` / `admin123`. This is development-only demo authentication, not production-grade JWT/database-backed authentication. The application includes database-backed machine, production, breakdown, and maintenance CRUD endpoints and a dashboard derived from records. Rule-based warnings only; no ML prediction is claimed. Verify builds and database connectivity on your machine before submission/deployment.
