alter table decks
add column if not exists ai_type varchar not null default 'NON_AI';