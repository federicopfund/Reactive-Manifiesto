# Deployment Guide for Render

This branch contains the configuration files needed to deploy the Reactive Manifesto application on Render.

## Files Added

### render.yaml
Main configuration file for Render deployment. It defines:
- **Web Service**: Play Framework application running on Java
- **PostgreSQL Database**: Production database service
- **Environment Variables**: Automatically configured

### system.properties
Specifies Java 17 runtime for the application.

### build.sbt (modified)
Added PostgreSQL driver dependency for production use.

## Deployment Steps

1. **Connect Repository to Render**
   - Go to [Render Dashboard](https://dashboard.render.com/)
   - Click "New" → "Blueprint"
   - Connect your GitHub repository
   - Select the `deploy` branch
   - Render will automatically detect the `render.yaml` file

2. **Review Configuration**
   - Render will show all services defined in `render.yaml`
   - Verify the web service and database configuration
   - The APPLICATION_SECRET will be auto-generated

3. **Deploy**
   - Click "Apply" to start deployment
   - Render will:
     - Create the PostgreSQL database
     - Build the application using SBT
     - Start the Play Framework server
     - Run database migrations automatically (via Play Evolutions)

## Environment Configuration

The following environment variables are automatically configured:

- `APPLICATION_SECRET`: Auto-generated secure key for Play Framework
- `DATABASE_URL`: PostgreSQL connection string from the database service
- `JAVA_OPTS`: JVM memory settings (-Xmx512m -Xms256m)
- `PORT`: Dynamically assigned by Render

## Database Configuration

The application automatically switches to PostgreSQL in production via command-line parameters:
- Profile: `slick.jdbc.PostgresProfile$`
- Driver: `org.postgresql.Driver`
- URL: From `$DATABASE_URL` environment variable

## Monitoring

- **Health Check**: Configured at `/` endpoint
- **Logs**: Available in Render dashboard
- **Database**: PostgreSQL instance with connection pooling

## Local Development vs Production

- **Local**: Uses H2 in-memory database (configured in `conf/application.conf`)
- **Production**: Uses PostgreSQL (configured via command-line parameters in `render.yaml`)

## Troubleshooting

### Build Failures
- Check Java version (should be 17)
- Verify SBT can compile the project locally
- Review Render build logs for specific errors

### Runtime Errors
- Check environment variables are properly set
- Verify DATABASE_URL is correctly formatted
- Review application logs in Render dashboard

### Database Connection Issues
- Ensure PostgreSQL service is running
- Verify connection string format
- Check database credentials

## Scaling

To scale the application:
1. Upgrade the plan in Render dashboard
2. The application is stateless and can handle horizontal scaling
3. Database connection pool will adjust automatically

## Cost

With free tier:
- Web Service: Free (with limitations)
- PostgreSQL: Free (with limitations)

Check [Render Pricing](https://render.com/pricing) for details.
