# User Activity Score Feature

## Overview
The User Activity Score feature quantifies user engagement and contribution on the platform through numerical scores. This helps incentivize participation and provides administrators with insights into user behavior.

## Purpose
1. **Quantify User Contribution**: Display user engagement through numerical scores
2. **Incentivize Participation**: Use leaderboards to encourage more active participation
3. **Data Analysis**: Help administrators understand user behavior and platform usage

## Activity Metrics
The system tracks the following user activities:
- Article publications
- Comments
- Favorites/Bookmarks
- Logins

## Scoring System
Each activity type has a weighted score:
- Article publication: 10 points
- Comment: 2 points
- Favorite/Bookmark: 1 point
- Login: 1 point

## API Endpoint
```
POST /api/user/activity-score
```

### Request Parameters
```json
{
  "userId": 123,
  "timeRange": "monthly" // Options: daily, weekly, monthly, yearly, all
}
```

### Response Format
```json
{
  "success": true,
  "data": {
    "totalScore": 85,
    "rank": 15,
    "totalUsers": 1250,
    "activities": [
      {
        "type": "article",
        "count": 5,
        "score": 50
      },
      {
        "type": "comment",
        "count": 12,
        "score": 24
      },
      {
        "type": "favorite",
        "count": 8,
        "score": 8
      },
      {
        "type": "login",
        "count": 20,
        "score": 3
      }
    ],
    "trend": [
      {"date": "2023-01-01", "score": 65},
      {"date": "2023-01-02", "score": 72},
      {"date": "2023-01-03", "score": 85}
    ]
  }
}
```

## Implementation Details

### Database Schema
A new table `t_user_activity_score` stores daily activity scores for each user:
```sql
CREATE TABLE `t_user_activity_score` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint(20) unsigned NOT NULL COMMENT '用户ID',
  `activity_date` date NOT NULL COMMENT '活动日期',
  `daily_score` int NOT NULL DEFAULT '0' COMMENT '当日总积分',
  `article_count` int NOT NULL DEFAULT '0' COMMENT '当日发布文章数',
  `comment_count` int NOT NULL DEFAULT '0' COMMENT '当日评论数',
  `favorite_count` int NOT NULL DEFAULT '0' COMMENT '当日收藏数',
  `login_count` int NOT NULL DEFAULT '0' COMMENT '当日登录次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_date` (`user_id`, `activity_date`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_activity_date` (`activity_date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户活跃度积分表';
```

### Components

1. **UserActivityScoreDO**: Entity class for user activity scores
2. **UserActivityScoreMapper**: MyBatis mapper for database operations
3. **UserActivityScoreReqVO**: Request VO for API parameters
4. **UserActivityScoreRspVO**: Response VO for API responses
5. **UserActivityScoreService**: Service interface
6. **UserActivityScoreServiceImpl**: Service implementation with scoring logic
7. **UserActivityScoreController**: REST controller for API endpoints
8. **UserActivityScoreScheduledTask**: Scheduled task for daily score updates
9. **UserActivityScoreAspect**: Aspect for real-time score updates after user actions

### Real-time Updates
The system uses Aspect-Oriented Programming to update scores in real-time when users perform tracked actions:
- Publishing articles
- Adding comments
- Favoriting content

### Scheduled Updates
A daily scheduled task runs at 1:00 AM to ensure all user scores are up-to-date for the previous day.

## Integration Points
- Uses existing User, Article, Comment, and Favorite entities
- Integrates with Spring Security for user authentication
- Leverages MyBatis Plus for database operations
- Works with existing logging and monitoring systems