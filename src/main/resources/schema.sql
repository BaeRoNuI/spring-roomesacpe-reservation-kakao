CREATE TABLE IF NOT EXISTS THEME
(
    id    bigint not null auto_increment,
    name  varchar(20),
    desc  varchar(255),
    price int,
    primary key (id)
);

CREATE TABLE IF Not Exists RESERVATION
(
    id       bigint not null auto_increment,
    date     date,
    time     time,
    name     varchar(20),
    theme_id bigint null_to_default,
    foreign key (theme_id) REFERENCES THEME (id) on delete restrict,
    primary key (id)
);

