package com.gaog.weblog.common.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gaog.weblog.common.domain.dos.ArticleDO;
import com.gaog.weblog.common.domain.dos.CategoryDO;
import com.gaog.weblog.common.domain.mapper.ArticleAccessUserMapper;
import com.gaog.weblog.common.domain.mapper.CategoryMapper;
import com.gaog.weblog.common.domain.mapper.CategoryAccessUserMapper;
import com.gaog.weblog.common.enums.VisibilityScopeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ContentVisibilityService {

    @Autowired
    private ArticleAccessUserMapper articleAccessUserMapper;
    @Autowired
    private CategoryAccessUserMapper categoryAccessUserMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    public boolean canCurrentUserConfigureVisibility() {
        return hasAnyRole("ROLE_ADMIN", "ROLE_EDITOR");
    }

    public boolean isCurrentUserPrivileged() {
        return hasAnyRole("ROLE_ADMIN", "ROLE_EDITOR");
    }

    public Long getCurrentUserIdSafely() {
        try {
            Object principal = getCurrentPrincipal();
            if (principal == null) {
                return null;
            }

            Method method = principal.getClass().getMethod("getUserId");
            Object value = method.invoke(principal);
            if (value instanceof Long) {
                return (Long) value;
            }
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public Integer normalizeScope(Integer scope) {
        return VisibilityScopeEnum.normalize(scope);
    }

    public List<Long> normalizeAssignedUserIds(List<Long> userIds) {
        if (userIds == null) {
            return Collections.emptyList();
        }

        return userIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    public boolean canAccessCategory(CategoryDO categoryDO) {
        if (categoryDO == null) {
            return false;
        }
        if (isCurrentUserPrivileged()) {
            return true;
        }

        Integer scope = normalizeScope(categoryDO.getVisibilityScope());
        if (VisibilityScopeEnum.PUBLIC.getCode().equals(scope)) {
            return true;
        }

        Long currentUserId = getCurrentUserIdSafely();
        if (currentUserId == null) {
            return false;
        }

        return articleOrCategoryAssigned(
                false,
                categoryDO.getId(),
                currentUserId
        );
    }

    public boolean canAccessArticle(ArticleDO articleDO, Long categoryId) {
        if (articleDO == null) {
            return false;
        }

        Long currentUserId = getCurrentUserIdSafely();
        if (isCurrentUserPrivileged() || Objects.equals(articleDO.getUserId(), currentUserId)) {
            return true;
        }

        Integer articleScope = normalizeScope(articleDO.getVisibilityScope());
        boolean articleAllowed = VisibilityScopeEnum.PUBLIC.getCode().equals(articleScope);
        if (!articleAllowed && currentUserId != null) {
            articleAllowed = articleOrCategoryAssigned(true, articleDO.getId(), currentUserId);
        }
        if (!articleAllowed) {
            return false;
        }

        if (categoryId == null) {
            return true;
        }

        return canAccessCategory(categoryMapper.selectById(categoryId));
    }

    public String buildCategoryAccessibleArticleSubQuery(Long currentUserId) {
        if (currentUserId == null) {
            return "SELECT acr.article_id FROM t_article_category_rel acr " +
                    "JOIN t_category c ON c.id = acr.category_id " +
                    "WHERE c.visibility_scope = 1";
        }

        return "SELECT acr.article_id FROM t_article_category_rel acr " +
                "JOIN t_category c ON c.id = acr.category_id " +
                "WHERE c.visibility_scope = 1 " +
                "OR c.id IN (SELECT category_id FROM t_category_access_user WHERE user_id = " + currentUserId + ")";
    }

    public boolean hasAnyRole(String... roleNames) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getAuthorities() == null) {
                return false;
            }

            for (String roleName : roleNames) {
                boolean matched = authentication.getAuthorities().stream()
                        .anyMatch(authority -> roleName.equals(authority.getAuthority()));
                if (matched) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private Object getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getPrincipal();
    }

    private boolean articleOrCategoryAssigned(boolean article, Long bizId, Long currentUserId) {
        if (article) {
            return articleAccessUserMapper.selectCount(new LambdaQueryWrapper<com.gaog.weblog.common.domain.dos.ArticleAccessUserDO>()
                    .eq(com.gaog.weblog.common.domain.dos.ArticleAccessUserDO::getArticleId, bizId)
                    .eq(com.gaog.weblog.common.domain.dos.ArticleAccessUserDO::getUserId, currentUserId)) > 0;
        }

        return categoryAccessUserMapper.selectCount(new LambdaQueryWrapper<com.gaog.weblog.common.domain.dos.CategoryAccessUserDO>()
                .eq(com.gaog.weblog.common.domain.dos.CategoryAccessUserDO::getCategoryId, bizId)
                .eq(com.gaog.weblog.common.domain.dos.CategoryAccessUserDO::getUserId, currentUserId)) > 0;
    }
}
