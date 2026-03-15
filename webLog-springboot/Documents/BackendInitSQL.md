# 后端初始化 SQL 说明

后端启动时会按下面顺序处理数据库初始化：

1. 执行 `weblog-web/src/main/resources/sql/CreateTable.sql`
   - 用于创建所有业务表
2. 执行 `weblog-web/src/main/resources/sql/InsertData.sql`
   - 用于插入默认角色、管理员账号、管理员角色关联、默认站点设置
3. 执行 `weblog-common/src/main/java/com/gaog/weblog/common/config/DatabaseInitializationConfig.java`
   - 用于给旧库自动补齐缺失字段
   - 当前会自动补：
     - `t_blog_settings.frontend_article_page_size`
     - `t_article.article_source`
     - `t_category.show_on_front`
   - 当前会自动删除：
     - `t_blog_settings.slogan`
     - `t_blog_settings.contact_email`

## 当前初始化后的默认数据

- 默认管理员账号
  - 用户名：`admin`
  - 密码：`123456`
- 默认角色
  - `ROLE_ADMIN`
  - `ROLE_EDITOR`
  - `ROLE_VISITOR`
- 默认站点设置
  - 标题：`ThoughtFlow`
  - 前台文章分页大小：`12`

## 适用场景

- 全新数据库：直接依赖应用启动自动建表和插数
- 旧数据库升级：依赖启动时自动补字段逻辑
