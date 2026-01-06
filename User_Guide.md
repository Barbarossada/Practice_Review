# 📘 Exam Practice System 使用文档

本文档将帮助从零开始部署和使用 Exam Practice 智能刷题系统。

## 📋 目录

1. [项目简介](#1-项目简介)
2. [环境准备](#2-环境准备)
3. [安装与部署](#3-安装与部署)
    - [数据库初始化](#31-数据库初始化)
    - [后端部署 (Backend)](#32-后端部署-backend)
    - [前端部署 (Frontend)](#33-前端部署-frontend)
4. [系统使用指南](#4-系统使用指南)
    - [用户注册与登录](#41-用户注册与登录)
    - [题目管理 (Admin)](#42-题目管理-admin)
    - [在线练习](#43-在线练习)
    - [错题本](#44-错题本)
5. [常见问题](#5-常见问题)

---

## 1. 项目简介

Exam Practice 是一个现代化的期末复习在线题库系统，采用 **Vue 3** + **Spring Boot** 前后端分离架构。支持题目批量导入、智能练习、错题自动收集与复习等功能。

## 2. 环境准备

在开始之前，请确保您的电脑已经安装了以下软件：

*   **Java**: JDK 1.8 或 JDK 17+
*   **Node.js**: v16+ (推荐 v18)
*   **MySQL**: 8.0+
*   **Maven**: 3.6+ (通常集成在 IDEA 中)
*   **Git**: (可选) 用于拉取代码

## 3. 安装与部署

### 3.1 数据库初始化

1.  **创建数据库**:
    使用 Navicat、DBeaver 或 MySQL 命令行工具创建一个名为 `exam_practice` 的数据库。
    ```sql
    CREATE DATABASE exam_practice CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    ```

2.  **导入 SQL 脚本**:
    运行项目根目录下的 `sql/exam_practice.sql` 文件。该脚本会自动创建所需的表结构（`question`, `user`, `practice_record` 等）并初始化必要的管理员账户。

    > **注意**: 脚本中已预置了一个管理员账户：
    > *   用户名: `admin`
    > *   密码: `admin123` (加密存储)

### 3.2 后端部署 (Backend)

后端基于 Spring Boot 开发。

1.  **打开项目**:
    使用 IntelliJ IDEA 打开 `backend` 文件夹。

2.  **配置数据库连接**:
    打开 `src/main/resources/application.yml` (或 `application-dev.yml`)，找到数据库配置部分：
    ```yaml
    spring:
      datasource:
        url: jdbc:mysql://localhost:3306/exam_practice?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
        username: root  # 修改为你的 MySQL 用户名
        password: root  # 修改为你的 MySQL 密码
    ```

3.  **运行项目**:
    找到 `src/main/java/com/exam/ExamApplication.java`，右键点击 **Run 'ExamApplication'**。
    
    当控制台看到 `Started ExamApplication in ...` 字样时，说明后端启动成功，默认端口为 `8080`。

### 3.3 前端部署 (Frontend)

前端基于 Vue 3 + Vite 开发。

1.  **打开终端**:
    在项目根目录打开终端 (CMD 或 PowerShell)，进入 frontend 目录：
    ```bash
    cd frontend
    ```

2.  **安装依赖**:
    ```bash
    npm install
    # 或者使用 yarn
    yarn
    ```

3.  **启动项目**:
    ```bash
    npm run dev
    ```

4.  **访问系统**:
    启动成功后，控制台会显示访问地址（通常是 `http://localhost:5173`）。在浏览器中通过该地址即可访问系统。

---

## 4. 系统使用指南

### 4.1 用户注册与登录

*   打开系统首页，点击"登录"。
*   使用默认管理员账号 `admin / admin123` 登录，或者点击"注册"创建一个新账户。
*   系统使用 Token 认证，支持自动保持登录状态。

### 4.2 题目管理 (Admin)

*   **手动录入**: 在"题目管理"页面点击 **"新增题目"**，支持单选、多选、判断三种题型。
*   **批量导入**: 支持 Excel 文件批量导入。
    *   点击 **"导入"** 按钮。
    *   输入本次导入题目的**科目名称**（如：计算机网络）。
    *   选择 Excel 文件上传。
    *   *提示*: 导入模板格式应包含：题目内容、A、B、C、D、答案、解析、难度等列。
*   **查询与筛选**: 支持按科目、题型、关键词检索题目。

### 4.3 在线练习

*   点击侧边栏 **"开始练习"**。
*   选择要练习的 **科目** 和 **模式**（随机练习 / 顺序练习）。
*   进入答题界面后，提交答案会立即显示正误与解析。
*   练习记录会自动保存。

### 4.4 错题本

*   凡是在练习中做错的题目，系统会自动将其加入 **"错题本"**。
*   在错题本中可以查看错题详情、错误次数。
*   提供 **"错题重练"** 功能，帮助巩固薄弱知识点。
*   当错题在后续练习中答对达到一定熟练度后，系统会提示是否移除。

---

## 5. 常见问题

**Q: 启动后端报错 "Access denied for user ..."**
A: 请检查 `application.yml` 中的数据库用户名和密码是否正确。

**Q: 导入 Excel 失败**
A: 请检查 Excel 格式是否符合要求，建议先手动添加一道题导出 Excel 作为模板。

**Q: 前端无法连接后端**
A: 请确保后端项目已在 `8080` 端口启动。如果修改了后端端口，请同步修改 `frontend/vite.config.js` 中的代理配置。

---

如有其他问题，请查阅 [API.md](./API.md) 文档或联系开发者。
