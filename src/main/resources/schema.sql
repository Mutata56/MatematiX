DROP TABLE IF EXISTS USERS cascade;
DROP TABLE IF EXISTS comments cascade;
DROP TABLE IF EXISTS articles cascade;
DROP TABLE IF EXISTS avatars cascade;
DROP TABLE IF EXISTS article_comments cascade;
DROP TABLE IF EXISTS comment_user_actions cascade;
DROP TABLE IF EXISTS user_article cascade;
DROP TABLE IF EXISTS reset_tokens cascade;
DROP TABLE IF EXISTS verification_tokens cascade;
CREATE TABLE USERS(
username varchar(15) NOT NULL UNIQUE PRIMARY KEY,
email varchar(45) NOT NULL UNIQUE,
password varchar(60) DEFAULT NULL,
enabled int DEFAULT NULL,
blocked int DEFAULT NULL,
role varchar(20) DEFAULT NULL,
last_time_online datetime DEFAULT NULL,
rating int DEFAULT '0'
);
CREATE TABLE comments (
id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
content varchar(300) DEFAULT NULL,
date datetime DEFAULT NULL,
author varchar(15) DEFAULT NULL,
receiver varchar(15) DEFAULT NULL,
rating smallint DEFAULT NULL,
FOREIGN KEY (receiver) REFERENCES USERS(username) ON DELETE CASCADE
);
CREATE TABLE articles (
id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
name varchar(50) DEFAULT NULL
);
CREATE TABLE article_comments (
article_id int NOT NULL,
comment_id int NOT NULL,
FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE
);
CREATE TABLE avatars (
username varchar(15) DEFAULT NULL PRIMARY KEY,
avatar_format varchar(4) DEFAULT NULL,
avatar mediumblob,
FOREIGN KEY (username) REFERENCES users (username) ON DELETE CASCADE
);
CREATE TABLE comment_user_actions (
comment_id int NOT NULL,
username varchar(15) NOT NULL,
action tinyint DEFAULT NULL,
FOREIGN KEY (comment_id) REFERENCES comments (id) ON DELETE CASCADE,
FOREIGN KEY (username) REFERENCES users (username) ON DELETE CASCADE
);
CREATE TABLE reset_tokens (
id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
expiration_date timestamp NOT NULL,
username varchar(15) NOT NULL,
token varchar(36) NOT NULL,
FOREIGN KEY (username) REFERENCES users (username) ON DELETE CASCADE
);
CREATE TABLE user_article (
name varchar(15) NOT NULL,
article_id int NOT NULL,
FOREIGN KEY (name) REFERENCES users (username) ON DELETE CASCADE,
FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE
);
CREATE TABLE verification_tokens (
id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
expiration_date timestamp NOT NULL,
username varchar(15) NOT NULL,
token varchar(36) NOT NULL,
FOREIGN KEY (username) REFERENCES users (username) ON DELETE CASCADE
);