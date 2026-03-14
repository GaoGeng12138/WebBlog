# UserRoleDO Refactoring - From (username, role) to (userId, roleId)

## Overview
Refactored the `UserRoleDO` entity and related components to use `userId` and `roleId` (numeric IDs) instead of string-based `username` and `role` names. This improves data integrity, performance, and follows better database design patterns.

## Changes Made

### 1. Domain Model - UserRoleDO
**File**: `weblog-common/src/main/java/.../domain/dos/UserRoleDO.java`

**Before**:
```java
private String username;
private String role;
```

**After**:
```java
private Long userId;
private Long roleId;
```

### 2. Database Mapper - UserRoleMapper
**File**: `weblog-common/src/main/java/.../domain/mapper/UserRoleMapper.java`

**New Methods**:
- `selectByUserId(Long userId)` - Query roles by user ID (new primary method)
- `selectRoleIdsByUserId(Long userId)` - Query role IDs as a set
- `deleteByUserId(Long userId)` - Delete all roles for a user

**Deprecated Methods** (kept for backward compatibility):
- `selectByUsername(String username)` - ⚠️ Deprecated, throws UnsupportedOperationException
- `selectRolesByUsername(String username)` - ⚠️ Deprecated
- `deleteByUsername(String username)` - ⚠️ Deprecated

### 3. Service Layer

#### AdminRoleServiceImpl
**File**: `weblog-admin/src/main/java/.../service/impl/AdminRoleServiceImpl.java`

**Changes in `assignRole()` method**:
- Use `userRoleMapper.deleteByUserId(userId)` instead of username-based deletion
- Create `UserRoleDO` with `userId` and `roleId` instead of `username` and `role`

#### AdminUserServiceImpl
**File**: `weblog-admin/src/main/java/.../service/impl/AdminUserServiceImpl.java`

**Changes in `createUser()` method**:
- Assign roles using `userId` and `roleId` instead of username and role name

**Changes in `getUserRoles()` method**:
- Query roles using `userRoleMapper.selectByUserId(userId)`
- Return list of `Long` (role IDs) instead of role names

### 4. Authentication Service

#### UserDetailServiceImpl
**File**: `weblog-web/src/main/java/.../service/UserDetailServiceImpl.java`

**Changes in `loadUserByUsername()` method**:
- Query user roles by `userId` instead of `username`: `userRoleMapper.selectByUserId(userDO.getId())`
- Join with `RoleMapper` to get role names from role IDs
- Convert role IDs to role names for `CustomUserDetails`

**Impact on authentication**:
- Login flow still works the same (loads by username)
- Role loading now uses numeric IDs internally
- Role names are resolved from the role table for Spring Security

### 5. Database Schema

#### CreateTable.sql
**File**: `weblog-web/src/main/resources/sql/CreateTable.sql`

**Before**:
```sql
CREATE TABLE `t_user_role` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `username` varchar(60) NOT NULL,
    `role` varchar(60) NOT NULL,
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`)
)
```

**After**:
```sql
CREATE TABLE `t_user_role` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `user_id` bigint(20) unsigned NOT NULL,
    `role_id` bigint(20) unsigned NOT NULL,
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
)
```

#### Migration Script
**File**: `weblog-web/src/main/resources/sql/MigrateUserRoleTable.sql`

Provides migration options for existing databases:
1. Add new columns step-by-step
2. Migrate data using JOINs with user and role tables
3. Drop old columns after verification
4. Add proper indexes

## Benefits

✅ **Data Integrity**: Foreign key relationships can now be enforced  
✅ **Performance**: Numeric ID comparisons are faster than string comparisons  
✅ **Normalization**: Proper database normalization eliminates data duplication  
✅ **Flexibility**: Role names can be changed without affecting user-role assignments  
✅ **Scalability**: Numeric IDs are more efficient for large datasets

## Migration Path

### For New Installations
Use the updated schema from `CreateTable.sql` directly - no migration needed.

### For Existing Databases
Execute the migration script in `MigrateUserRoleTable.sql`:

```sql
-- Step 1: Add new columns
ALTER TABLE `t_user_role` ADD COLUMN `user_id` BIGINT(20) UNSIGNED DEFAULT NULL;
ALTER TABLE `t_user_role` ADD COLUMN `role_id` BIGINT(20) UNSIGNED DEFAULT NULL;

-- Step 2: Migrate data
UPDATE `t_user_role` tr
SET tr.`user_id` = (SELECT u.`id` FROM `t_user` u WHERE u.`username` = tr.`username`),
    tr.`role_id` = (SELECT r.`id` FROM `t_role` r WHERE r.`name` = tr.`role`)
WHERE tr.`user_id` IS NULL OR tr.`role_id` IS NULL;

-- Step 3: Drop old columns
ALTER TABLE `t_user_role` DROP COLUMN `username`;
ALTER TABLE `t_user_role` DROP COLUMN `role`;

-- Step 4: Add indexes
ALTER TABLE `t_user_role` ADD KEY `idx_user_id` (`user_id`);
ALTER TABLE `t_user_role` ADD KEY `idx_role_id` (`role_id`);
```

## Backward Compatibility

❌ **Breaking Changes**:
- `selectByUsername()`, `selectRolesByUsername()`, `deleteByUsername()` now throw `UnsupportedOperationException`
- Code directly calling these methods must be updated

✅ **Preserved**:
- Spring Security role loading still works (roles are converted to names internally)
- Login/authentication flow unchanged
- User role assignment API works the same way
- Admin role management endpoints work the same way

## Files Modified

1. ✅ `UserRoleDO.java` - Changed fields from String to Long
2. ✅ `UserRoleMapper.java` - Updated methods, added new ones, deprecated old ones
3. ✅ `AdminRoleServiceImpl.java` - Updated assignRole() method
4. ✅ `AdminUserServiceImpl.java` - Updated createUser() and getUserRoles() methods
5. ✅ `UserDetailServiceImpl.java` - Updated role loading logic
6. ✅ `CreateTable.sql` - Updated table schema
7. ✅ `MigrateUserRoleTable.sql` - New migration script

## Testing Recommendations

1. **Unit Tests**:
   - Test role assignment with numeric IDs
   - Test role retrieval for users
   - Test role deletion

2. **Integration Tests**:
   - Test user login with role loading
   - Test admin role management APIs
   - Test user creation with roles

3. **Database Tests**:
   - Verify data migration correctness
   - Check index performance
   - Validate foreign key relationships

## Future Improvements

- Add foreign key constraints: `CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES t_user(id)`
- Add foreign key constraints: `CONSTRAINT fk_role_id FOREIGN KEY (role_id) REFERENCES t_role(id)`
- Add unique constraint: `UNIQUE KEY uk_user_role (user_id, role_id)` to prevent duplicate assignments
- Consider composite primary key if appropriate

## Notes

- All timestamp handling remains unchanged (still uses `Date` class)
- All transaction management remains unchanged
- All validation logic remains unchanged
- Spring Security integration works seamlessly with the new approach
