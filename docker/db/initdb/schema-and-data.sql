use matematix;
CREATE TABLE users(username VARCHAR(15) PRIMARY KEY NOT NULL,email VARCHAR(45) NOT NULL, password VARCHAR(1000) NOT NULL,blocked TINYINT,enabled TINYINT,role VARCHAR(30),rating INT,last_time_online DATETIME);
CREATE TABLE articles(id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,name VARCHAR(300));
CREATE TABLE avatars(username VARCHAR(45) PRIMARY KEY NOT NULL,
avatar BLOB, FOREIGN KEY(username) REFERENCES users(username));
create table comments(id BIGINT PRIMARY KEY NOT NULL auto_increment,
content TEXT,date DATETIME,author VARCHAR(45),receiver VARCHAR(45),rating BIGINT,
 FOREIGN KEY(receiver) REFERENCES users(username));
create table comment_user_actions(comment_id BIGINT PRIMARY KEY NOT NULL,
username VARCHAR(45), action TINYINT);
create table reset_tokens(id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
token TEXT,expiration_date DATETIME,username VARCHAR(45),
FOREIGN KEY(username) REFERENCES users(username));
CREATE TABLE user_friends (
  username    VARCHAR(45) NOT NULL,
  friend_name VARCHAR(45) NOT NULL,
  PRIMARY KEY (username, friend_name),
  FOREIGN KEY (username)    REFERENCES users(username),
  FOREIGN KEY (friend_name) REFERENCES users(username)
);
create table verification_tokens(id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
token TEXT,expiration_date DATETIME,username VARCHAR(45),
FOREIGN KEY(username) REFERENCES users(username));
CREATE TABLE user_articles(username VARCHAR(30),id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT);
INSERT INTO users (username, email, password, blocked, enabled, role, rating, last_time_online)
VALUES
  ('user','user@example.com','$10$PCtPI6fP1oPqm9VcvYB/gu6bUGpmE22RWTrEOqEJbSRlubhyVR/IK',0,1,'ROLE_USER',0,NULL);

