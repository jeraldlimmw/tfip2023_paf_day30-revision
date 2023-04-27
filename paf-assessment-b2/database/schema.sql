create database bgg;

create table user(
user_id CHAR(8) NOT NULL PRIMARY KEY,
username VARCHAR(50) NOT NULL UNIQUE,
name VARCHAR(50)
);

create table task(
task_id INT auto_increment NOT NULL PRIMARY KEY,
description VARCHAR(255) NOT NULL,
priority INT NOT NULL,
due_date DATETIME NOT NULL,
user_id CHAR(8) NOT NULL
);
