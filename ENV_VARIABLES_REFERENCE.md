# Environment Variables Quick Reference

This document provides a quick reference for all environment variables used in the LoveDev microservices project.

---

## Database Configuration

### Main Database (User Service, Business Service)
| Variable | Description | Dev Default | Prod Default |
|----------|-------------|-------------|--------------|
| `DB_HOST` | Database hostname | localhost | postgres |
| `DB_PORT` | Database port | 5432 | 5432 |
| `DB_NAME` | Database name | lovedev_db | lovedev_db |
| `DB_USERNAME` | Database username | postgres | postgres |
| `DB_PASSWORD` | Database password | postgres | root |
| `DATABASE_URL` | Full JDBC URL | jdbc:postgresql://localhost:5432/lovedev_db | jdbc:postgresql://postgres:5432/lovedev_db |
| `DATABASE_USERNAME` | Alias for DB_USERNAME | postgres | postgres |
| `DATABASE_PASSWORD` | Alias for DB_PASSWORD | postgres | root |

### Notification Service Database
| Variable | Description | Dev Default | Prod Default |
|----------|-------------|-------------|--------------|
| `NOTIFICATION_DATABASE_URL` | Notification DB JDBC URL | jdbc:postgresql://localhost:5432/notification_db | jdbc:postgresql://postgres:5432/notification_db |
| `NOTIFICATION_DATABASE_USERNAME` | Notification DB username | postgres | postgres |
| `NOTIFICATION_DATABASE_PASSWORD` | Notification DB password | postgres | root |

---

## Redis Configuration

| Variable | Description | Dev Default | Prod Default |
|----------|-------------|-------------|--------------|
| `REDIS_HOST` | Redis hostname | localhost | redis |
| `REDIS_PORT` | Redis port | 6379 | 6379 |
| `REDIS_PASSWORD` | Redis password (optional) | (empty) | (empty) |

---

## Kafka Configuration

| Variable | Description | Dev Default | Prod Default |
|----------|-------------|-------------|--------------|
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka broker addresses | localhost:9092 | kafka:29092 |

---

## JWT Configuration

| Variable | Description | Default |
|----------|-------------|---------|
| `JWT_SECRET` | Secret key for JWT signing | 404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970 |
| `JWT_EXPIRATION` | JWT token expiration (ms) | 86400000 (24 hours) |
| `JWT_REFRESH_EXPIRATION` | Refresh token expiration (ms) | 604800000 (7 days) |

**Note:** Change `JWT_SECRET` in production!

---

## Email Configuration

### SMTP Settings
| Variable | Description | Default |
|----------|-------------|---------|
| `MAIL_HOST` | SMTP server hostname | smtp.gmail.com |
| `MAIL_PORT` | SMTP server port | 587 |
| `MAIL_USERNAME` | SMTP username/email | your-email@gmail.com |
| `MAIL_PASSWORD` | SMTP password/app password | your-app-password |
| `MAIL_AUTH` | Enable SMTP authentication | true |
| `MAIL_ENABLE` | Enable STARTTLS | true |

### Email Service Settings
| Variable | Description | Dev Default | Prod Default |
|----------|-------------|-------------|--------------|
| `EMAIL_FROM` | Sender email address | dev@lovedev.com | noreply@lovedev.com |
| `EMAIL_FROM_NAME` | Sender display name | LoveDev Team (Dev) | LoveDev Team |
| `EMAIL_ENABLED` | Enable email sending | true | true |
| `EMAIL_VERIFICATION_URL` | Email verification link | http://localhost:3000/verify-email | http://localhost:3000/verify-email |
| `EMAIL_RESET_PASSWORD_URL` | Password reset link | http://localhost:3000/reset-password | http://localhost:3000/reset-password |

**Required Actions:**
- Replace `MAIL_USERNAME` and `MAIL_PASSWORD` with your Gmail credentials
- For Gmail, use an App Password (not your regular password)
- Update `EMAIL_VERIFICATION_URL` and `EMAIL_RESET_PASSWORD_URL` for production

---

## OAuth2 Configuration

### Google OAuth
| Variable | Description | Default |
|----------|-------------|---------|
| `GOOGLE_CLIENT_ID` | Google OAuth client ID | 363168316067-jiv1kmlc2r641ma4hrnp7vesgcllojq2.apps.googleusercontent.com |
| `GOOGLE_CLIENT_SECRET` | Google OAuth client secret | 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 |

### Facebook OAuth
| Variable | Description | Default |
|----------|-------------|---------|
| `FACEBOOK_CLIENT_ID` | Facebook OAuth client ID | Ov23liAskp0pGyjGUcu9 |
| `FACEBOOK_CLIENT_SECRET` | Facebook OAuth client secret | d44ad9afb08dc49f4f7f3853391d338c42f37d55 |

### GitHub OAuth
| Variable | Description | Default |
|----------|-------------|---------|
| `GITHUB_CLIENT_ID` | GitHub OAuth client ID | your-github-client-id |
| `GITHUB_CLIENT_SECRET` | GitHub OAuth client secret | your-github-client-secret |

### General OAuth
| Variable | Description | Default |
|----------|-------------|---------|
| `OAUTH2_REDIRECT_URI` | OAuth2 redirect URI | http://localhost:8080 |

**Required Actions:**
- Replace placeholder OAuth credentials with real ones from:
  - [Google Cloud Console](https://console.cloud.google.com/)
  - [Facebook Developers](https://developers.facebook.com/)
  - [GitHub OAuth Apps](https://github.com/settings/developers)

---

## Firebase Configuration

| Variable | Description | Default |
|----------|-------------|---------|
| `FIREBASE_ENABLED` | Enable Firebase integration | false |
| `FIREBASE_CONFIG_PATH` | Path to Firebase config JSON | firebase-config.json |
| `FIREBASE_SERVICE_ACCOUNT_FILE` | Firebase service account file | firebase-config.json |
| `FIREBASE_DATABASE_URL` | Firebase Realtime Database URL | https://your-project.firebaseio.com |

**Required Actions:**
- Download service account JSON from Firebase Console
- Place it in project root as `firebase-config.json`
- Set `FIREBASE_ENABLED=true` to enable push notifications

---

## CORS Configuration

| Variable | Description | Dev Default | Prod Default |
|----------|-------------|-------------|--------------|
| `CORS_ALLOWED_ORIGINS` | Allowed origins for CORS | http://localhost:3000,http://localhost:8080,http://localhost:4200 | http://localhost:3000,http://localhost:8080 |

**Note:** Update for production with your actual frontend URLs

---

## File Upload Configuration

| Variable | Description | Default |
|----------|-------------|---------|
| `FILE_UPLOAD_DIR` | Upload directory path | ./uploads |
| `FILE_MAX_SIZE` | Max file size in bytes | 10485760 (10MB) |

---

## Service URLs

### Infrastructure Services
| Variable | Description | Dev Default | Prod Default |
|----------|-------------|-------------|--------------|
| `CONFIG_SERVER_URL` | Config server URL | http://localhost:8888 | http://config-server:8888 |
| `EUREKA_SERVER_URL` | Eureka server URL | http://localhost:8761/eureka/ | http://eureka-server:8761/eureka/ |
| `ZIPKIN_URL` | Zipkin tracing URL | http://localhost:9411/api/v2/spans | http://zipkin:9411/api/v2/spans |

### Business Services
| Variable | Description | Dev Default | Prod Default |
|----------|-------------|-------------|--------------|
| `USER_SERVICE_URL` | User service URL | http://localhost:8081 | http://user-service:8081 |
| `NOTIFICATION_SERVICE_URL` | Notification service URL | http://localhost:8083 | http://notification-service:8083 |
| `EMAIL_SERVICE_URL` | Email service URL | http://localhost:8082 | http://email-service:8082 |
| `BUSINESS_SERVICE_URL` | Business service URL | http://localhost:8084 | http://business-service:8084 |

---

## Application URLs

| Variable | Description | Default |
|----------|-------------|---------|
| `BASE_URL_API` | Backend API base URL | http://localhost:8080 |
| `BASE_URL_WEB` | Frontend web base URL | http://localhost:3000 |

**Note:** Update these for production deployment

---

## Monitoring Configuration

| Variable | Description | Default |
|----------|-------------|---------|
| `GRAFANA_USER` | Grafana admin username | admin |
| `GRAFANA_PASSWORD` | Grafana admin password | admin |

**Required Actions:**
- Change default Grafana credentials in production!

---

## Spring Configuration

| Variable | Description | Dev Default | Prod Default |
|----------|-------------|-------------|--------------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | dev | prod |

---

## Environment-Specific Variable Matrix

### Development (.env.local)
- Uses `localhost` for all services
- Database password: `postgres`
- More verbose logging
- All actuator endpoints exposed

### Production (.env)
- Uses Docker service names
- Database password: `root`
- Minimal logging
- Limited actuator endpoints

---

## Security Checklist

Before deploying to production:

- [ ] Change `JWT_SECRET` to a unique, strong secret
- [ ] Update `DB_PASSWORD` and `NOTIFICATION_DATABASE_PASSWORD`
- [ ] Replace OAuth2 credentials with production ones
- [ ] Configure real `MAIL_USERNAME` and `MAIL_PASSWORD`
- [ ] Update `CORS_ALLOWED_ORIGINS` with production URLs
- [ ] Change `GRAFANA_PASSWORD`
- [ ] Update `BASE_URL_API` and `BASE_URL_WEB`
- [ ] Review and update `EMAIL_VERIFICATION_URL` and `EMAIL_RESET_PASSWORD_URL`
- [ ] Secure Redis with `REDIS_PASSWORD` if needed
- [ ] Enable Firebase if using push notifications
- [ ] Review all default credentials and change them

---

## Quick Setup Commands

### Development Setup:
```bash
# Use .env.local for dev
cp .env.local .env

# Or set profile
export SPRING_PROFILES_ACTIVE=dev

# Start local services
docker-compose up -d postgres redis kafka

# Start Spring Boot services
./start-all.sh
```

### Production Setup:
```bash
# Use .env for production
export SPRING_PROFILES_ACTIVE=prod

# Start all services with Docker
docker-compose up -d
```

---

## Troubleshooting

### Services Can't Connect to Database
- Check `DB_HOST` matches your setup (localhost for dev, postgres for Docker)
- Verify database is running: `docker ps | grep postgres`
- Check credentials match database configuration

### OAuth2 Not Working
- Verify redirect URIs match in OAuth provider console
- Check client IDs and secrets are correct
- Ensure `OAUTH2_REDIRECT_URI` matches your API gateway URL

### Emails Not Sending
- Verify SMTP credentials
- For Gmail, use App Password, not regular password
- Check SMTP port (587 for TLS, 465 for SSL)
- Ensure `MAIL_AUTH` and `MAIL_ENABLE` are `true`

### Services Can't Find Each Other
- In dev: Use `localhost` URLs
- In Docker: Use service names (e.g., `user-service` not `localhost`)
- Check `SERVICE_URL` variables match your deployment

### Profile Not Loading
- Verify `SPRING_PROFILES_ACTIVE` is set
- Check yml files have correct profile names
- Ensure profile-specific files exist (application-dev.yml, application-prod.yml)

---

## Variable Usage by Service

### User Service Uses:
- Database variables
- Redis variables
- Kafka variables
- JWT variables
- OAuth2 variables
- Email variables
- File upload variables
- Eureka URL
- Zipkin URL

### Email Service Uses:
- SMTP variables
- Email service variables
- JWT secret
- User service URL

### Notification Service Uses:
- Notification database variables
- Firebase variables
- JWT secret
- User service URL
- Email service URL

### API Gateway Uses:
- CORS variables
- Service URLs (all)
- Eureka URL

### Business Service Uses:
- Database variables
- Kafka variables
- Service URLs
- Eureka URL

### Config Server Uses:
- (Uses file-based configuration, minimal env vars)

### Eureka Server Uses:
- (Standalone service, minimal env vars)

---

**Keep this reference handy when configuring your deployment!**
