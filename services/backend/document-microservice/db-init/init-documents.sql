-- Document microservice table initialization
-- This script creates the document table with proper UUID support

CREATE TABLE IF NOT EXISTS document (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    filename VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_type VARCHAR(50),
    upload_date TIMESTAMP(6) WITH TIME ZONE,
    course_id UUID NOT NULL,
    user_id INTEGER,
    upload_status VARCHAR(50) DEFAULT 'PENDING',
    extracted_text TEXT,
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_document_course FOREIGN KEY (course_id) REFERENCES course_spaces(id),
    CONSTRAINT fk_document_user FOREIGN KEY (user_id) REFERENCES app_user(id),
    CONSTRAINT unique_document_course UNIQUE (filename, course_id) -- Prevent duplicate documents in same course
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_document_course_id ON document(course_id);
CREATE INDEX IF NOT EXISTS idx_document_user_id ON document(user_id);
CREATE INDEX IF NOT EXISTS idx_document_upload_status ON document(upload_status);

SELECT 'Finished initializing document table from init-documents.sql' AS status;
