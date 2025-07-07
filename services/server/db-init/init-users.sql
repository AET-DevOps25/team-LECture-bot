
-- Enable pgcrypto for UUID support (required by Hibernate for UUID columns)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_extension WHERE extname = 'pgcrypto') THEN
        CREATE EXTENSION pgcrypto;
    END IF;
END$$;

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
ON CONFLICT (email) DO NOTHING;







