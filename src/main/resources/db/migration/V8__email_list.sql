drop domain if exists email;
create domain email as text
    -- https://html.spec.whatwg.org/multipage/input.html#e-mail-state-(type=email)
    check ( value ~ '^[a-zA-Z0-9.!#$%&''*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$' );

create table if not exists newsletter_target
(
    email      email not null unique primary key,
    ip_address varchar(40)
);
