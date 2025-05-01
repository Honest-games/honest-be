create table promos (
    id serial primary key,
    promo_code varchar not null,
    deck_id varchar not null,
    promo_type varchar not null,
    deleted_at timestamp
);

create unique index promos_promo_code_unique
    on promos (promo_code)
    where deleted_at is null;

create table entered_promos (
    id serial primary key,
    promo_id int not null references promos (id),
    client_id varchar not null,
    created_at timestamp default now()
);

create unique index entered_promos_client_id_promo_id_unique
    on entered_promos (client_id, promo_id);

update decks set ai_type = 'NON_AI' where ai_type = 'AI_EXTENDED';