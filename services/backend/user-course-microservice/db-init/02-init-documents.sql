-- Document microservice table initialization
-- This script creates the documents table with proper UUID support

-- Should be in documents microservice db-init folder
-- Must be run after course_spaces table is created
-- Hence must be placed in the same folder as the course_spaces table creation script to ensure order of execution

CREATE TABLE IF NOT EXISTS documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    filename VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_type VARCHAR(50),
    upload_date TIMESTAMP(6) WITH TIME ZONE,
    course_id UUID NOT NULL,
    upload_status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_documents_course FOREIGN KEY (course_id) REFERENCES course_spaces(id),
    CONSTRAINT unique_documents_course UNIQUE (filename, course_id) -- Prevent duplicate documents in same course
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_documents_course_id ON documents(course_id);
CREATE INDEX IF NOT EXISTS idx_documents_upload_status ON documents(upload_status);

SELECT 'Finished initializing documents table from init-documents.sql' AS status;
