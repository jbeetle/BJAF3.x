CREATE TABLE xx_user  
(  
  userid bigint NOT NULL,  
  passwd text NOT NULL,  
  username text NOT NULL,  
  email text,  
  sex integer,
  birthday date,
  CONSTRAINT xx_user_pk PRIMARY KEY (userid)  
);
CREATE SEQUENCE xxx_userid_seq
  INCREMENT 1
  MINVALUE 10000
  MAXVALUE 99999999999999999
  START 10004
  CACHE 1
  CYCLE;
CREATE TABLE xx_friend  
(  
  friendid bigint not null,
  userid bigint NOT NULL,  
  friendname text NOT NULL,  
  phone text NOT NULL,  
  email text,  
  address text,
  CONSTRAINT xx_friend_pk PRIMARY KEY (friendid)  
);
CREATE SEQUENCE xxx_friendid_seq
  INCREMENT 1
  MINVALUE 10000
  MAXVALUE 99999999999999999
  START 10004
  CACHE 1
  CYCLE;
INSERT INTO xx_user(
            userid, passwd, username, email, sex, birthday)
    VALUES (10000, '7b55ef6a3c0c4be172e29384b6e6c5f1ca9d620eb1580489e3c888f49cec9904', 'Henry', 'henryyu@163.com', 1, '1976-02-24');
 
commit;

	
