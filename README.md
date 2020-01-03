# ObjectSpace-Cloud

## 简介

本项目是http://www.objectspace.cn 网站后台 基于SpringCloud。

## AuthCenter—用户认证授权中心

- ### 快速启动

  ```sql
  CREATE DATABASE cloud_authcenter;
  use cloud_authcenter;
  CREATE TABLE `cloud_filter_map` (
    `url` varchar(256) DEFAULT NULL,
    `filter` varchar(256) DEFAULT NULL
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
  CREATE TABLE `cloud_user` (
    `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户id',
    `user_email` varchar(64) NOT NULL COMMENT '用户邮箱(注册时使用)',
    `user_password` varchar(32) NOT NULL COMMENT '用户密码',
    `user_salt` varchar(32) NOT NULL COMMENT '采用加盐存储',
    `user_name` varchar(64) DEFAULT NULL COMMENT '用户名(昵称)',
    `user_profile` varchar(1024) DEFAULT NULL COMMENT '头像',
    `user_status` tinyint(1) DEFAULT NULL COMMENT '用户状态:true 正常 false 禁用',
    `user_register_date` date DEFAULT NULL,
    `user_last_login_date` date DEFAULT NULL COMMENT '上次登录时间',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `user_email` (`user_email`)
  ) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;
  CREATE TABLE `cloud_role` (
    `role_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色id',
    `role_name` varchar(128) NOT NULL COMMENT '角色名',
    `role_desc` varchar(512) DEFAULT NULL COMMENT '角色详细信息',
    `role_create_date` date DEFAULT NULL COMMENT '角色创建时间',
    `role_application` varchar(64) DEFAULT NULL COMMENT '角色归属应用',
    PRIMARY KEY (`role_id`)
  ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
  CREATE TABLE `cloud_permission` (
    `permission_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '权限id',
    `permission_name` varchar(64) DEFAULT NULL COMMENT '权限名',
    `permission_url` varchar(1024) DEFAULT NULL COMMENT '权限url',
    `permission_desc` varchar(1024) DEFAULT NULL COMMENT '权限详细描述',
    `permission_create_date` date DEFAULT NULL COMMENT '权限创建时间',
    `permission_application` varchar(32) DEFAULT NULL COMMENT '权限归属应用',
    PRIMARY KEY (`permission_id`)
  ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8
  CREATE TABLE `cloud_user_role` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `user_id` int(11) DEFAULT NULL,
    `role_id` int(11) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `user_id` (`user_id`),
    KEY `role_id` (`role_id`),
    CONSTRAINT `cloud_user_role_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `cloud_user` (`user_id`),
    CONSTRAINT `cloud_user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `cloud_role` (`role_id`)
  ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
  CREATE TABLE `cloud_role_permission` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `role_id` int(11) DEFAULT NULL,
    `permission_id` int(11) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `role_id` (`role_id`),
    KEY `permission_id` (`permission_id`),
    CONSTRAINT `cloud_role_permission_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `cloud_role` (`role_id`),
    CONSTRAINT `cloud_role_permission_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `cloud_permission` (`permission_id`)
  ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
  ```

  

- 

## LogCenter—日志中心

## ComponentCenter—组件管理中心