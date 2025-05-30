-- basket table
create table if not exists basket
(
    id         serial    not null primary key,
    user_id    uuid      not null,
    product_id integer   not null,
    amount     integer   not null default 1,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);
create index if not exists basket_user_id_idx on basket (user_id);