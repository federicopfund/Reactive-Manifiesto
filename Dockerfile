# Multi-stage Dockerfile for Play Framework 3.0.1 application
# Stage 1: Build the application
FROM eclipse-temurin:17-jdk as builder

# Install sbt 1.9.7
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    curl -L -o /tmp/sbt.tgz https://github.com/sbt/sbt/releases/download/v1.9.7/sbt-1.9.7.tgz && \
    tar -xzf /tmp/sbt.tgz -C /usr/local && \
    ln -s /usr/local/sbt/bin/sbt /usr/bin/sbt && \
    rm /tmp/sbt.tgz && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy project files
COPY project/build.properties project/
COPY project/plugins.sbt project/
COPY build.sbt .

# Download dependencies (cached layer)
RUN sbt update

# Copy application source code
COPY . .

# Build the application
RUN sbt compile stage

# Stage 2: Run the application
FROM eclipse-temurin:17-jre

# Create a non-root user
RUN useradd -m -u 1001 playuser

# Set working directory
WORKDIR /app

# Copy the staged application from builder
COPY --from=builder --chown=playuser:playuser /app/target/universal/stage /app

# Switch to non-root user
USER playuser

# Expose the application port
EXPOSE 9000

# Set default environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m" \
    APPLICATION_SECRET="changeme_please_use_env_var" \
    HTTP_PORT=9000

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:${HTTP_PORT}/health || exit 1

# Run the application
CMD ["sh", "-c", "bin/web -Dhttp.port=${HTTP_PORT} -Dplay.http.secret.key=${APPLICATION_SECRET} ${JAVA_OPTS}"]
