-- SMS Analysis Database Schema

-- Analysis Task Table
CREATE TABLE IF NOT EXISTS analysis_task (
    id SERIAL PRIMARY KEY,
    task_id VARCHAR(255) NOT NULL UNIQUE,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    status VARCHAR(24),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_task_id ON analysis_task(task_id);

-- Task Detail Table
CREATE TABLE IF NOT EXISTS task_detail (
    id SERIAL PRIMARY KEY,
    task_id VARCHAR(255) NOT NULL,
    year_month VARCHAR(10) NOT NULL,
    sms_id VARCHAR(255),
    sms_address VARCHAR(255),
    sms_date BIGINT,
    sms_date_sent BIGINT,
    sms_body TEXT,
    sms_type INTEGER,
    sms_thread_id INTEGER,
    sms_subscription_id INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES analysis_task(task_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_task_detail_task_id ON task_detail(task_id);
CREATE INDEX IF NOT EXISTS idx_task_detail_year_month ON task_detail(year_month);

-- Task Summary Table
CREATE TABLE IF NOT EXISTS task_summary (
    id SERIAL PRIMARY KEY,
    task_id VARCHAR(255) NOT NULL,
    year_month VARCHAR(10) NOT NULL,
    total_sms INTEGER,
    result JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES analysis_task(task_id) ON DELETE CASCADE,
    UNIQUE(task_id, year_month)
);

CREATE INDEX IF NOT EXISTS idx_task_summary_task_id ON task_summary(task_id);
CREATE INDEX IF NOT EXISTS idx_task_summary_year_month ON task_summary(year_month);