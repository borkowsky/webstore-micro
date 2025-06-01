alter table brands add created_at timestamp not null default now();
alter table brands add updated_at timestamp not null default now();
