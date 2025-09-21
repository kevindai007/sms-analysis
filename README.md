# SMS Analysis Application

A Spring Boot application for uploading and analyzing SMS data with PostgreSQL database support.

## Features

- Upload SMS data in batches
- Automatic grouping by year-month
- Statistical analysis including:
  - Message count statistics (incoming/outgoing)
  - Average message length
  - Top contacts by message count
  - Monthly summaries
- REST API for all operations
- PostgreSQL database with proper schema
- Comprehensive test coverage

## Database Schema

The application uses three main tables:
- `analysis_task`: Tracks analysis tasks and their status
- `task_detail`: Stores individual SMS records
- `task_summary`: Contains monthly analysis results in JSONB format

See [API_DOCUMENTATION.md](API_DOCUMENTATION.md) for detailed API usage and examples.

## Quick Start

1. Set up PostgreSQL database
2. Configure connection in `application.yaml` or environment variables
3. Run the application: `./mvnw spring-boot:run`
4. Access API at `http://localhost:8080/api/sms-analysis`

## Configuration

Set these environment variables:
- `DB_URL`: Database URL (default: jdbc:postgresql://localhost:5432/sms_analysis)
- `DB_USERNAME`: Database username (default: root)
- `DB_PASSWORD`: Database password (default: password)

## Testing

Run tests with: `./mvnw test`

The application includes unit tests and uses H2 in-memory database for testing.