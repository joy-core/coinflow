# CoinFlow

CoinFlow is a personal finance and expense tracking application. The backend is built with Java 21 and Spring Boot 3.5, featuring JWT auth, bills, asset accounts, categories, budgets, templates, recurring bills, and reports. The frontend uses Vue 3, TypeScript, and Element Plus for financial management and data visualization.

## Project Structure

```
coinflow/
├── coinflow/          # Backend — Spring Boot 3.5 + Java 21 + MyBatis + MySQL
│   └── README.md      # Backend documentation
└── coinflow-web/      # Frontend — Vue 3 + TypeScript + Vite + Element Plus
    └── README.md      # Frontend documentation
```

## Quick Start

### Backend

```bash
cd coinflow

# 1. Create MySQL database and import schema
mysql -u root -p coinflow < db/coinflow.sql

# 2. Run
./mvnw spring-boot:run
```

The server starts on `http://localhost:8080`.

### Frontend

```bash
cd coinflow-web

# 1. Install dependencies
npm install

# 2. Start dev server (requires backend running)
npm run dev
```

The app runs on `http://localhost:5173`. API requests to `/api/*` are proxied to `localhost:8080`.

### Production Build

```bash
# Backend
cd coinflow && ./mvnw clean package
java -jar target/coin-flow-0.0.1-SNAPSHOT.jar

# Frontend
cd coinflow-web && npm run build
```

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 21, Spring Boot 3.5, MyBatis, MySQL 5.7, Spring Security + JWT, Lombok |
| Frontend | TypeScript, Vue 3 (Composition API), Vite 6, Element Plus, Axios, Vue Router 4, dayjs |

## Features

- User registration, login, and JWT authentication
- Bill management — income and expense records
- Category system with parent-child hierarchy
- Asset accounts (cash, bank, Alipay, WeChat, credit card)
- Monthly budget management per category
- Recurring bill scheduling (daily, weekly, monthly, yearly)
- Bill templates for quick entry
- Account book management — multiple notebooks for organizing finances
- Financial reports and statistics with visual charts
- Theme customization

## API

All backend endpoints return JSON wrapped in a standard `R<T>` response format:

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

Authentication is handled via JWT Bearer tokens.
