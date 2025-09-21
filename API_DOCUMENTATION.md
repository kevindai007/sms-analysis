# SMS Analysis API

This application provides a REST API for uploading and analyzing SMS data.

## Database Schema

The application uses PostgreSQL with the following tables:

### analysis_task
- Tracks analysis tasks
- Fields: id, task_id, start_time, end_time, status, created_at, updated_at

### task_detail
- Stores individual SMS records
- Fields: id, task_id, year_month, sms_id, sms_address, sms_date, sms_date_sent, sms_body, sms_type, sms_thread_id, sms_subscription_id, created_at, updated_at

### task_summary
- Stores analysis results by year-month
- Fields: id, task_id, year_month, total_sms, result (JSONB), created_at, updated_at

## API Endpoints

### 1. Create Analysis Task
```
POST /api/sms-analysis/tasks
```
Creates a new analysis task and returns a unique task ID.

**Response:**
```json
{
  "success": true,
  "message": "Analysis task created successfully",
  "data": {
    "id": 1,
    "taskId": "uuid-string",
    "status": "IN_PROGRESS",
    "startTime": "2023-01-01T10:00:00",
    "createdAt": "2023-01-01T10:00:00",
    "updatedAt": "2023-01-01T10:00:00"
  }
}
```

### 2. Get Analysis Task
```
GET /api/sms-analysis/tasks/{taskId}
```
Retrieves the details of a specific analysis task.

### 3. Upload SMS Data
```
POST /api/sms-analysis/tasks/{taskId}/upload
```
Uploads SMS data to a specific task.

**Request Body:**
```json
[
  {
    "smsId": "sms123",
    "smsAddress": "+1234567890",
    "smsDate": 1672531200000,
    "smsDateSent": 1672531200000,
    "smsBody": "Hello, this is a test message",
    "smsType": 1,
    "smsThreadId": 100,
    "smsSubscriptionId": 1,
    "yearMonth": "2023-01"
  }
]
```

**Response:**
```json
{
  "success": true,
  "message": "SMS data uploaded successfully",
  "data": "Uploaded 1 SMS records"
}
```

### 4. Complete Analysis
```
POST /api/sms-analysis/tasks/{taskId}/complete
```
Completes the analysis and generates summaries for all year-months.

**Response:**
```json
{
  "success": true,
  "message": "Analysis completed successfully",
  "data": "task-id"
}
```

### 5. Get Task Summaries
```
GET /api/sms-analysis/tasks/{taskId}/summaries
```
Retrieves all analysis summaries for a task.

**Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "taskId": "uuid-string",
      "yearMonth": "2023-01",
      "totalSms": 100,
      "result": {
        "totalCount": 100,
        "incomingCount": 60,
        "outgoingCount": 40,
        "averageMessageLength": 45.5,
        "topAddresses": [
          {
            "address": "+1234567890",
            "count": 25
          }
        ],
        "generatedAt": "2023-01-01T10:00:00"
      }
    }
  ]
}
```

### 6. Generate Summary for Specific Year-Month
```
POST /api/sms-analysis/tasks/{taskId}/summaries/{yearMonth}
```
Generates analysis summary for a specific year-month (format: "YYYY-MM").

## Configuration

### Database Configuration
Configure the database connection in `application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/sms_analysis
    username: your_username
    password: your_password
```

### Environment Variables
- `DB_URL`: Database URL (default: jdbc:postgresql://localhost:5432/sms_analysis)
- `DB_USERNAME`: Database username (default: root)
- `DB_PASSWORD`: Database password (default: password)
- `SHOW_SQL`: Show SQL queries in logs (default: false)
- `LOG_LEVEL`: Application log level (default: INFO)

## SMS Data Fields

- **smsId**: Unique identifier for the SMS
- **smsAddress**: Phone number or address
- **smsDate**: Message date as timestamp (milliseconds)
- **smsDateSent**: Sent date as timestamp (milliseconds)
- **smsBody**: Message content
- **smsType**: Message type (1 = incoming, 2 = outgoing)
- **smsThreadId**: Thread/conversation ID
- **smsSubscriptionId**: Subscription ID
- **yearMonth**: Year-month grouping (format: "YYYY-MM")

## Analysis Results

The analysis generates the following statistics:
- Total message count
- Incoming vs outgoing message counts
- Average message length
- Top 5 most active addresses
- Generation timestamp

Results are stored in JSONB format allowing for flexible analysis data structure.