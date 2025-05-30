-- payments table
create type payments_status as enum ('CREATED', 'APPROVED', 'REJECTED');
create table payments
(
    id         serial          not null primary key,
    user_id    uuid            not null,
    sum        float           not null default 0.0,
    status     payments_status not null default 'CREATED',
    created_at timestamp       not null default now(),
    updated_at timestamp       not null default now(),
    deleted    boolean         not null default false
);
create index payments_user_id on payments (user_id);

-- orders table
create type orders_status as enum ('CREATED', 'PAID', 'ACCEPTED', 'REJECTED', 'DELIVERY', 'DELIVERED', 'RECEIVED');
create table if not exists orders
(
    id         serial        not null primary key,
    user_id    uuid          not null,
    status     orders_status not null default 'CREATED',
    deleted    boolean       not null default false,
    address_id integer       not null,
    payment_id integer                default null references payments (id) on delete set null,
    created_at timestamp     not null default now(),
    updated_at timestamp     not null default now()
);
create index if not exists orders_deleted_idx on orders (deleted);

-- orders_products table
create table orders_products
(
    id         serial    not null primary key,
    product_id integer   not null,
    order_id   integer            default null,
    amount     integer   not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);
create index orders_products_idx on orders_products (product_id, order_id);