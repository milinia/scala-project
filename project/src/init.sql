create database text;

create table TEXT(
    token serial not null,
    PRIMARY KEY(token),
    text character varying NOT NULL,
    created_at bigint NOT NULL,
    expiration_time bigint NOT NULL
);