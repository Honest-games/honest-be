alter table public.decks
    drop constraint decks_bg_image_id_fkey;

alter table public.decks
    add foreign key (bg_image_id) references public.vector_images
        on update cascade;

alter table public.decks
    drop constraint decks_modal_image_id_fkey;

alter table public.decks
    add foreign key (modal_image_id) references public.vector_images
        on update cascade;

-- promos

alter table public.promos
    add constraint promos_decks_id_fk
        foreign key (deck_id) references public.decks
            on update cascade;

-- levels
alter table public.levels
    drop constraint levels_bg_image_id_fkey;

alter table public.levels
    add foreign key (bg_image_id) references public.vector_images
        on update cascade;

alter table public.levels
    add constraint levels_decks_id_fk
        foreign key (deck_id) references public.decks
            on update cascade;

-- questions

alter table public.questions
    add constraint questions_levels_id_fk
        foreign key (level_id) references public.levels
            on update cascade;

