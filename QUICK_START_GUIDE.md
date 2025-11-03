# Quick Start Guide - Updated Configuration

This guide will help you quickly get started with the updated LoveDev microservices project.

---

## Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL 14+ (or use Docker)
- Redis (or use Docker)
- Kafka (or use Docker)

---

## Option 1: Full Docker Deployment (Recommended for Production Testing)

### Step 1: Prepare Environment

```bash
# Navigate to project root
cd /path/to/lovedev_production_complete

# The .env file is already configured for Docker/Production
# Review and update credentials as needed
nano .env
```

### Step 2: Update Security Credentials

**IMPORTANT:** Update these in `.env` before starting:

```bash
# Database
DB_PASSWORD=your_secure_password

# Email
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-gmail-app-password

# OAuth2
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
GITHUB_CLIENT_ID=your-github-client-id
GITHUB_CLIENT_SECRET=your-github-client-secret

# JWT
JWT_SECRET=your-secure-jwt-secret-key
```

### Step 3: Create Databases

```bash
# Start PostgreSQL
docker-compose up -d postgres

# Wait for PostgreSQL to be ready
sleep 10

# Create databases
docker exec -it postgres psql -U postgres -c "CREATE DATABASE lovedev_db;"
docker exec -it postgres psql -U postgres -c "CREATE DATABASE notification_db;"
```

### Step 4: Start All Services

```bash
# Start infrastructure services
docker-compose up -d redis kafka zookeeper

# Wait for services to be ready
sleep 20

# Start application services
docker-compose up -d config-server eureka-server
sleep 30

# Start business services
docker-compose up -d user-service email-service notification-service business-service
sleep 20

# Start API Gateway
docker-compose up -d api-gateway

# Check all services are running
docker-compose ps
```

### Step 5: Verify Services

```bash
# Check Eureka Dashboard
open http://localhost:8761

# Check API Gateway
curl http://localhost:8000/actuator/health

# Check individual services
curl http://localhost:8081/actuator/health  # User Service
curl http://localhost:8082/actuator/health  # Email Service
curl http://localhost:8083/actuator/health  # Notification Service
curl http://localhost:8084/actuator/health  # Business Service
```

---

## Option 2: Local Development (IDE)

### Step 1: Prepare Development Environment

```bash
# Navigate to project root
cd /path/to/lovedev_production_complete

# Copy development environment file
cp .env.local .env

# Or set Spring profile
export SPRING_PROFILES_ACTIVE=dev
```

### Step 2: Update Development Credentials

Edit `.env` (or `.env.local`):

```bash
# Database (local)
DB_USERNAME=postgres
DB_PASSWORD=postgres

# Email
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-gmail-app-password
```

### Step 3: Start Infrastructure Services

```bash
# Start PostgreSQL, Redis, Kafka with Docker
docker-compose up -d postgres redis kafka zookeeper

# Wait for services to be ready
sleep 15

# Create databases
docker exec -it postgres psql -U postgres -c "CREATE DATABASE lovedev_db;"
docker exec -it postgres psql -U postgres -c "CREATE DATABASE notification_db;"
```

### Step 4: Start Services in Order

**Terminal 1: Config Server**
```bash
cd config-server
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Terminal 2: Eureka Server**
```bash
cd eureka-server
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Wait for Eureka to start (about 30 seconds), then:

**Terminal 3: User Service**
```bash
cd user-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Terminal 4: Email Service**
```bash
cd email-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Terminal 5: Notification Service**
```bash
cd notification-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Terminal 6: Business Service**
```bash
cd business-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Terminal 7: API Gateway**
```bash
cd api-gateway
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Step 5: Verify Local Setup

```bash
# Check Eureka Dashboard
open http://localhost:8761

# All services should appear in Eureka within 1-2 minutes
```

---

## Option 3: Hybrid Approach

Run infrastructure in Docker, services locally:

```bash
# Start infrastructure
docker-compose up -d postgres redis kafka zookeeper

# Start Eureka and Config Server in Docker
docker-compose up -d config-server eureka-server

# Then run business services locally in your IDE
```

---

## Service Ports Reference

| Service | Port | URL |
|---------|------|-----|
| API Gateway | 8000 | http://localhost:8000 |
| User Service | 8081 | http://localhost:8081 |
| Email Service | 8082 | http://localhost:8082 |
| Notification Service | 8083 | http://localhost:8083 |
| Business Service | 8084 | http://localhost:8084 |
| Eureka Server | 8761 | http://localhost:8761 |
| Config Server | 8888 | http://localhost:8888 |

---

## Testing the Services

### 1. Check Service Registration

Visit Eureka Dashboard: http://localhost:8761

All services should appear within 1-2 minutes.

### 2. Test User Registration

```bash
curl -X POST http://localhost:8000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test123!@#",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### 3. Test User Login

```bash
curl -X POST http://localhost:8000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test123!@#"
  }'
```

### 4. Test Email Service

```bash
# Get JWT token from login response first
TOKEN="your-jwt-token"

curl -X POST http://localhost:8000/api/emails/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "to": "recipient@example.com",
    "subject": "Test Email",
    "body": "This is a test email"
  }'
```

### 5. Access Swagger UI

- **User Service:** http://localhost:8081/swagger-ui.html
- **Email Service:** http://localhost:8082/swagger-ui.html
- **Notification Service:** http://localhost:8083/swagger-ui.html
- **API Gateway:** http://localhost:8000/swagger-ui.html (aggregated)

---

## Monitoring

### Eureka Dashboard
http://localhost:8761
- View all registered services
- Check service health status

### Actuator Endpoints

Development (all endpoints exposed):
```bash
# Health check
curl http://localhost:8081/actuator/health

# Metrics
curl http://localhost:8081/actuator/metrics

# Environment
curl http://localhost:8081/actuator/env

# All endpoints
curl http://localhost:8081/actuator
```

Production (limited endpoints):
```bash
# Health check
curl http://localhost:8081/actuator/health

# Info
curl http://localhost:8081/actuator/info

# Metrics
curl http://localhost:8081/actuator/metrics
```

---

## Troubleshooting

### Services Not Starting

**Check logs:**
```bash
# Docker
docker-compose logs -f service-name

# Local
# Check the terminal where service is running
```

**Common issues:**
1. Port already in use - Check with `netstat -an | grep PORT`
2. Database not ready - Wait longer before starting services
3. Eureka not ready - Wait for Eureka before starting business services

### Services Not Registering with Eureka

**Check:**
1. Eureka Server is running: http://localhost:8761
2. Service has correct `EUREKA_SERVER_URL` in environment
3. Network connectivity (for Docker, check bridge network)

**Solution:**
```bash
# Restart the service
docker-compose restart service-name

# Or if running locally, stop and restart the service
```

### Database Connection Errors

**Check:**
1. PostgreSQL is running: `docker ps | grep postgres`
2. Database exists: `docker exec -it postgres psql -U postgres -l`
3. Credentials match in .env

**Create database if missing:**
```bash
docker exec -it postgres psql -U postgres -c "CREATE DATABASE lovedev_db;"
docker exec -it postgres psql -U postgres -c "CREATE DATABASE notification_db;"
```

### Email Not Sending

**Check:**
1. SMTP credentials are correct
2. Using Gmail App Password (not regular password)
3. Email service is running
4. Check logs for SMTP errors

**Test SMTP:**
```bash
# Check email service logs
docker-compose logs email-service | grep -i smtp
```

### OAuth2 Login Fails

**Check:**
1. OAuth credentials are correct in .env
2. Redirect URIs match in OAuth provider console
3. Frontend URL matches `OAUTH2_REDIRECT_URI`

### Redis Connection Issues

```bash
# Check Redis is running
docker ps | grep redis

# Test Redis connection
docker exec -it redis redis-cli ping
# Should return: PONG
```

### Kafka Issues

```bash
# Check Kafka is running
docker ps | grep kafka

# Check Kafka topics
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092
```

---

## Development Tips

### Hot Reload (Spring DevTools)

Services have Spring DevTools configured for hot reload during development.

### Profile Switching

Switch between dev and prod:
```bash
# Development
export SPRING_PROFILES_ACTIVE=dev

# Production
export SPRING_PROFILES_ACTIVE=prod

# Or in application properties
-Dspring.profiles.active=dev
```

### Database Management

**View tables:**
```bash
docker exec -it postgres psql -U postgres -d lovedev_db -c "\dt"
```

**Reset database:**
```bash
docker exec -it postgres psql -U postgres -c "DROP DATABASE lovedev_db;"
docker exec -it postgres psql -U postgres -c "CREATE DATABASE lovedev_db;"
# Restart user-service to run Flyway migrations
```

### Logs Location

**Docker:**
```bash
docker-compose logs -f service-name
```

**Local:**
- Check `logs/` directory in project root
- Each service logs to: `logs/service-name.log`

---

## Stopping Services

### Docker:
```bash
# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Stop specific service
docker-compose stop service-name
```

### Local:
```bash
# Press Ctrl+C in each terminal

# Or if using start-all.sh script
./stop-all.sh
```

---

## Next Steps

1. **Configure Frontend:**
   - Update API URL to `http://localhost:8000`
   - Update OAuth redirect URIs

2. **Set Up Monitoring:**
   - Configure Grafana dashboards
   - Set up Prometheus metrics collection
   - Enable Zipkin tracing

3. **Production Deployment:**
   - Set up proper domain names
   - Configure SSL/TLS certificates
   - Set up load balancer
   - Configure production database with backups
   - Set up log aggregation

4. **Security Hardening:**
   - Change all default passwords
   - Use secrets management (Vault, AWS Secrets Manager)
   - Enable HTTPS only
   - Configure firewall rules
   - Set up API rate limiting

---

## Quick Commands Reference

```bash
# Start everything (Docker)
docker-compose up -d

# View logs
docker-compose logs -f service-name

# Restart a service
docker-compose restart service-name

# Check service health
curl http://localhost:PORT/actuator/health

# View Eureka
open http://localhost:8761

# Stop everything
docker-compose down

# Database shell
docker exec -it postgres psql -U postgres -d lovedev_db

# Redis shell
docker exec -it redis redis-cli

# Kafka topics
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092
```

---

## Support

If you encounter issues:
1. Check the logs first
2. Verify all environment variables are set
3. Ensure services start in correct order
4. Check network connectivity
5. Review the Troubleshooting section above

---

**Your microservices architecture is now properly configured and ready to run! 🚀**
