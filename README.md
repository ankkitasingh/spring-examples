# Bank API System

A Spring Boot–based RESTful API for banking operations, product catalog management, and order processing — fully Dockerized and automatically deployed to AWS EC2 using GitHub Actions.

---

## 📘 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [API Endpoints](#api-endpoints)
  - [Bank APIs](#1-bank-apis)
  - [Catalog APIs](#2-catalog-apis)
  - [Order APIs](#3-order-apis)
- [Build & Test Workflow](#build--test-workflow)
- [Dockerization](#dockerization)
- [EC2 Deployment (GitHub Actions)](#ec2-deployment-github-actions)
- [Project Structure](#project-structure)
- [Environment Variables](#environment-variables)
- [Local Development](#local-development)

---

##  Overview

This project provides a simple banking API with endpoints for:

- Creating accounts and performing transfers  
- Managing product catalogs with summaries, filters, and top sellers  
- Placing and viewing user orders  

The application uses **Spring Boot**, **Spring Data JPA**, and **Maven**, with continuous integration and deployment through **GitHub Actions** to **AWS EC2**.

---

##  Architecture

```
Spring Boot (REST APIs)
│
├── Controllers (Bank, Catalog, Order)
├── Services (Business Logic)
├── Repository (JPA Data Access)
├── Models / DTOs
└── Docker + GitHub Actions CI/CD → AWS EC2
```

---

##  API Endpoints

###  Bank APIs
**Base URL:** `/api/bank`

| Method | Endpoint | Description | Request Body | Response |
|--------|-----------|--------------|---------------|-----------|
| POST | `/accounts` | Create a new account | `{ "owner": "John", "balance": "1000" }` | `Account` JSON |
| POST | `/transfer` | Transfer funds between accounts | `{ "fromId": "1", "toId": "2", "amount": "100", "failAfterDebit": "false" }` | `{ "status": "ok" }` |

---

###  Catalog APIs
**Base URL:** `/api/catalog`

| Method | Endpoint | Description | Params | Response |
|--------|-----------|--------------|---------|-----------|
| GET | `/summaries` | Paginated product summaries | `page`, `size`, `sort`, `category` | `Page<ProductSummary>` |
| GET | `/search` | Search products by filters | `category`, `min`, `max`, `page`, `size` | `Page<Product>` |
| GET | `/top-sellers` | Get top-selling products | `limit` | `List<TopSellerView>` |

---

###  Order APIs
**Base URL:** `/api/orders`

| Method | Endpoint | Description | Request Body | Response |
|--------|-----------|--------------|---------------|-----------|
| POST | `/` | Create an order | `{ "email": "user@mail.com", "productIds": [1,2,3] }` | `{ "orderId": 1001 }` |
| GET | `/user/{userId}` | Get orders by user | — | `List<Order>` |

---

##  Build & Test Workflow

GitHub Actions workflow (`.github/workflows/test.yml`) runs automatically on every push or PR to feature branches:

```yaml
name: ✅ Build & Test with Coverage

on:
  push:
    branches: [feature**]
  pull_request:
    branches: [main, develop, dev, feature**]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout repository
        uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: 'maven'
      - name: Run unit tests with JaCoCo
        run: mvn -B clean verify
        working-directory: ./api
      - name: Upload coverage report
        uses: actions/upload-artifact@v4
        with:
          name: jacoco-report
          path: api/target/site/jacoco/
```

This ensures code quality and coverage metrics are validated before merging.

---

##  Dockerization

The application is containerized using a `Dockerfile` and `docker-compose.yml`.

**Steps:**
1. Create a `Dockerfile` at the project root with the below code.
   
```dockerfile
# ===== build stage =====
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

# ===== runtime stage =====
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dfile.encoding=UTF-8 -Duser.timezone=UTC -XX:+ExitOnOutOfMemoryError"
COPY --from=build /build/target/*SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
```

2. Add a `.dockerignore` to exclude unnecessary files.
```
target
.git
.gitignore
.idea
*.iml
*.log

```   
3. Define a `docker-compose.yml` for multi-service management (e.g., app + DB).
```
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: ${DB_NAME}
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - pgdata:/var/lib/postgresql/data
    # Keep DB private: no ports published to the host
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USER} -d ${DB_NAME}"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

  app:
    build: .
    environment:
      # Profiles
      SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE}
      # DB settings consumed by application-prod.(properties|yml)
      DB_HOST: postgres
      DB_PORT: ${DB_PORT}
      DB_NAME: ${DB_NAME}
      DB_USER: ${DB_USER}
      DB_PASSWORD: ${DB_PASSWORD}
      DB_POOL_SIZE: ${DB_POOL_SIZE}
      PORT: ${PORT}
    depends_on:
      postgres:
        condition: service_healthy
    ports:
      - "${PORT:-8080}:8080"   # expose API publicly on HTTP port 80
    restart: unless-stopped

volumes:
  pgdata:

```

---

##  EC2 Deployment (GitHub Actions)

Automated deployment pipeline (defined in `.github/workflows/deploy.yml`):

1. Builds the Spring Boot JAR  
2. Creates a Docker image  
3. Copies it to AWS EC2 via SSH  
4. Runs the container

```
name: Deploy to EC2 

on:
  push:
    branches: ["main"]

jobs:
  deploy:
    runs-on: ubuntu-latest
    env:
      MODULE_DIR: api   # <— your module with the compose file

    steps:
      - name: Checkout repo
        uses: actions/checkout@v4

      - name: Show branch/commit info
        run: |
          echo "Branch: $GITHUB_REF_NAME"
          echo "Commit: $GITHUB_SHA"
          git log --oneline -n 3 || true

      - name: Setup SSH key (base64 secret)
        run: |
          set -euo pipefail
          mkdir -p ~/.ssh
          echo "${{ secrets.EC2_SSH_KEY_B64 }}" | base64 -d > ~/.ssh/gha-ec2
          sed -i 's/\r$//' ~/.ssh/gha-ec2
          chmod 600 ~/.ssh/gha-ec2
          ssh-keyscan -H "${{ secrets.EC2_HOST }}" >> ~/.ssh/known_hosts
          ssh -i ~/.ssh/gha-ec2 -o IdentitiesOnly=yes -o StrictHostKeyChecking=yes \
            "${{ secrets.EC2_USER }}"@"${{ secrets.EC2_HOST }}" "echo OK && hostname"

      - name: Ensure target dir on EC2
        run: |
          ssh -i ~/.ssh/gha-ec2 -o StrictHostKeyChecking=yes \
            "${{ secrets.EC2_USER }}"@"${{ secrets.EC2_HOST }}" \
            "mkdir -p '${{ secrets.EC2_TARGET_DIR }}' && ls -la '${{ secrets.EC2_TARGET_DIR }}'"

      - name: Rsync ONLY the api module to EC2 (exclude junk)
        run: |
          RSYNC_SSH="ssh -i ~/.ssh/gha-ec2 -o StrictHostKeyChecking=yes"
          rsync -avz --delete \
            --exclude '.git/' \
            --exclude '.idea/' \
            --exclude '.metadata/' \
            --exclude '.gradle/' \
            --exclude 'target/' \
            -e "$RSYNC_SSH" \
            "${{ env.MODULE_DIR }}/" \
            "${{ secrets.EC2_USER }}@${{ secrets.EC2_HOST }}:${{ secrets.EC2_TARGET_DIR }}/${{ env.MODULE_DIR }}/"

      - name: List files on EC2 (sanity)
        run: |
          ssh -i ~/.ssh/gha-ec2 -o StrictHostKeyChecking=yes \
            "${{ secrets.EC2_USER }}"@"${{ secrets.EC2_HOST }}" \
            "cd '${{ secrets.EC2_TARGET_DIR }}' && \
             echo '== tree (depth 2) =='; find . -maxdepth 2 -type f -print | sed 's|^\./||'"

      - name: Deploy on EC2 (compose from api/)
        run: |
          ssh -i ~/.ssh/gha-ec2 -o StrictHostKeyChecking=yes \
            "${{ secrets.EC2_USER }}"@"${{ secrets.EC2_HOST }}" \
            "set -euo pipefail; \
             BASE='${{ secrets.EC2_TARGET_DIR }}'; \
             MOD='${{ env.MODULE_DIR }}'; \
             COMP_DIR=\"\$BASE/\$MOD\"; \
             COMP_FILE=\"\$COMP_DIR/docker-compose.yml\"; \
             [ -f \"\$COMP_FILE\" ] || { echo 'ERROR: missing' \"\$COMP_FILE\"; exit 1; }; \
             if command -v docker-compose >/dev/null 2>&1; then DC='docker-compose'; else DC='docker compose'; fi; \
             echo '== Docker versions =='; docker --version; \$DC version; \
             echo '== Effective compose config =='; \$DC -f \"\$COMP_FILE\" --project-directory \"\$COMP_DIR\" config; \
             # Build all services defined in the compose file (or change to a specific service like 'app')
             echo '== Build images =='; \$DC -f \"\$COMP_FILE\" --project-directory \"\$COMP_DIR\" build --pull; \
             echo '== Up services =='; \$DC -f \"\$COMP_FILE\" --project-directory \"\$COMP_DIR\" up -d; \
             echo '== Compose ps =='; \$DC -f \"\$COMP_FILE\" --project-directory \"\$COMP_DIR\" ps; \
             echo '== Logs (last 100) =='; \$DC -f \"\$COMP_FILE\" --project-directory \"\$COMP_DIR\" logs --tail=100 || true; \
             echo '== Health check (optional) =='; (curl -fsS -m 10 http://localhost/actuator/health || true)"


```   

**Required GitHub Secrets:**
- `EC2_HOST` — Public IP or hostname of your EC2 instance  
- `EC2_SSH_KEY` — Private SSH key for secure access  

After successful deployment, SSH into your EC2 instance and run:
```bash
docker-compose ps
```
You should see your container running and accessible via your EC2 public URL.

---

##  Project Structure

```
api/
├── src/main/java/com/bank/api/
│   ├── controller/
│   │   ├── BankController.java
│   │   ├── CatalogController.java
│   │   └── OrderController.java
│   ├── model/
│   ├── repository/
│   └── services/
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

---

##  Environment Variables

| Variable | Description |
|-----------|--------------|
| `SPRING_DATASOURCE_URL` | JDBC connection URL |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `SERVER_PORT` | Application port  |

---

##  Local Development

```bash
# Clone repository
git clone https://github.com/<your-org>/<repo>.git

# Build & run tests
cd api
mvn clean verify

# Run locally
mvn spring-boot:run
```

Then access:
```
http://localhost:8080/api/bank/accounts
```

---

