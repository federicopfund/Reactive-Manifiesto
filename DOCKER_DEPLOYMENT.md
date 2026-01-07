# Docker Deployment Guide

This guide explains how to deploy the Reactive Manifesto application using Docker and Docker Compose.

## Prerequisites

- Docker Engine 20.10+ or Docker Desktop
- Docker Compose 2.0+

## Quick Start

### Using Docker Compose (Recommended)

Run the application with PostgreSQL database:

```bash
docker-compose up --build
```

The application will be available at: http://localhost:9000

To run in detached mode:

```bash
docker-compose up -d --build
```

To stop the application:

```bash
docker-compose down
```

To stop and remove volumes (database data):

```bash
docker-compose down -v
```

### Using Docker Only

Build the image:

```bash
docker build -t reactive-manifesto:latest .
```

Run with H2 in-memory database (development):

```bash
docker run -p 9000:9000 \
  -e APPLICATION_SECRET="your_secret_key_here" \
  reactive-manifesto:latest
```

Run with external PostgreSQL (production):

```bash
docker run -p 9000:9000 \
  -e APPLICATION_SECRET="your_secret_key_here" \
  -e JAVA_OPTS="-Xmx512m -Xms256m" \
  reactive-manifesto:latest \
  sh -c "bin/web \
    -Dhttp.port=9000 \
    -Dplay.http.secret.key=\${APPLICATION_SECRET} \
    -Dslick.dbs.default.profile='slick.jdbc.PostgresProfile\$' \
    -Dslick.dbs.default.db.driver='org.postgresql.Driver' \
    -Dslick.dbs.default.db.url='jdbc:postgresql://your-db-host:5432/reactive_manifesto' \
    -Dslick.dbs.default.db.user='postgres' \
    -Dslick.dbs.default.db.password='your_password'"
```

## Configuration

### Environment Variables

- `HTTP_PORT`: Port for the application (default: 9000)
- `APPLICATION_SECRET`: Secret key for Play Framework (required in production)
- `JAVA_OPTS`: JVM options (default: "-Xmx512m -Xms256m")
- `DB_PROFILE`: Slick database profile (for PostgreSQL: "slick.jdbc.PostgresProfile$")
- `DB_DRIVER`: JDBC driver (for PostgreSQL: "org.postgresql.Driver")
- `DB_URL`: Database JDBC URL
- `DB_USER`: Database user
- `DB_PASSWORD`: Database password

### Docker Compose Environment

Edit `docker-compose.yml` to customize:

- Database credentials
- Application port
- Memory allocation (JAVA_OPTS)
- Application secret

## Architecture

### Multi-stage Build

The Dockerfile uses a multi-stage build process:

1. **Builder stage** (eclipse-temurin:17-jdk):
   - Installs sbt 1.9.7
   - Downloads dependencies
   - Compiles and stages the application

2. **Runtime stage** (eclipse-temurin:17-jre):
   - Uses minimal JRE image
   - Runs as non-root user (playuser)
   - Includes health check

### Versions

- Java: 17 (Eclipse Temurin)
- sbt: 1.9.7
- Play Framework: 3.0.1
- Scala: 2.13.12
- PostgreSQL: 16 (Alpine)

## Health Check

The container includes a health check that queries `/health` endpoint every 30 seconds.

Check container health:

```bash
docker ps
```

View health check logs:

```bash
docker inspect --format='{{json .State.Health}}' reactive-manifesto-app
```

## Logs

View application logs:

```bash
# Docker Compose
docker-compose logs -f web

# Docker only
docker logs -f <container_id>
```

View database logs:

```bash
docker-compose logs -f db
```

## Troubleshooting

### Port Already in Use

If port 9000 is already in use, change it in `docker-compose.yml`:

```yaml
ports:
  - "8080:9000"  # Map host port 8080 to container port 9000
```

### Database Connection Issues

Ensure the database is healthy:

```bash
docker-compose ps
```

Check database logs:

```bash
docker-compose logs db
```

### Memory Issues

Increase JVM memory in `docker-compose.yml`:

```yaml
environment:
  JAVA_OPTS: "-Xmx1024m -Xms512m"
```

### Application Secret

For production, generate a strong secret:

```bash
# Linux/Mac
openssl rand -base64 32

# Or use sbt
sbt playGenerateSecret
```

## Production Deployment

### Best Practices

1. **Use a strong APPLICATION_SECRET**:
   ```bash
   export APPLICATION_SECRET=$(openssl rand -base64 32)
   ```

2. **Use external database** (not docker-compose for production)

3. **Use environment-specific configurations**

4. **Set appropriate resource limits**:
   ```yaml
   deploy:
     resources:
       limits:
         cpus: '1'
         memory: 1G
   ```

5. **Use volumes for persistent data**

6. **Enable HTTPS** (use a reverse proxy like nginx)

### Example Production Command

```bash
docker run -d \
  --name reactive-manifesto \
  --restart unless-stopped \
  -p 9000:9000 \
  -e APPLICATION_SECRET="${APPLICATION_SECRET}" \
  -e JAVA_OPTS="-Xmx1024m -Xms512m" \
  --health-cmd="curl -f http://localhost:9000/health || exit 1" \
  --health-interval=30s \
  --health-timeout=3s \
  --health-retries=3 \
  reactive-manifesto:latest
```

## Cleaning Up

Remove all containers, images, and volumes:

```bash
# Stop and remove containers
docker-compose down -v

# Remove image
docker rmi reactive-manifesto:latest

# Clean up unused Docker resources
docker system prune -a
```

## Additional Resources

- [Play Framework Documentation](https://www.playframework.com/documentation/3.0.x/Home)
- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
