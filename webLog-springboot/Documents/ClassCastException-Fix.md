# ClassCastException Fix - Troubleshooting Guide

## Problem
`java.lang.String cannot be cast to com.gaog.weblog.jwt.model.CustomUserDetails`

## Root Cause
This error occurs when the authentication principal is stored as a String (username) instead of `CustomUserDetails` object.

## Solutions Applied

### 1. **Added Safe Utility Methods** ✅
Updated `SecurityContextUtil` to safely handle casting with proper error logging:

```java
@Slf4j
public class SecurityContextUtil {
    public static CustomUserDetails getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                return (CustomUserDetails) authentication.getPrincipal();
            }
            
            // Log warning if principal is not CustomUserDetails
            if (authentication != null && authentication.getPrincipal() != null) {
                log.warn("Principal is not CustomUserDetails, it's: {}", 
                        authentication.getPrincipal().getClass().getName());
            }
        } catch (Exception e) {
            log.error("Error getting current user from security context", e);
        }
        return null;
    }
}
```

### 2. **Updated All Services to Use SecurityContextUtil** ✅
Changed all services to use the safe utility instead of direct casting:

**Files Updated:**
- ✅ `FavoriteServiceImpl.java` - All 4 methods
- ✅ `UserServiceImpl.java` - `getUserInfo()`
- ✅ `AdminUserServiceImpl.java` - `findUserInfo()`

**Pattern Used:**
```java
// OLD (causes ClassCastException)
CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
    .getContext().getAuthentication().getPrincipal();

// NEW (safe with null check)
CustomUserDetails userDetails = SecurityContextUtil.getCurrentUser();
if (userDetails == null) {
    return Response.fail("用户未登录");
}
```

## How to Debug

### Step 1: Check Application Logs
When the error occurs, check the logs for:
```
WARN - Principal is not CustomUserDetails, it's: java.lang.String
```

This will tell you which endpoint is receiving String instead of CustomUserDetails.

### Step 2: Verify UserDetailServiceImpl is Being Used
Add logging to `UserDetailServiceImpl.loadUserByUsername()`:

```java
@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.info("Loading user details for username: {}", username);
    
    
    CustomUserDetails result = CustomUserDetails.builder()
        // ... builder code ...
        .build();
    
    log.info("Loaded CustomUserDetails for user: {}, userId: {}", 
        result.getUsername(), result.getUserId());
    
    return result;
}
```

### Step 3: Check Token Authentication Flow
Add logging to `TokenAuthenticationFilter.doFilterInternal()`:

```java
// After line 76
UserDetails userDetails = userDetailsService.loadUserByUsername(username);
log.info("UserDetails type: {}", userDetails.getClass().getName());
log.info("UserDetails instance check: {}", userDetails instanceof CustomUserDetails);
```

### Step 4: Verify Security Configuration
Make sure your security configuration doesn't have conflicting configurations that might be storing username as String.

## Common Scenarios

### Scenario 1: Login Works, Token Validation Fails
**Symptom:** Login successful, but subsequent requests with token fail

**Check:**
1. Is `UserDetailServiceImpl` being called during token validation?
2. Add breakpoint at line 76 in `TokenAuthenticationFilter`
3. Verify that `userDetails` is `CustomUserDetails` type

### Scenario 2: Some Endpoints Work, Others Don't
**Symptom:** `/admin/*` works but `/favorite/*` fails (or vice versa)

**Possible Cause:** Different security configurations for different URL patterns

**Solution:** Check `WebSecurityConfig` for URL-specific configurations

### Scenario 3: Anonymous Access
**Symptom:** Error occurs on endpoints that should be accessible without authentication

**Solution:** Check if endpoint requires authentication:
```java
@GetMapping("/check")
@PreAuthorize("hasRole('ROLE_USER')") // This requires authentication
public Response isFavorited(@RequestParam Long articleId) {
    // This will fail if accessed without token
}
```

If endpoint should work without authentication, make it optional:
```java
public Response isFavorited(@RequestParam Long articleId) {
    CustomUserDetails user = SecurityContextUtil.getCurrentUser();
    if (user == null) {
        // Return default response for anonymous users
        return Response.success(false);
    }
    // ... check favorite for logged-in user
}
```

## Testing Checklist

### ✅ Test After Changes:

1. **Login Test**
   - Login with valid credentials
   - Verify token is returned
   - Check logs for "Loaded CustomUserDetails"

2. **Authenticated Request Test**
   - Call an authenticated endpoint with token
   - Should work without ClassCastException
   - Check logs for principal type

3. **Get User Info Test**
   - Call `/admin/user/info` or equivalent
   - Should return user details without database query
   - Verify response contains all fields

4. **Favorite Operations Test**
   - Add favorite
   - Remove favorite
   - List favorites
   - Check if favorited

5. **Anonymous Access Test (if applicable)**
   - Access public endpoints without token
   - Should not throw ClassCastException

## Quick Fixes for Specific Errors

### Error: "Principal is not CustomUserDetails, it's: java.lang.String"

**Fix 1: Clear Sessions**
```bash
# If using Redis for sessions
redis-cli FLUSHDB

# If using in-memory sessions, restart application
```

**Fix 2: Regenerate Token**
- Login again to get a new token
- Old tokens might still have username as principal

**Fix 3: Check Multiple UserDetailsService**
- Ensure only one `UserDetailsService` bean exists
- Check if there are multiple `@Service` implementations

### Error: NullPointerException after getting user

**Cause:** `getCurrentUser()` returned null

**Fix:** Always check for null:
```java
CustomUserDetails user = SecurityContextUtil.getCurrentUser();
if (user == null) {
    log.error("User not authenticated for endpoint: {}", request.getRequestURI());
    return Response.fail("用户未登录");
}
```

## Verification Commands

### 1. Check if JWT contains correct data
Decode your JWT token at https://jwt.io/
- Should contain username in payload
- `TokenAuthenticationFilter` will use this to load `CustomUserDetails`

### 2. Test with curl
```bash
# Login
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Save the token from response

# Test authenticated endpoint
curl -X GET http://localhost:8080/favorite/check?articleId=1 \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 3. Enable Debug Logging
Add to `application.yml`:
```yaml
logging:
  level:
    com.gaog.weblog.jwt.fillter: DEBUG
    com.gaog.weblog.jwt.utils: DEBUG
    org.springframework.security: DEBUG
```

## Prevention

To prevent this issue in the future:

1. **Always use `SecurityContextUtil`** instead of direct casting
2. **Always check for null** before using user details
3. **Add logging** in critical authentication points
4. **Write integration tests** that verify authentication flow
5. **Document** which endpoints require authentication

## Still Having Issues?

If the problem persists:

1. **Check the exact line** where the error occurs
2. **Share the stack trace** - it will show which method is failing
3. **Verify** that you've recompiled and restarted the application
4. **Clear all sessions/tokens** and try fresh login
5. **Check for multiple** `UserDetailsService` implementations
