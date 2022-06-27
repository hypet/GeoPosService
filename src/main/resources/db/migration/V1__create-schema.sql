drop table if exists "user";
create table "user"(
    id bigint auto_increment primary key,
    user_id varchar(36) not null,
    created_on date,
    email varchar(255),
    first_name varchar(255),
    second_name varchar(255)
);

create unique index user_userid_idx on "user" (user_id);

drop table if exists location;
create table location(
    id bigint auto_increment primary key,
    user_id bigint not null,
    created_on timestamp,
    latitude double precision,
    longitude double precision
);

create unique index location_userid_createdon_idx on location (user_id, created_on);
