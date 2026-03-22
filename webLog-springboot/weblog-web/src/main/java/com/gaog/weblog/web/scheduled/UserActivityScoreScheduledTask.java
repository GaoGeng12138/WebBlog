package com.gaog.weblog.web.scheduled;

import com.gaog.weblog.common.domain.dos.UserDO;
import com.gaog.weblog.common.domain.mapper.UserMapper;
import com.gaog.weblog.web.service.impl.UserActivityScoreServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Scheduled task for updating user activity scores
 */
@Component
@Slf4j
public class UserActivityScoreScheduledTask {
    
    @Autowired
    private UserActivityScoreServiceImpl userActivityScoreService;
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * Daily task to update user activity scores for the previous day
     * Runs at 1:00 AM every day
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void updateUserActivityScores() {
        try {
            log.info("Starting daily user activity score update task");
            
            // Get yesterday's date
            LocalDate yesterday = LocalDate.now().minusDays(1);
            
            // Get all users
            List<UserDO> users = userMapper.selectList(null);
            
            // Update activity scores for each user
            for (UserDO user : users) {
                try {
                    userActivityScoreService.recalculateActivityScores(
                        user.getId(),
                        yesterday,
                        yesterday
                    );
                    log.debug("Updated activity score for user: {}", user.getId());
                } catch (Exception e) {
                    log.error("Failed to update activity score for user: {}", user.getId(), e);
                }
            }
            
            log.info("Completed daily user activity score update task");
        } catch (Exception e) {
            log.error("Error in daily user activity score update task", e);
        }
    }
}
