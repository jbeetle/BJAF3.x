/*==============================================================*/
/* Database name:  security                                     */
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     16-5-10 19:05:31                             */
/*==============================================================*/


drop database if exists security;

/*==============================================================*/
/* Database: security                                           */
/*==============================================================*/
create database security;

use security;

/*==============================================================*/
/* Table: sec_permissions                                       */
/*==============================================================*/
create table sec_permissions
(
   permissionId         bigint(20) not null auto_increment,
   permission           varchar(100) default NULL,
   description          varchar(100) default NULL,
   available            smallint(1) default '0',
   primary key (permissionId),
   unique key idx_sec_permissions_permission (permission)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: sec_roles                                             */
/*==============================================================*/
create table sec_roles
(
   roleId               bigint(20) not null auto_increment,
   role                 varchar(100) default NULL,
   description          varchar(100) default NULL,
   available            smallint(1) default '0',
   primary key (roleId),
   unique key idx_sec_roles_role (role)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: sec_roles_permissions                                 */
/*==============================================================*/
create table sec_roles_permissions
(
   roleId               bigint(20) not null,
   permissionId         bigint(20) not null,
   primary key (roleId, permissionId)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: sec_users                                             */
/*==============================================================*/
create table sec_users
(
   userId               bigint(20) not null auto_increment,
   username             varchar(100) default NULL,
   password             varchar(100) default NULL,
   salt                 varchar(100) default NULL,
   locked               smallint(1) default '0',
   primary key (userId),
   unique key idx_sec_users_username (username)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: sec_users_roles                                       */
/*==============================================================*/
create table sec_users_roles
(
   userId               bigint(20) not null,
   roleId               bigint(20) not null,
   primary key (userId, roleId)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table sec_roles_permissions add constraint FK_Reference_3 foreign key (permissionId)
      references sec_permissions (permissionId) on delete restrict on update restrict;

alter table sec_roles_permissions add constraint FK_Reference_4 foreign key (roleId)
      references sec_roles (roleId) on delete restrict on update restrict;

alter table sec_users_roles add constraint FK_Reference_1 foreign key (userId)
      references sec_users (userId) on delete restrict on update restrict;

alter table sec_users_roles add constraint FK_Reference_2 foreign key (roleId)
      references sec_roles (roleId) on delete restrict on update restrict;

