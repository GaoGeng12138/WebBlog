# TokenAuthenticationFilter Refactoring - Storing More User Information

## Overview
This document explains how to refactor `TokenAuthenticationFilter` to store and access more user information in the Spring Security authentication context.

## What Changed

### 1. **Created `CustomUserDetails` Class**
**Location:** `weblog-jwt/src/main/java/com/gaog/weblog/jwt/model/CustomUserDetails.java`

A custom implementation of Spring Security's `UserDetails` interface that stores additional user information:
- `userId` - User ID (Long)
- `username` - Username
- `password` - Encrypted password
- `avatar` - Avatar URL
- `nickname` - User nickname
- `email` - User email
- `introduction` - User bio/introduction
- `roles` - List of user roles
- Account status flags (enabled, expired, locked, etc.)

### 2. **Updated `UserDetailServiceImpl`**
**Location:** `weblog-web/src/main/java/com/gaog/weblog/web/service/UserDetailServiceImpl.java`

Changed from returning `org.springframework.security.core.userdetails.User` to returning `CustomUserDetails`:

```java
// OLD CODE:
return User.withUsername(userDO.getUsername())
        .password(userDO.getPassword())
        .authorities(roleArr)
        .build();

// NEW CODE:
return CustomUserDetails.builder()
        .userId(userDO.getId())
        .username(userDO.getUsername())
        .password(userDO.getPassword())
        .avatar(userDO.getAvatar())
        .nickname(userDO.getNickname())
        .email(userDO.getEmail())
        .introduction(userDO.getIntroduction())
        .roles(roles)
        .enabled(true)
        .accountNonExpired(true)
        .credentialsNonExpired(true)
        .accountNonLocked(true)
        .build();
```

### 3. **Created `SecurityContextUtil` Helper**
**Location:** `weblog-jwt/src/main/java/com/gaog/weblog/jwt/utils/SecurityContextUtil.java`

A utility class to easily access current user information anywhere in your application:

```java
// Get current user details
CustomUserDetails user = SecurityContextUtil.getCurrentUser();

// Get specific fields
Long userId = SecurityContextUtil.getCurrentUserId();
String username = SecurityContextUtil.getCurrentUsername();
String email = SecurityContextUtil.getCurrentUserEmail();
String avatar = SecurityContextUtil.getCurrentUserAvatar();
String nickname = SecurityContextUtil.getCurrentUserNickname();
```

### 4. **Updated `FavoriteServiceImpl`**
**Location:** `weblog-web/src/main/java/com/gaog/weblog/web/service/impl/FavoriteServiceImpl.java`

Changed from casting to String to using `CustomUserDetails`:

```java
// OLD CODE:
String currentUser = (String) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
Long userId = Long.valueOf(currentUser);

// NEW CODE (Option 1):
CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
Long userId = userDetails.getUserId();

// NEW CODE (Option 2 - Using utility):
Long userId = SecurityContextUtil.getCurrentUserId();
```

## How to Use in Your Code

### Method 1: Direct Cast (Current Approach)
```java
@Service
public class YourService {
    
    public void someMethod() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        
        Long userId = userDetails.getUserId();
        String email = userDetails.getEmail();
        String avatar = userDetails.getAvatar();
        // ... access any field you need
    }
}
```

### Method 2: Using SecurityContextUtil (Recommended)
```java
@Service
public class YourService {
    
    public void someMethod() {
        // Get user ID directly
        Long userId = SecurityContextUtil.getCurrentUserId();
        
        // Or get full user details
        CustomUserDetails user = SecurityContextUtil.getCurrentUser();
        if (user != null) {
            Long id = user.getUserId();
            String email = user.getEmail();
            String avatar = user.getAvatar();
            String nickname = user.getNickname();
            Collection<String> roles = user.getRoles();
        }
    }
}
```

## Example: Updating UserServiceImpl

```java
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Response getUserInfo() {
        // NEW: Direct access to user info from context
        CustomUserDetails currentUser = SecurityContextUtil.getCurrentUser();
        
        if (currentUser == null) {
            return Response.fail("User not authenticated");
        }
        
        // No need to query database for basic info - it's already in context!
        UserInfoVO userInfoVO = UserInfoVO.builder()
                .userId(currentUser.getUserId())
                .username(currentUser.getUsername())
                .avatar(currentUser.getAvatar())
                .introduction(currentUser.getIntroduction())
                .email(currentUser.getEmail())
                .nickname(currentUser.getNickname())
                .roles(new HashSet<>(currentUser.getRoles()))
                .build();

        return Response.success(userInfoVO);
    }
}
```

## Benefits

1. **Performance**: No need to query database for basic user info - it's already in the security context
2. **Type Safety**: Strongly typed access to user properties (no String casting)
3. **Convenience**: Easy access to user info anywhere in your application
4. **Extensibility**: Easy to add more user fields in the future
5. **Security**: All user data is automatically cleared when the session expires

## Important Notes

### TokenAuthenticationFilter Doesn't Need Changes!
The `TokenAuthenticationFilter` you showed **doesn't need any modifications** because:

1. It calls `userDetailsService.loadUserByUsername(username)` at line 76
2. That method now returns `CustomUserDetails` (after our changes to `UserDetailServiceImpl`)
3. The `CustomUserDetails` implements `UserDetails` interface, so it's compatible
4. Spring Security automatically stores it in the authentication context

```java
// In TokenAuthenticationFilter.java - NO CHANGES NEEDED
UserDetails userDetails = userDetailsService.loadUserByUsername(username);
// ↑ This now returns CustomUserDetails, but the code doesn't need to change!

UsernamePasswordAuthenticationToken authentication = 
    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
// ↑ CustomUserDetails is stored here automatically
```

### Testing

After these changes, test the following scenarios:

1. **Login**: User should be able to log in as normal
2. **Access Protected Endpoints**: All authenticated endpoints should work
3. **Get User Info**: Should return complete user information
4. **Favorite Articles**: Should work with user ID from context

### Migration Path

If you have existing code that casts to String:
```java
// OLD - will break after changes
String username = (String) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
```

Update to:
```java
// NEW Option 1 - Direct access
CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
String username = user.getUsername();

// NEW Option 2 - Using utility (recommended)
String username = SecurityContextUtil.getCurrentUsername();
```

## Additional Customization

To add more fields to `CustomUserDetails`:

1. Add the field to `CustomUserDetails.java`:
```java
private String phoneNumber;
```

2. Update the builder in `UserDetailServiceImpl.java`:
```java
.phoneNumber(userDO.getPhoneNumber())
```

3. Add a helper method in `SecurityContextUtil.java` (optional):
```java
public static String getCurrentUserPhoneNumber() {
    CustomUserDetails userDetails = getCurrentUser();
    return userDetails != null ? userDetails.getPhoneNumber() : null;
}
```

## Troubleshooting

**Issue**: ClassCastException when casting to CustomUserDetails
- **Cause**: Some endpoints might not be authenticated
- **Solution**: Check if authentication exists and principal is of correct type:
```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
    CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
    // ... use user
}
```

**Issue**: User info is null or outdated
- **Cause**: User info is loaded once during authentication
- **Solution**: For critical operations, re-fetch from database. For most operations, context data is sufficient.
