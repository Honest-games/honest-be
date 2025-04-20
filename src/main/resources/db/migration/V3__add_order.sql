alter table decks
add column if not exists deck_order int not null default 1;