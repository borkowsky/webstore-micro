-- reviews table
create table if not exists reviews
(
    id         serial                   not null primary key,
    rating     integer                  not null,
    text       text                              default null,
    user_id    uuid                     not null,
    product_id integer                  not null,
    images     character varying(255)[] not null default '{}',
    order_id   integer                  not null,
    deleted    boolean                  not null default false,
    created_at timestamp                not null default now(),
    updated_at timestamp                not null default now()
);
create index if not exists reviews_product_id_idx on reviews (product_id);
create index if not exists reviews_user_id_idx on reviews (user_id);
create index if not exists reviews_deleted_idx on reviews (deleted);
