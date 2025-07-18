-- Insert 5 sample users
-- IMPORTANT: Passwords are in plain text for this initial setup.

CREATE TABLE IF NOT EXISTS app_user (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL
);

INSERT INTO app_user (email, password_hash, name) VALUES
('evtim@example.com', 'evtimpass', 'Evtim Kostadinov'),
('laura@example.com', 'laurapass', 'Laura Leschke'),
('carlos@example.com', 'carlospass', 'Carlos Mejia'),
('max@example.com', 'maxpass', 'Max Mustermann'),
('erika@example.com', 'erikapass', 'Erika Mustermann')
ON CONFLICT (email) DO NOTHING; -- Optional: Prevents error if an email already exists, useful if script runs multiple times against a non-empty DB somehow, though initdb.d scripts usually don't.

CREATE TABLE IF NOT EXISTS course_spaces (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_course_space_user FOREIGN KEY (user_id) REFERENCES app_user(id) 
);
