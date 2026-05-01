# 🔧 ReleasePilot — DevOps Release Management System

A comprehensive release management platform for DevOps teams to track application versions, manage deployment pipelines, perform rollbacks, and maintain audit trails.

## 🏗️ Architecture

| Layer | Technology |
|-------|-----------|
| **Backend** | Spring Boot 3.2, Spring Security, Spring Data JPA |
| **Frontend** | React 18, Vite, React Router |
| **Database** | PostgreSQL 16 |
| **Auth** | JWT (JSON Web Tokens) with BCrypt |
| **Infra** | Docker, Docker Compose |

## ✨ Features

- **🔐 Authentication & RBAC** — JWT-based auth with Admin and Developer roles
- **📁 Project Management** — CRUD operations for application projects
- **🏷️ Release Tracking** — Version history with release notes
- **🚀 Deployment Pipeline** — Deploy to Development, Staging, or Production
- **↩️ Rollback** — One-click rollback for any deployment
- **📋 Audit Logs** — Complete history of all system actions
- **📊 Dashboard** — Real-time deployment stats and activity feed

## 🚀 Quick Start (Docker)

```bash
# Clone and start all services
docker-compose up --build

# Services will be available at:
# Frontend: http://localhost:5173
# Backend:  http://localhost:8080
# Postgres: localhost:5432
```

## 👤 Default Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Developer | `developer` | `dev123` |

## 📡 API Endpoints

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login & get JWT |

### Projects
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/projects` | List all projects |
| POST | `/api/projects` | Create project |
| PUT | `/api/projects/:id` | Update project |
| DELETE | `/api/projects/:id` | Delete (Admin only) |

### Releases
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/releases` | List all releases |
| GET | `/api/releases/project/:id` | Releases by project |
| POST | `/api/releases` | Create release |

### Deployments
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/deployments` | List all deployments |
| POST | `/api/deployments` | Deploy a release |
| POST | `/api/deployments/:id/rollback` | Rollback deployment |
| GET | `/api/deployments/stats` | Deployment statistics |

### Audit Logs
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/audit-logs` | All audit logs |

## 🛠️ Local Development

### Backend
```bash
cd backend
./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

## 📂 Project Structure

```
├── docker-compose.yml
├── backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/devops/releasemgmt/
│       ├── config/        # Security, JWT, CORS, Seeder
│       ├── controller/    # REST API endpoints
│       ├── dto/           # Request/Response objects
│       ├── entity/        # JPA entities
│       ├── repository/    # Data access layer
│       └── service/       # Business logic
├── frontend/
│   ├── Dockerfile
│   ├── nginx.conf
│   └── src/
│       ├── components/    # Layout, shared UI
│       ├── context/       # Auth context
│       ├── pages/         # Dashboard, Projects, Releases...
│       └── services/      # API client
└── README.md
```

## 📄 License

MIT
