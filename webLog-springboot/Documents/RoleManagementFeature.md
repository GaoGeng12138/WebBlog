# Role Management Feature Implementation

## Overview
A complete role management system has been implemented for the webLog-springboot project. This feature allows administrators to manage system roles and assign them to users with granular permission control.

## Components Implemented

### 1. Domain Model (RoleDO)
**File**: `weblog-common/src/main/java/com/gaog/weblog/common/domain/dos/RoleDO.java`

- **Fields**:
  - `id`: Role ID (Primary Key)
  - `name`: Role name (e.g., ROLE_ADMIN, ROLE_EDITOR, ROLE_VISITOR)
  - `description`: Role description
  - `isEnabled`: Role status (true = enabled, false = disabled)
  - `createTime`: Creation timestamp
  - `updateTime`: Last update timestamp
  - `isDeleted`: Logical delete flag (0 = not deleted, 1 = deleted)

- **Table**: `t_role`

### 2. Data Access Layer

#### RoleMapper
**File**: `weblog-common/src/main/java/com/gaog/weblog/common/domain/mapper/RoleMapper.java`

Methods:
- `selectByName(String roleName)`: Query role by name
- `selectByIdNotDeleted(Long id)`: Query role by ID excluding deleted roles
- `selectBatchIds(List<Long> ids)`: Batch query roles (inherited from BaseMapper)
- `insert(RoleDO roleDO)`: Insert new role
- `update(RoleDO roleDO, Wrapper wrapper)`: Update role
- `delete(Wrapper wrapper)`: Delete role
- `selectList(Wrapper wrapper)`: Query role list
- `selectPage(Page page, Wrapper wrapper)`: Query role page

#### UserRoleMapper Updates
**File**: `weblog-common/src/main/java/com/gaog/weblog/common/domain/mapper/UserRoleMapper.java`

New Methods:
- `deleteByUsername(String username)`: Delete all roles for a user by username

### 3. API Request/Response Objects (VOs)

#### Role Management VOs
Located in: `weblog-admin/src/main/java/com/gaog/weblog/admin/model/vo/role/`

1. **AddRoleReqVO** - Create role request
   - `name`: Role name (required)
   - `description`: Role description
   - `isEnabled`: Role status (required)

2. **UpdateRoleReqVO** - Update role request
   - `id`: Role ID (required)
   - `description`: Role description
   - `isEnabled`: Role status

3. **DeleteRoleReqVO** - Delete role request
   - `id`: Role ID (required)

4. **FindRolePageListReqVO** - Query role page list request
   - `current`: Page number
   - `size`: Page size
   - `name`: Role name (supports fuzzy search)
   - `isEnabled`: Role status filter

5. **FindRolePageListRspVO** - Query role page list response
   - `id`: Role ID
   - `name`: Role name
   - `description`: Role description
   - `isEnabled`: Role status
   - `createTime`: Creation time
   - `updateTime`: Update time

6. **FindRoleSelectListReqVO** - Query role select list request
   - Empty VO for querying all enabled roles

7. **FindRoleSelectListRspVO** - Query role select list response
   - `id`: Role ID
   - `name`: Role name
   - `description`: Role description

8. **AssignRoleReqVO** - Assign role to user request
   - `userId`: User ID (required)
   - `roleIds`: List of role IDs (required)

#### User Creation Enhancement
Updated: `weblog-admin/src/main/java/com/gaog/weblog/admin/model/vo/user/CreateUserReqVO.java`

New Field:
- `roleIds`: List of role IDs to assign during user creation (optional)

### 4. Service Layer

#### AdminRoleService Interface
**File**: `weblog-admin/src/main/java/com/gaog/weblog/admin/service/AdminRoleService.java`

Methods:
- `addRole(AddRoleReqVO)`: Create a new role
- `findRolePageList(FindRolePageListReqVO)`: Query role page list with filters
- `updateRole(UpdateRoleReqVO)`: Update role information
- `deleteRole(DeleteRoleReqVO)`: Delete role (logical delete)
- `findRoleSelectList(FindRoleSelectListReqVO)`: Query all enabled roles for selection
- `assignRole(AssignRoleReqVO)`: Assign roles to a user

#### AdminRoleServiceImpl Implementation
**File**: `weblog-admin/src/main/java/com/gaog/weblog/admin/service/impl/AdminRoleServiceImpl.java`

Features:
- Transaction management with `@Transactional`
- Input validation and error handling
- Support for logical deletion
- Batch role assignment to users
- Comprehensive logging for audit trail

#### AdminUserService Enhancement
Updated: `weblog-admin/src/main/java/com/gaog/weblog/admin/service/AdminUserService.java`

New Methods:
- `getUserRoles(Long userId)`: Get all roles assigned to a user

#### AdminUserServiceImpl Enhancement
Updated: `weblog-admin/src/main/java/com/gaog/weblog/admin/service/impl/AdminUserServiceImpl.java`

Enhancements:
- `createUser()`: Now supports assigning roles during user creation
- `getUserRoles()`: New method to retrieve user's roles

### 5. Controller Layer

#### AdminRoleController
**File**: `weblog-admin/src/main/java/com/gaog/weblog/admin/controller/AdminRoleController.java`

Endpoints:
- `POST /admin/role/add` - Create a new role
- `POST /admin/role/list` - Query role page list
- `POST /admin/role/update` - Update role information
- `POST /admin/role/delete` - Delete role
- `POST /admin/role/select/list` - Query role select list
- `POST /admin/role/assign` - Assign roles to user

All endpoints require `@PreAuthorize("hasRole('ROLE_ADMIN')")` authorization.

#### AdminUserController Enhancement
Updated: `weblog-admin/src/main/java/com/gaog/weblog/admin/controller/AdminUserController.java`

New Endpoint:
- `POST /admin/user/roles` - Get user roles (query parameter: userId)

### 6. Response Enums

Updated: `weblog-common/src/main/java/com/gaog/weblog/common/enums/ResponseCodeEnum.java`

New Error Codes:
- `ROLE_NAME_ALREADY_EXISTS("20028")`: Role name already exists
- `ROLE_NOT_FOUND("20029")`: Role not found
- `ROLE_NOT_ENABLED("20030")`: Role is not enabled
- `ASSIGN_ROLE_FAILED("20031")`: Role assignment failed
- `UPDATE_ROLE_FAILED("20032")`: Role update failed
- `DELETE_ROLE_FAILED("20033")`: Role delete failed
- `CREATE_ROLE_FAILED("20034")`: Role creation failed

### 7. Database Schema

#### SQL Migration Script
**File**: `weblog-web/src/main/resources/sql/AddRoleTable.sql`

Role Table Definition:
```sql
CREATE TABLE `t_role` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Role ID',
    `name` varchar(60) NOT NULL COMMENT 'Role name',
    `description` varchar(255) DEFAULT NULL COMMENT 'Role description',
    `is_enabled` tinyint(2) NOT NULL DEFAULT '1' COMMENT 'Role status',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `is_deleted` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Logical delete',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    KEY `idx_is_enabled` (`is_enabled`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
```

Default Roles:
- `ROLE_ADMIN`: Administrator role with full access
- `ROLE_EDITOR`: Editor role can publish and edit articles
- `ROLE_VISITOR`: Visitor role can only view articles and comment

## Integration Points

### With Spring Security
- All controller endpoints are protected with `@PreAuthorize("hasRole('ROLE_ADMIN')")`
- Roles are integrated with the existing `CustomUserDetails` model
- User roles are loaded from `t_user_role` table during authentication

### With Existing User Management
- Roles can be assigned during user creation
- Roles can be modified after user creation using the assign role endpoint
- User role information is included in user list queries

## API Usage Examples

### Create a Role
```bash
POST /admin/role/add
Content-Type: application/json

{
  "name": "ROLE_MODERATOR",
  "description": "Moderator role for content moderation",
  "isEnabled": true
}
```

### Query Role Page List
```bash
POST /admin/role/list
Content-Type: application/json

{
  "current": 1,
  "size": 10,
  "name": "ROLE_",
  "isEnabled": true
}
```

### Assign Roles to User
```bash
POST /admin/role/assign
Content-Type: application/json

{
  "userId": 1,
  "roleIds": [1, 2, 3]
}
```

### Get User Roles
```bash
POST /admin/user/roles?userId=1
```

### Create User with Roles
```bash
POST /admin/user/create
Content-Type: application/json

{
  "username": "john_doe",
  "password": "SecurePass123",
  "email": "john@example.com",
  "nickname": "John",
  "introduction": "Software Developer",
  "avatar": "https://example.com/avatar.jpg",
  "isEnabled": true,
  "roleIds": [2, 3]
}
```

## Security Features

1. **Authorization Control**: All role management endpoints require ROLE_ADMIN authorization
2. **Input Validation**: All requests are validated using JSR-303 annotations
3. **Transaction Safety**: All data modifications are wrapped in transactions
4. **Logical Deletion**: Roles are soft-deleted to maintain referential integrity
5. **Error Handling**: Comprehensive error messages and logging for audit trails

## Future Enhancements

1. **Permission Management**: Add granular permission system linked to roles
2. **Role Hierarchy**: Implement role inheritance and hierarchy
3. **Audit Logging**: Enhanced audit logging for role-related operations
4. **API Documentation**: Complete Swagger/OpenAPI documentation
5. **Role Templates**: Pre-built role templates for common scenarios
6. **Bulk Operations**: Batch role assignment for multiple users

## Notes

- The role system uses the existing `t_user_role` junction table format
- Role names follow the Spring Security convention: `ROLE_*`
- All timestamps use LocalDateTime for consistency
- The system supports logical deletion for data integrity
- Duplicate role names are prevented by unique constraint
