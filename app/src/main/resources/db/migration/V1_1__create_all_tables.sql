CREATE TABLE email_records
(
    id VARCHAR(100) PRIMARY KEY,
    subject VARCHAR(500),
    has_html_body BOOLEAN,
    has_text_body BOOLEAN,
    has_attachments BOOLEAN,
    email_from VARCHAR(100),
    email_to TEXT,
    email_cc TEXT,
    email_bcc TEXT,
    received_at TIMESTAMP,
    sent_at TIMESTAMP,
    error_message TEXT
);

-- DROP IF NEEDED
-- DROP TABLE public.flyway_schema_history CASCADE;
-- DROP TABLE public.email_records CASCADE;
