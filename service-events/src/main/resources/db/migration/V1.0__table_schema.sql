-- events table
create table if not exists events
(
    id         serial    not null primary key,
    user_id    uuid      not null,
    text       text      not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);
create index if not exists events_user_id_idx on events (user_id);