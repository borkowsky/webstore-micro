-- favorites table
create table if not exists favorites
(
    id         serial    not null primary key,
    user_id    uuid      not null,
    product_id integer   not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);
create index if not exists favorites_user_id_idx on favorites (user_id);