# CookSmart AI — Personal Cooking To-Do Planner

Hackathon-ready full-stack app that generates a personalized daily cooking plan: breakfast, lunch, dinner, grocery list (missing items only), ingredient substitutions, budget feasibility, and a simple cooking timeline.

## Deploy to Render (fastest path)

One Docker image serves both the UI and API.

1. Push this repo to GitHub.
2. Go to [https://dashboard.render.com](https://dashboard.render.com) → **New** → **Blueprint**.
3. Connect the repo (uses `render.yaml`).
4. Apply. Wait for the free Postgres + web service to finish building (~8–12 min first time).
5. Open the service URL — the app is live at `/`, API at `/api/v1/...`, Swagger at `/swagger-ui.html`.

Optional: set `OPENAI_API_KEY` in the Render dashboard for live LLM plans. Without it, the built-in planner still works.

### Push to GitHub

```bash
cd cooksmart-ai
git init
git add .
git commit -m "Initial CookSmart AI app"
gh auth login
gh repo create cooksmart-ai --public --source=. --remote=origin --push
```

Then connect that repo in Render Blueprint.

---

## Tech Stack

| Layer | Technology |
| --- | --- |
| Frontend | React 19, Vite, TypeScript, Tailwind CSS v4 |
| Backend | Spring Boot 3.4, Java 17, layered architecture |
| Database | PostgreSQL 16 |
| Docs | OpenAPI / Swagger UI (`springdoc`) |
| Containers | Docker + Docker Compose |
| AI | OpenAI-compatible chat API (optional) with rule-based fallback |

## Architecture

```
Browser (React)
    │  REST /api/v1/*
    ▼
Controller  →  Service  →  Repository  →  PostgreSQL
                 │
                 ├─ AiMealPlannerService (LLM when OPENAI_API_KEY is set)
                 └─ RuleBasedMealPlannerService (always available fallback)
```

- **Controller** — HTTP mapping, Bean Validation (`@Valid`)
- **Service** — meal generation, budget analysis, mapping, persistence orchestration
- **Repository** — Spring Data JPA
- **Exception handling** — centralized `GlobalExceptionHandler`
- **Config** — environment variables via `application.yml` / Compose

## Project Structure

```
cooksmart-ai/
├── backend/                 # Spring Boot API
│   ├── src/main/java/com/cooksmart/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── dto/
│   │   ├── exception/
│   │   ├── enums/
│   │   └── config/
│   └── src/test/java/       # Service-layer unit tests
├── frontend/                # React + Vite UI
├── docker-compose.yml
├── .env.example
└── cooksmart.brd.md
```

## Quick Start (Docker Compose)

**Prerequisites:** Docker Desktop (or Docker Engine + Compose).

```bash
cp .env.example .env
# Optional: set OPENAI_API_KEY in .env for live LLM planning

docker compose up --build
```

| Service | URL |
| --- | --- |
| App UI | http://localhost:3000 |
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/api-docs |
| PostgreSQL | localhost:5432 |

The UI proxies `/api` to the backend inside Compose via Nginx, so the browser can call relative `/api/...` paths.

Without an API key, the app still works using the built-in rule-based meal catalog — ideal for demos.

## Local Development (without full Compose)

### 1. Database

```bash
docker compose up -d db
```

### 2. Backend

Requires JDK 17+ and Maven 3.9+.

```bash
cd backend
export DATABASE_URL=jdbc:postgresql://localhost:5432/cooksmart
export DATABASE_USERNAME=cooksmart
export DATABASE_PASSWORD=cooksmart
export CORS_ALLOWED_ORIGINS=http://localhost:5173
# optional:
# export OPENAI_API_KEY=sk-...

mvn spring-boot:run
```

Run tests:

```bash
cd backend
mvn test
```

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Vite runs at http://localhost:5173 and proxies `/api` to `http://localhost:8080`.

## Environment Variables

| Variable | Default | Description |
| --- | --- | --- |
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/cooksmart` | JDBC URL |
| `DATABASE_USERNAME` | `cooksmart` | DB user |
| `DATABASE_PASSWORD` | `cooksmart` | DB password |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,...` | Comma-separated origins |
| `OPENAI_API_KEY` | _(empty)_ | Enables LLM planner when set |
| `OPENAI_BASE_URL` | `https://api.openai.com/v1` | OpenAI-compatible base URL |
| `OPENAI_MODEL` | `gpt-4o-mini` | Chat model |
| `AI_ENABLED` | `true` | Set `false` to force rule-based planner |
| `CURRENCY_SYMBOL` | `₹` | Display currency |
| `SERVER_PORT` | `8080` | Backend port |
| `VITE_API_BASE_URL` | _(empty)_ | Frontend API prefix (leave empty when Nginx proxies `/api`) |
| `LOG_LEVEL` | `INFO` | `com.cooksmart` log level |

Copy `.env.example` → `.env` and adjust. **Never commit real API keys.**

## API Documentation

Interactive docs: [Swagger UI](http://localhost:8080/swagger-ui.html)

### `GET /api/v1/health`

Liveness check.

### `POST /api/v1/meal-plans`

Generate and persist a daily plan.

**Request body**

```json
{
  "peopleCount": 2,
  "diet": "Vegetarian",
  "cuisine": "Indian",
  "cookingTime": "15–30 min",
  "budget": 700,
  "availableIngredients": ["Rice", "Eggs", "Tomatoes", "Onions", "Milk"],
  "allergies": ["Nuts"]
}
```

**Validation**

- `peopleCount` ≥ 1
- `budget` > 0
- `diet` and `cookingTime` required
- `cuisine` optional (defaults to `Any`)
- ingredient names cannot be blank

**Response (201)** includes meals, grocery list, substitutions, budget analysis, and cooking timeline.

### `GET /api/v1/meal-plans/{id}`

Fetch a saved plan.

### `GET /api/v1/meal-plans?limit=10`

List recent plans.

### Example `curl`

```bash
curl -s -X POST http://localhost:8080/api/v1/meal-plans \
  -H 'Content-Type: application/json' \
  -d '{
    "peopleCount": 2,
    "diet": "Vegetarian",
    "cuisine": "Indian",
    "cookingTime": "15–30 min",
    "budget": 700,
    "availableIngredients": ["Rice", "Onion"],
    "allergies": []
  }' | jq
```

## User Flow

1. Landing hero → **Plan today's meals**
2. Enter preferences (people, diet, cuisine, time, budget, ingredients, allergies)
3. Generate plan
4. Review breakfast / lunch / dinner
5. Check grocery checklist, substitutions, budget status, and timeline

## Security Notes

- API keys and DB credentials come from environment variables only
- Input validation on all write endpoints
- CORS restricted to configured origins
- Backend runs as a non-root user in Docker
- Generic 500 messages; validation details returned only for 400s

## Deployment

### Docker Compose (local / VM)

```bash
docker compose up --build -d
```

### Render (suggested)

1. Provision a **PostgreSQL** instance.
2. Deploy **backend** as a Docker web service from `backend/`, setting `DATABASE_*`, `CORS_ALLOWED_ORIGINS`, and optional `OPENAI_API_KEY`.
3. Deploy **frontend** as a static/Docker service; set `VITE_API_BASE_URL` to your backend public URL at build time, **or** put Nginx/CDN in front and proxy `/api` to the backend (same pattern as `frontend/nginx.conf`).

### Production checklist

- [ ] Strong `POSTGRES_PASSWORD`
- [ ] Restrict `CORS_ALLOWED_ORIGINS` to your real domain
- [ ] Provide `OPENAI_API_KEY` only via secret store
- [ ] Enable HTTPS at the reverse proxy / platform layer
- [ ] Set `LOG_LEVEL=INFO` (or `WARN`) in production

## Testing

```bash
cd backend && mvn test
```

Coverage focuses on the service layer:

- `MealPlanServiceTest` — persistence mapping, budget status, sanitization, not-found
- `RuleBasedMealPlannerServiceTest` — diet/allergy filtering, grocery exclusion, cost scaling

## Assumptions

1. **Currency** defaults to Indian Rupee (`₹`); override with `CURRENCY_SYMBOL`.
2. **AI is optional.** With no `OPENAI_API_KEY`, a curated rule-based catalog generates plans so demos never block on network/LLM failures.
3. **Costs are estimates** for planning UX, not live market prices.
4. **Available ingredients** are matched loosely (case-insensitive substring) when building the grocery list.
5. **Cuisine** is optional and defaults to `Any`.
6. **Stretch goals** from the BRD (weekly planner, nutrition, PDF export, share links, voice input) are intentionally out of scope for this MVP.
7. Schema is managed with JPA `ddl-auto=update` for hackathon speed; use Flyway/Liquibase for long-lived production schemas.

## License

MIT — built for hackathon demonstration and learning.
