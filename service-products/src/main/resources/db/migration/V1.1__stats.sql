-- stats table
create table if not exists stats
(
    id         serial    not null primary key,
    users      integer   not null default 0,
    orders     integer   not null default 0,
    reviews    integer   not null default 0,
    paid       float     not null default 0.0,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);