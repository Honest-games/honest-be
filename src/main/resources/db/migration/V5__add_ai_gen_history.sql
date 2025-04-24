CREATE TABLE IF NOT EXISTS ai_generated_questions_history (
    id SERIAL PRIMARY KEY,
    question_text VARCHAR NOT NULL,
    level_id VARCHAR NOT NULL REFERENCES levels(id),
    client_id VARCHAR NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);