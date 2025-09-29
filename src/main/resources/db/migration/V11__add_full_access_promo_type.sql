-- Добавляем индекс для быстрого подсчета вопросов по клиенту
CREATE INDEX IF NOT EXISTS idx_questions_history_client_id
ON questions_history(client_id);