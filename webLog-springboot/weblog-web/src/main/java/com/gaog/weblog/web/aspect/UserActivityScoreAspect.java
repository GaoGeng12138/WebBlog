package com.gaog.weblog.web.aspect;

import com.gaog.weblog.jwt.utils.SecurityContextUtil;
import com.gaog.weblog.web.service.impl.UserActivityScoreServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Aspect for updating user activity scores after certain actions
 */
@Aspect
@Component
@Slf4j
public class UserActivityScoreAspect {
    
    @Autowired
    private UserActivityScoreServiceImpl userActivityScoreService;
    
    /**
     * Update user activity score after publishing an article
     */
    @AfterReturning(pointcut = "execution(* com.gaog.weblog.web.service.impl.ArticleServiceImpl.publishArticle(..))")
    public void afterPublishArticle() {
        try {
            Long userId = SecurityContextUtil.getCurrentUserId();
            if (userId != null) {
                LocalDate today = LocalDate.now();
                userActivityScoreService.calculateAndSaveActivityScores(userId, today, today);
                log.debug("Updated activity score for user {} after publishing article", userId);
            }
        } catch (Exception e) {
            log.error("Error updating activity score after publishing article", e);
        }
    }
    
    /**
     * Update user activity score after adding a comment
     */
    @AfterReturning(pointcut = "execution(* com.gaog.weblog.web.service.impl.CommentServiceImpl.addComment(..))")
    public void afterAddComment() {
        try {
            Long userId = SecurityContextUtil.getCurrentUserId();
            if (userId != null) {
                LocalDate today = LocalDate.now();
                userActivityScoreService.calculateAndSaveActivityScores(userId, today, today);
                log.debug("Updated activity score for user {} after adding comment", userId);
            }
        } catch (Exception e) {
            log.error("Error updating activity score after adding comment", e);
        }
    }
    
    /**
     * Update user activity score after favoriting an article
     */
    @AfterReturning(pointcut = "execution(* com.gaog.weblog.web.service.impl.FavoriteServiceImpl.favoriteArticle(..))")
    public void afterFavoriteArticle() {
        try {
            Long userId = SecurityContextUtil.getCurrentUserId();
            if (userId != null) {
                LocalDate today = LocalDate.now();
                userActivityScoreService.calculateAndSaveActivityScores(userId, today, today);
                log.debug("Updated activity score for user {} after favoriting article", userId);
            }
        } catch (Exception e) {
            log.error("Error updating activity score after favoriting article", e);
        }
    }
}