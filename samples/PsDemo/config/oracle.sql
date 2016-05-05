set define off
spool test.log

prompt
prompt Creating table EXP_FRIEND
prompt =========================
prompt
create table EXP_FRIEND
(
  friendid   NUMBER(12) not null,
  friendname VARCHAR2(50) not null,
  phone      VARCHAR2(50) not null,
  email      VARCHAR2(50),
  address    VARCHAR2(50)
)
;
alter table EXP_FRIEND
  add constraint FRIENDPK primary key (FRIENDID);

prompt
prompt Creating table EXP_USER
prompt =======================
prompt
create table EXP_USER
(
  userid   NUMBER(12) not null,
  passwd   VARCHAR2(100) not null,
  username VARCHAR2(50) not null,
  email    VARCHAR2(50),
  sex      NUMBER(1),
  birthday DATE
)
;
alter table EXP_USER
  add constraint USERPK primary key (USERID);
alter table EXP_USER
  add constraint USERUIQ unique (USERNAME);

prompt
prompt Creating table EXP_UF_RELATION
prompt ==============================
prompt
create table EXP_UF_RELATION
(
  userid   NUMBER(12) not null,
  friendid NUMBER(12) not null
)
;
alter table EXP_UF_RELATION
  add constraint UFRPK primary key (USERID, FRIENDID);
alter table EXP_UF_RELATION
  add constraint FK_EXP_UF__REFERENCE_EXP_FRI foreign key (FRIENDID)
  references EXP_FRIEND (FRIENDID);
alter table EXP_UF_RELATION
  add constraint FK_EXP_UF__REFERENCE_EXP_USE foreign key (USERID)
  references EXP_USER (USERID);

prompt
prompt Creating sequence SEQ_EXP_FRIEND
prompt ================================
prompt
create sequence SEQ_EXP_FRIEND
minvalue 1
maxvalue 9999999999999999999999999999
start with 10081
increment by 1
cache 20;

prompt
prompt Creating sequence SEQ_EXP_USER
prompt ==============================
prompt
create sequence SEQ_EXP_USER
minvalue 1
maxvalue 9999999999999999999999999999
start with 1021
increment by 1
cache 20;


spool off
