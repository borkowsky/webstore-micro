create table if not exists addresses
(
    id          serial                 not null primary key,
    user_id     uuid                   not null,
    address     character varying(255) not null,
    city        character varying(255) not null,
    region      character varying(255) not null,
    country     character varying(6)   not null,
    postal_code integer                         default null,
    created_at  timestamp              not null default now(),
    updated_at  timestamp              not null default now()
);

create index addresses_user_id on addresses (user_id);