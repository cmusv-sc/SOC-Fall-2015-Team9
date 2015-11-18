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
