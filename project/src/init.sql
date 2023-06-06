create database text;

create table TEXT(
    token UUID PRIMARY KEY,
    PRIMARY KEY(token),
    text text NOT NULL,
    created_at bigint NOT NULL,
    expiration_time bigint NOT NULL
);