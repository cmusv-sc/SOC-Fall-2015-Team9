use testdb;


/*
create table if not exists Message
(
  id integer auto_increment,
  type varchar(512) not null,
  state varchar(512) not null,
  fromUerId integer not null,
  toUerId integer not null,
  content varchar(2048) not null,
  primary key (id),
  foreign key (fromUerId ) References User(id),
  foreign key (toUerId ) References User(id)
);
*/



CREATE TABLE if not exists Comment (
  commentId bigint(20) NOT NULL AUTO_INCREMENT,
  parentId bigint(20) DEFAULT NULL,
  inReplyTo varchar(255) DEFAULT NULL, 
  elementId bigint(20) DEFAULT NULL, 
  createdBy bigint(20) DEFAULT NULL,
  fullname varchar(255) DEFAULT NULL,
  picture varchar(255) DEFAULT NULL,
  postedDate datetime DEFAULT NULL,
  text varchar(255) DEFAULT NULL,
  PRIMARY KEY (commentId),
  FOREIGN KEY(elementId) REFERENCES ClimateService(id),
  FOREIGN KEY(createdBy) REFERENCES User(id)
  );

create table if not exists HashTag
(
  id bigint(20) not null AUTO_INCREMENT,
  commentId bigint(20) not null,
  serviceId bigint(20) not null,
  content varchar(1024),
  primary key (id),
  foreign key (commentId) References Comment(commentId),
  foreign key (serviceId) References ClimateService(id)
);

