alter table decks
    add column bg_image_id varchar references vector_images (id),
    add column modal_image_id varchar references vector_images (id);

alter table levels
    add column bg_image_id varchar references vector_images (id);