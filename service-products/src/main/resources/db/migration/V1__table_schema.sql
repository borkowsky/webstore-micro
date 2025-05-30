-- categories table
create table if not exists categories
(
    id          serial                 not null primary key,
    name        character varying(255) not null,
    description text                            default null,
    icon        character varying(255)          default null,
    category_id integer                         default null references categories (id) on delete cascade,
    deleted     boolean                not null default false,
    enabled     boolean                not null default true,
    created_at  timestamp              not null default now(),
    updated_at  timestamp              not null default now()
);
create index if not exists categories_category_id_idx on categories (category_id);
create index if not exists categories_deleted_idx on categories (deleted);
create index if not exists categories_enabled_idx on categories (enabled);


-- brands table
create table brands
(
    id      serial                 not null primary key,
    name    character varying(255) not null unique,
    image   character varying(255) not null,
    deleted boolean                not null default false
);
create index brands_name_idx on brands (name);

-- products table
create table if not exists products
(
    id             serial                   not null primary key,
    name           character varying(255)   not null,
    description    text                     not null,
    category_id    integer                  not null references categories (id) on delete cascade,
    price          float                    not null,
    discount_price float                             default null,
    balance        integer                  not null,
    images         character varying(255)[] not null default '{}',
    deleted        boolean                  not null default false,
    enabled        boolean                  not null default true,
    rating         float                    not null default 0.0,
    tags           character varying(255)[] not null default '{}',
    brand_id       integer                           default null references brands (id) on delete set null,
    created_at     timestamp                not null default now(),
    updated_at     timestamp                not null default now()
);
create index if not exists products_category_id_idx on products (category_id);
create index if not exists products_deleted_idx on products (deleted);
create index if not exists products_enabled_idx on products (enabled);
create index if not exists products_rating_idx on products (rating);
create index if not exists products_tags_idx on products using gin (tags array_ops);