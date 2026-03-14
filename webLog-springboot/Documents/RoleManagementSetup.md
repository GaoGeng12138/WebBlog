# Role Management System Setup Guide

## Installation Steps

### 1. Database Setup

Execute the SQL migration script to create the role table and insert default roles:

**File**: `weblog-web/src/main/resources/sql/AddRoleTable.sql`

```bash
mysql -u your_username -p your_database < weblog-web/src/main/resources/sql/AddRoleTable.sql
```

Or execute the SQL directly in your MySQL client:

```sql
-- Copy and paste the entire content of AddRoleTable.sql
```

### 2. Verify Database Schema

After running the migration script, verify the tables were created:

```sql
-- Check if t_role table exists
SHOW TABLES LIKE 't_role';

-- Check role table structure
DESC t_role;

-- Check default roles were inserted
SELECT * FROM t_role;
```

Expected result:
```
+----+---------------+-------------------------------------------+------------+---------------------+---------------------+------------+
| id | name          | description                               | is_enabled | create_time         | update_time         | is_deleted |
+----+---------------+-------------------------------------------+------------+---------------------+---------------------+------------+
| 1  | ROLE_ADMIN    | Administrator role with full access       | 1          | 2025-12-18 ...      | 2025-12-18 ...      | 0          |
| 2  | ROLE_EDITOR   | Editor role can publish and edit articles | 1          | 2025-12-18 ...      | 2025-12-18 ...      | 0          |
| 3  | ROLE_VISITOR  | Visitor role can only view articles ...   | 1          | 2025-12-18 ...      | 2025-12-18 ...      | 0          |
+----+---------------+-------------------------------------------+------------+---------------------+---------------------+------------+
```

### 3. Project Rebuild

After making changes, rebuild the project:

```bash
# Using Maven
mvn clean install

# Or using Maven wrapper
./mvnw clean install

# Or in IntelliJ IDEA
Build > Rebuild Project
```

### 4. Assign Initial Roles

Before using the admin panel, assign roles to existing users. You can do this using:

**Option A: Direct SQL**
```sql
-- Assign ROLE_ADMIN to admin user
INSERT INTO t_user_role (username, role, create_time) 
VALUES ('admin', 'ROLE_ADMIN', NOW());

-- Assign ROLE_EDITOR to editor user
INSERT INTO t_user_role (username, role, create_time) 
VALUES ('editor', 'ROLE_EDITOR', NOW());

-- Assign ROLE_VISITOR to visitor user
INSERT INTO t_user_role (username, role, create_time) 
VALUES ('visitor', 'ROLE_VISITOR', NOW());
```

**Option B: Using the API**

First, ensure you have an admin user logged in, then use:

```bash
# Create a new user with roles
curl -X POST http://localhost:8080/admin/user/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "username": "new_user",
    "password": "SecurePassword123",
    "email": "user@example.com",
    "nickname": "New User",
    "isEnabled": true,
    "roleIds": [1, 2]
  }'

# Or assign roles to existing user
curl -X POST http://localhost:8080/admin/role/assign \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "userId": 1,
    "roleIds": [1, 2]
  }'
```

## File Structure

New files created:

```
weblog-springboot/
├── weblog-admin/src/main/java/com/gaog/weblog/admin/
│   ├── controller/
│   │   └── AdminRoleController.java          (NEW)
│   ├── model/vo/role/                        (NEW DIRECTORY)
│   │   ├── AddRoleReqVO.java
│   │   ├── AssignRoleReqVO.java
│   │   ├── DeleteRoleReqVO.java
│   │   ├── FindRolePageListReqVO.java
│   │   ├── FindRolePageListRspVO.java
│   │   ├── FindRoleSelectListReqVO.java
│   │   ├── FindRoleSelectListRspVO.java
│   │   └── UpdateRoleReqVO.java
│   └── service/
│       ├── AdminRoleService.java             (NEW)
│       └── impl/
│           └── AdminRoleServiceImpl.java      (NEW)
├── weblog-common/src/main/java/com/gaog/weblog/common/
│   ├── domain/
│   │   ├── dos/
│   │   │   └── RoleDO.java                   (NEW)
│   │   └── mapper/
│   │       └── RoleMapper.java               (NEW)
│   └── enums/
│       └── ResponseCodeEnum.java             (UPDATED)
└── weblog-web/src/main/resources/sql/
    └── AddRoleTable.sql                      (NEW)
```

Updated files:

```
weblog-admin/src/main/java/com/gaog/weblog/admin/
├── controller/
│   └── AdminUserController.java              (UPDATED - added getUserRoles endpoint)
├── model/vo/user/
│   └── CreateUserReqVO.java                  (UPDATED - added roleIds field)
└── service/
    ├── AdminUserService.java                 (UPDATED - added getUserRoles method)
    └── impl/
        └── AdminUserServiceImpl.java          (UPDATED - implemented role assignment in createUser)

weblog-common/src/main/java/com/gaog/weblog/common/domain/mapper/
└── UserRoleMapper.java                       (UPDATED - added deleteByUsername method)
```

## Configuration

### Spring Security Configuration

The existing Spring Security configuration in `WebSecurityConfig.java` already supports the role-based access control. All admin endpoints are protected with:

```java
@PreAuthorize("hasRole('ROLE_ADMIN')")
```

### Swagger/API Documentation

The role management API endpoints are automatically documented in Swagger UI with:
- API tags: "Admin 角色管理" (Admin Role Management)
- Operation descriptions and parameter documentation
- Request/response schemas

To view the API documentation:
1. Start the application
2. Navigate to: `http://localhost:8080/doc.html` (Knife4j) or `http://localhost:8080/swagger-ui.html` (Swagger UI)

## Testing

### Manual Testing via cURL

**1. Create a Role:**
```bash
curl -X POST http://localhost:8080/admin/role/add \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "ROLE_CONTENT_MANAGER",
    "description": "Manager for content moderation",
    "isEnabled": true
  }'
```

**2. List Roles:**
```bash
curl -X POST http://localhost:8080/admin/role/list \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "current": 1,
    "size": 10
  }'
```

**3. Update a Role:**
```bash
curl -X POST http://localhost:8080/admin/role/update \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "id": 1,
    "description": "Updated description",
    "isEnabled": true
  }'
```

**4. Get Role Selection List:**
```bash
curl -X POST http://localhost:8080/admin/role/select/list \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{}'
```

**5. Assign Roles to User:**
```bash
curl -X POST http://localhost:8080/admin/role/assign \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "userId": 1,
    "roleIds": [1, 2, 3]
  }'
```

**6. Get User Roles:**
```bash
curl -X POST "http://localhost:8080/admin/user/roles?userId=1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Using Postman

1. Import the role management API endpoints into Postman
2. Set up a Bearer token for your admin user
3. Test each endpoint with the provided request bodies
4. Verify responses match the expected VO structures

## Troubleshooting

### Issue: "Table 't_role' doesn't exist"

**Solution**: Run the SQL migration script from `AddRoleTable.sql`

### Issue: "Permission denied" error when accessing role endpoints

**Solution**: Ensure the user is logged in as an admin and has ROLE_ADMIN assigned

### Issue: Roles not appearing after creation

**Solution**: 
1. Check if the role was inserted successfully: `SELECT * FROM t_role;`
2. Verify the `is_deleted` flag is 0
3. Verify the `is_enabled` flag is 1 for selection list

### Issue: Role assignment fails

**Solution**:
1. Verify user exists: `SELECT * FROM t_user WHERE id = ?;`
2. Verify role exists: `SELECT * FROM t_role WHERE id = ?;`
3. Check for transaction errors in the application logs

## Performance Optimization Tips

1. **Indexing**: The t_role table includes indexes on:
   - `name` (UNIQUE)
   - `is_enabled`
   - `create_time`

2. **Query Optimization**: Use role selection list for dropdowns instead of full role list

3. **Caching**: Consider caching enabled roles for selection lists

4. **Batch Operations**: Use the batch role assignment endpoint for multiple users

## Next Steps

1. ✅ Set up the database schema
2. ✅ Assign initial roles to existing users
3. ✅ Test role management API endpoints
4. ✅ Integrate role management into your frontend UI
5. ✅ Configure any additional role-specific permissions
6. ✅ Set up role-based access logging for audit trails

## Additional Resources

- **Role Management Implementation**: See `RoleManagementFeature.md` for detailed documentation
- **Spring Security Documentation**: https://spring.io/projects/spring-security
- **MyBatis Plus Documentation**: https://baomidou.com/
- **Knife4j Documentation**: https://doc.xiaomubai.com/

## Support

For issues or questions regarding the role management system:

1. Check the application logs for detailed error messages
2. Review the `RoleManagementFeature.md` documentation
3. Verify database schema matches the SQL migration script
4. Ensure all Spring dependencies are properly configured
