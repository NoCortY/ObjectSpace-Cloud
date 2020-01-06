# ObjectSpace-Cloud

## 简介

本项目是http://www.objectspace.cn 网站后台 基于SpringCloud。

## AuthCenter—用户认证授权中心
- ### 架构
  ![1576573890742](C:\Users\NoCortY\AppData\Roaming\Typora\typora-user-images\1576573890742.png)
  
- ### 职能

  

- ### 快速启动

  - #### SQL建表脚本

    ```sql
    CREATE DATABASE cloud_authcenter;
    use cloud_authcenter;
    --认证中心本地权限
    CREATE TABLE `cloud_filter_map` (
      `url` varchar(256) DEFAULT NULL,
      `filter` varchar(256) DEFAULT NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
    --菜单
    CREATE TABLE `cloud_menu_info` (
      `id` int(11) NOT NULL AUTO_INCREMENT,
      `title` varchar(64) DEFAULT NULL,
      `category` varchar(64) DEFAULT NULL,
      `role_id` int(11) DEFAULT NULL,
      `href` varchar(1024) DEFAULT NULL,
      `icon` varchar(512) DEFAULT NULL,
      `page` varchar(64) DEFAULT NULL,
      `parent` int(11) DEFAULT NULL,
      `target` varchar(64) DEFAULT NULL,
      `priority` int(11) DEFAULT NULL,
      `classify` varchar(64) DEFAULT NULL,
      `image` varchar(64) DEFAULT NULL,
      PRIMARY KEY (`id`),
      KEY `cloud_menu_info` (`role_id`),
      CONSTRAINT `cloud_menu_info_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `cloud_role` (`role_id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
    -- 权限
    CREATE TABLE `cloud_permission` (
      `permission_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '权限id',
      `permission_name` varchar(64) DEFAULT NULL COMMENT '权限名',
      `permission_url` varchar(1024) DEFAULT NULL COMMENT '权限url',
      `permission_desc` varchar(1024) DEFAULT NULL COMMENT '权限详细描述',
      `permission_create_date` date DEFAULT NULL COMMENT '权限创建时间',
      `permission_application` varchar(32) DEFAULT NULL COMMENT '权限归属应用',
      PRIMARY KEY (`permission_id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
    --角色
    CREATE TABLE `cloud_role` (
      `role_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色id',
      `role_name` varchar(128) NOT NULL COMMENT '角色名',
      `role_desc` varchar(512) DEFAULT NULL COMMENT '角色详细信息',
      `role_create_date` date DEFAULT NULL COMMENT '角色创建时间',
      `role_application` varchar(64) DEFAULT NULL COMMENT '角色归属应用',
      PRIMARY KEY (`role_id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
    --用户
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
    ) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
    --用户_角色
    CREATE TABLE `cloud_user_role` (
      `id` int(11) NOT NULL AUTO_INCREMENT,
      `user_id` int(11) DEFAULT NULL,
      `role_id` int(11) DEFAULT NULL,
      PRIMARY KEY (`id`),
      KEY `user_id` (`user_id`),
      KEY `role_id` (`role_id`),
      CONSTRAINT `cloud_user_role_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `cloud_user` (`user_id`),
      CONSTRAINT `cloud_user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `cloud_role` (`role_id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
    --角色_权限
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

## LogCenter—日志中心

- ### 快速启动

  - #### SQL建表脚本

    ```sql
    CREATE DATABASE cloud_logcenter;
    USE cloud_logcenter;
    --日志
    CREATE TABLE `cloud_log` (
      `id` int(11) NOT NULL AUTO_INCREMENT,
      `operate_date` date DEFAULT NULL,
      `input_parameter` varchar(1024) DEFAULT NULL,
      `output_parameter` varchar(8192) DEFAULT NULL,
      `operate_interface` varchar(1024) DEFAULT NULL,
      `operate_user_ip` varchar(64) DEFAULT NULL,
      `log_application` int(11) DEFAULT NULL,
      PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=1532 DEFAULT CHARSET=utf8;
    --公告
    CREATE TABLE `cloud_notice` (
      `id` int(11) NOT NULL AUTO_INCREMENT,
      `title` varchar(64) DEFAULT NULL,
      `content` varchar(4096) DEFAULT NULL,
      `create_time` date DEFAULT NULL,
      PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
    ```

    



## ComponentCenter—组件管理中心