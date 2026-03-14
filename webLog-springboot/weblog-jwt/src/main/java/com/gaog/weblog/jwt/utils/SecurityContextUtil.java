package com.gaog.weblog.jwt.utils;

import com.gaog.weblog.common.enums.ResponseCodeEnum;
import com.gaog.weblog.common.exception.BizException;
import com.gaog.weblog.jwt.model.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

/**
 * Security Context Utility for accessing current user information
 */
@Slf4j
public class SecurityContextUtil {

    /**
     * Get current user details
     *
     * @return CustomUserDetails or null if not authenticated or wrong type
     */
    public static CustomUserDetails getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            if (Objects.isNull(userDetails)) {
                throw new BizException(ResponseCodeEnum.UNAUTHORIZED);
            }
            return userDetails;
        } else {
            throw new BizException(ResponseCodeEnum.UNAUTHORIZED);
        }
    }

    /**
     * Get current user ID
     *
     * @return User ID
     */
    public static Long getCurrentUserId() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.getUserId() : null;
    }

    /**
     * Get current username
     *
     * @return Username
     */
    public static String getCurrentUsername() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.getUsername() : null;
    }

    /**
     * Get current user email
     *
     * @return Email
     */
    public static String getCurrentUserEmail() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.getEmail() : null;
    }

    /**
     * Get current user avatar
     *
     * @return Avatar URL
     */
    public static String getCurrentUserAvatar() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.getAvatar() : null;
    }

    /**
     * Get current user nickname
     *
     * @return Nickname
     */
    public static String getCurrentUserNickname() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.getNickname() : null;
    }
}
