# Role Management - Quick Reference

## API Endpoints Summary

### Role Management Endpoints

| Method | Endpoint | Description | Request Body | Authorization |
|--------|----------|-------------|---------------|---------------|
| POST | `/admin/role/add` | Create new role | `AddRoleReqVO` | ROLE_ADMIN |
| POST | `/admin/role/list` | Query role page list | `FindRolePageListReqVO` | ROLE_ADMIN |
| POST | `/admin/role/update` | Update role info | `UpdateRoleReqVO` | ROLE_ADMIN |
| POST | `/admin/role/delete` | Delete role | `DeleteRoleReqVO` | ROLE_ADMIN |
| POST | `/admin/role/select/list` | Get all enabled roles | `FindRoleSelectListReqVO` | ROLE_ADMIN |
| POST | `/admin/role/assign` | Assign roles to user | `AssignRoleReqVO` | ROLE_ADMIN |

### User Management Enhancements

| Method | Endpoint | Description | Request Body | Authorization |
|--------|----------|-------------|---------------|---------------|
| POST | `/admin/user/create` | Create user with roles | `CreateUserReqVO` (now includes `roleIds`) | ROLE_ADMIN |
| POST | `/admin/user/roles` | Get user roles | Query param: `userId` | ROLE_ADMIN |

## Request Body Examples

### AddRoleReqVO - Create Role
```json
{
  "name": "ROLE_MODERATOR",
  "description": "Moderator role for content moderation",
  "isEnabled": true
}
```

### UpdateRoleReqVO - Update Role
```json
{
  "id": 1,
  "description": "Updated description",
  "isEnabled": true
}
```

### DeleteRoleReqVO - Delete Role
```json
{
  "id": 1
}
```

### FindRolePageListReqVO - Query Roles
```json
{
  "current": 1,
  "size": 10,
  "name": "ROLE_",
  "isEnabled": true
}
```

### FindRoleSelectListReqVO - Get Select List
```json
{}
```

### AssignRoleReqVO - Assign Roles to User
```json
{
  "userId": 1,
  "roleIds": [1, 2, 3]
}
```

### CreateUserReqVO - Create User with Roles
```json
{
  "username": "john_doe",
  "password": "SecurePass123",
  "email": "john@example.com",
  "nickname": "John Doe",
  "introduction": "Software Developer",
  "avatar": "https://example.com/avatar.jpg",
  "isEnabled": true,
  "roleIds": [2, 3]
}
```

## Default Roles

| Role Name | Description | ID |
|-----------|-------------|-----|
| ROLE_ADMIN | Administrator role with full access | 1 |
| ROLE_EDITOR | Editor role can publish and edit articles | 2 |
| ROLE_VISITOR | Visitor role can only view articles and comment | 3 |

## Response Status Codes

| Code | Message | Meaning |
|------|---------|---------|
| 20028 | 角色名称已存在 | Role name already exists |
| 20029 | 角色不存在 | Role not found |
| 20030 | 角色未启用 | Role is not enabled |
| 20031 | 分配角色失败 | Role assignment failed |
| 20032 | 更新角色失败 | Role update failed |
| 20033 | 删除角色失败 | Role deletion failed |
| 20034 | 创建角色失败 | Role creation failed |

## Database Schema

### t_role Table
```sql
CREATE TABLE t_role (
  id bigint(20) unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name varchar(60) NOT NULL UNIQUE,
  description varchar(255),
  is_enabled tinyint(2) NOT NULL DEFAULT 1,
  create_time datetime DEFAULT CURRENT_TIMESTAMP,
  update_time datetime DEFAULT CURRENT_TIMESTAMP,
  is_deleted tinyint(2) NOT NULL DEFAULT 0
);
```

### t_user_role Table (Existing)
```sql
CREATE TABLE t_user_role (
  id bigint(20) unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username varchar(60) NOT NULL,
  role varchar(60) NOT NULL,
  create_time datetime DEFAULT CURRENT_TIMESTAMP,
  KEY idx_username (username)
);
```

## File Locations

### Core Classes
- **RoleDO**: `weblog-common/.../domain/dos/RoleDO.java`
- **RoleMapper**: `weblog-common/.../domain/mapper/RoleMapper.java`
- **AdminRoleService**: `weblog-admin/.../service/AdminRoleService.java`
- **AdminRoleServiceImpl**: `weblog-admin/.../service/impl/AdminRoleServiceImpl.java`
- **AdminRoleController**: `weblog-admin/.../controller/AdminRoleController.java`

### VO Classes
- All located in: `weblog-admin/.../model/vo/role/`
- `AddRoleReqVO.java`
- `UpdateRoleReqVO.java`
- `DeleteRoleReqVO.java`
- `FindRolePageListReqVO.java`
- `FindRolePageListRspVO.java`
- `FindRoleSelectListReqVO.java`
- `FindRoleSelectListRspVO.java`
- `AssignRoleReqVO.java`

### Configuration
- **Response Codes**: `weblog-common/.../enums/ResponseCodeEnum.java`
- **Database Migration**: `weblog-web/src/main/resources/sql/AddRoleTable.sql`

## Common Tasks

### Task: Create a New Role
```bash
POST /admin/role/add
Body: {
  "name": "ROLE_CONTENT_REVIEWER",
  "description": "Review content before publishing",
  "isEnabled": true
}
```

### Task: Get All Available Roles (for dropdown)
```bash
POST /admin/role/select/list
Body: {}
```

### Task: Assign Multiple Roles to User
```bash
POST /admin/role/assign
Body: {
  "userId": 5,
  "roleIds": [1, 2, 3]
}
```

### Task: Get User's Current Roles
```bash
POST /admin/user/roles?userId=5
```

### Task: List All Users with Pagination
```bash
POST /admin/user/list
Body: {
  "current": 1,
  "size": 10,
  "username": "",
  "isEnabled": true
}
```

## Spring Security Integration

All role endpoints are protected with:
```java
@PreAuthorize("hasRole('ROLE_ADMIN')")
```

User roles are loaded from `t_user_role` table during authentication via `CustomUserDetails`.

## Key Features

✅ Full role CRUD operations  
✅ Pagination support for role listing  
✅ Fuzzy search on role names  
✅ Batch role assignment to users  
✅ Role status management (enabled/disabled)  
✅ Logical deletion support  
✅ Transaction safety  
✅ Comprehensive error handling  
✅ Swagger API documentation  
✅ Spring Security integration  

## Performance Considerations

- Role queries include indexes on `name`, `is_enabled`, and `create_time`
- Use selection list endpoint for dropdowns instead of full page list
- Role assignments are transactional to ensure data consistency
- Consider caching enabled roles in production

## Error Handling

All endpoints return consistent error responses:
```json
{
  "code": "20029",
  "msg": "角色不存在",
  "success": false
}
```

Check `ResponseCodeEnum` for all possible error codes.

## Testing Tips

1. Use Postman or curl to test endpoints
2. Include JWT token in Authorization header: `Bearer YOUR_TOKEN`
3. Test with different page sizes for pagination
4. Verify role assignment updates user's login roles immediately
5. Test edge cases like duplicate role names, non-existent roles, etc.

## Documentation References

- Full Feature Documentation: `RoleManagementFeature.md`
- Setup Guide: `RoleManagementSetup.md`
- This Quick Reference: `RoleManagementQuickReference.md`

---
**Last Updated**: 2025-12-18  
**Version**: 1.0
