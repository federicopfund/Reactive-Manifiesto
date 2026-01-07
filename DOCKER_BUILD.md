# 🐳 Docker Build Notes

## Building the Docker Image

The Dockerfile uses a multi-stage build to create an optimized production image:

```bash
docker build -t reactive-manifesto .
```

### Build Time
- First build: ~10-15 minutes (downloads dependencies)
- Subsequent builds: ~3-5 minutes (uses cache)

### Troubleshooting

#### SSL Certificate Issues

If you encounter SSL certificate errors during build:

```bash
# Option 1: Use --network=host
docker build --network=host -t reactive-manifesto .

# Option 2: Use buildx with different driver
docker buildx create --use
docker buildx build -t reactive-manifesto .
```

#### Build Optimization

To speed up builds, consider:

1. **Use BuildKit**:
   ```bash
   DOCKER_BUILDKIT=1 docker build -t reactive-manifesto .
   ```

2. **Use cache mount** (requires BuildKit):
   Add to Dockerfile before `RUN sbt`:
   ```dockerfile
   RUN --mount=type=cache,target=/root/.sbt \
       --mount=type=cache,target=/root/.ivy2 \
       sbt clean compile stage
   ```

3. **Pre-built base image**:
   Create a base image with SBT and dependencies cached.

#### Alternative: Use Pre-built Images

If building locally is slow, you can:

1. Use GitHub Actions to build and push to Docker Hub
2. Pull the pre-built image:
   ```bash
   docker pull username/reactive-manifesto:latest
   ```

### Running the Container

```bash
docker run -d -p 9000:9000 \
  -e APPLICATION_SECRET="your-secret-here" \
  --name reactive-app \
  reactive-manifesto
```

### With Docker Compose

For development with PostgreSQL:

```bash
docker-compose up -d
```

This starts both the app and PostgreSQL database.

## CI/CD Considerations

In CI/CD environments (GitHub Actions), builds may encounter:
- SSL certificate issues (especially with self-signed certificates)
- Network timeouts
- Build time limits

**Recommendation**: Let GitHub Actions handle the build using the provided workflows.
