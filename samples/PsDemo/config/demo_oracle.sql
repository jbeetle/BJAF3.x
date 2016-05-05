/*==============================================================*/
/* DBMS name:      ORACLE Version 11g                           */
/* Created on:     2013/4/25 9:22:46                            */
/*==============================================================*/


alter table EXP_UF_RELATION
   drop constraint FK_EXP_UF__REFERENCE_EXP_USE;

alter table EXP_UF_RELATION
   drop constraint FK_EXP_UF__REFERENCE_EXP_FRI;

drop table EXP_FRIEND cascade constraints;

drop table EXP_UF_RELATION cascade constraints;

drop table EXP_USER cascade constraints;

/*==============================================================*/
/* Table: EXP_FRIEND                                           */
/*==============================================================*/
create table EXP_FRIEND 
(
   FRIENDID             NUMBER(12)           not null,
   FRIENDNAME           VARCHAR2(50)         not null,
   PHONE                VARCHAR2(50)         not null,
   EMAIL                VARCHAR2(50),
   ADDRESS              VARCHAR2(50),
   constraint FRIENDPK primary key (FRIENDID)
);

/*==============================================================*/
/* Table: EXP_UF_RELATION                                      */
/*==============================================================*/
create table EXP_UF_RELATION 
(
   USERID               NUMBER(12)           not null,
   FRIENDID             NUMBER(12)           not null,
   constraint UFRPK primary key (USERID, FRIENDID)
);

/*==============================================================*/
/* Table: EXP_USER                                             */
/*==============================================================*/
create table EXP_USER 
(
   USERID               NUMBER(12)           not null,
   PASSWD               VARCHAR2(100)        not null,
   USERNAME             VARCHAR2(50)         not null,
   EMAIL                VARCHAR2(50),
   SEX                  NUMBER(1),
   BIRTHDAY             DATE,
   constraint USERPK primary key (USERID),
   constraint USERUIQ unique (USERNAME)
);

alter table EXP_UF_RELATION
   add constraint FK_EXP_UF__REFERENCE_EXP_USE foreign key (USERID)
      references EXP_USER (USERID);

alter table EXP_UF_RELATION
   add constraint FK_EXP_UF__REFERENCE_EXP_FRI foreign key (FRIENDID)
      references EXP_FRIEND (FRIENDID);

create sequence seq_exp_user start with 1001;
create sequence seq_exp_friend start with 10001;