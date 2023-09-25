alter table if exists newsletter_target
    drop column if exists ip_address,
    add column if not exists created timestamp not null default now(),
    add column if not exists updated timestamp not null default now()
;
