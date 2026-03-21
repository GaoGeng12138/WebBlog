package com.gaog.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gaog.weblog.common.domain.dos.ArticleDO;
import com.gaog.weblog.common.enums.ArticleStatusEnum;

import java.time.LocalDate;
import java.util.Objects;

/**
 * @author ZSJ
 * @date 2025/11/25 15:50
 * @description
 */
public interface ArticleMapper extends BaseMapper<ArticleDO> {

    /**
     * 分页查询
     *
     * @param current   当前页码
     * @param size      每页展示的数据量
     * @param title     文章标题
     * @param userId    用户ID
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return
     */
    default Page<ArticleDO> selectPageList(Long current, Long size, String title, Long userId, LocalDate startDate, LocalDate endDate,
                                           Long viewerUserId, boolean privilegedViewer) {
        // 分页对象(查询第几页、每页多少数据)
        Page<ArticleDO> page = new Page<>(current, size);

        // 构建查询条件
        LambdaQueryWrapper<ArticleDO> wrapper = Wrappers.<ArticleDO>lambdaQuery()
                // like 模块查询
                .like(StringUtils.isNotBlank(title), ArticleDO::getTitle, title.trim())
                // 用户ID过滤
                .eq(Objects.nonNull(userId), ArticleDO::getUserId, userId)
                //文章状态过滤 不传用户id时，首页查询已发布文章
                .eq(Objects.isNull(userId),ArticleDO::getStatus, ArticleStatusEnum.PUBLISH.getCode())
                // 大于等于 startDate
                .ge(Objects.nonNull(startDate), ArticleDO::getCreateTime, startDate)
                // 小于等于 endDate
                .le(Objects.nonNull(endDate), ArticleDO::getCreateTime, endDate)
                // 按创建时间倒叙
                .orderByDesc(ArticleDO::getCreateTime);

        applyVisibilityFilter(wrapper, viewerUserId, privilegedViewer);

        return selectPage(page, wrapper);
    }

    /**
     * 查询上一篇文章
     *
     * @param articleId
     * @return
     */
    default ArticleDO selectPreArticle(Long articleId) {
        return selectOne(Wrappers.<ArticleDO>lambdaQuery()
                .eq(ArticleDO::getStatus, ArticleStatusEnum.PUBLISH.getCode())
                .orderByAsc(ArticleDO::getId) // 按文章 ID 升序排列
                .gt(ArticleDO::getId, articleId) // 查询比当前文章 ID 大的
                .last("limit 1")); // 第一条记录即为上一篇文章
    }


    /**
     * 查询下一篇文章
     *
     * @param articleId
     * @return
     */
    default ArticleDO selectNextArticle(Long articleId) {
        return selectOne(Wrappers.<ArticleDO>lambdaQuery()
                .eq(ArticleDO::getStatus, ArticleStatusEnum.PUBLISH.getCode())
                .orderByDesc(ArticleDO::getId) // 按文章 ID 倒序排列
                .lt(ArticleDO::getId, articleId) // 查询比当前文章 ID 小的
                .last("limit 1")); // 第一条记录即为下一篇文章
    }

    /**
     * 查询用户文章
     * @param articleId
     * @param userId
     * @return
     */
   default ArticleDO selectByUserId(Long articleId,Long userId){
        return selectOne(Wrappers.<ArticleDO>lambdaQuery()
                .eq(ArticleDO::getId,articleId)
                .eq(ArticleDO::getUserId,userId));
   }

    default void applyVisibilityFilter(LambdaQueryWrapper<ArticleDO> wrapper, Long viewerUserId, boolean privilegedViewer) {
        if (privilegedViewer) {
            return;
        }

        String categoryAccessibleSubquery;
        if (viewerUserId == null) {
            categoryAccessibleSubquery = "SELECT acr.article_id FROM t_article_category_rel acr " +
                    "JOIN t_category c ON c.id = acr.category_id " +
                    "WHERE c.visibility_scope = 1";
        } else {
            categoryAccessibleSubquery = "SELECT acr.article_id FROM t_article_category_rel acr " +
                    "JOIN t_category c ON c.id = acr.category_id " +
                    "WHERE c.visibility_scope = 1 " +
                    "OR c.id IN (SELECT category_id FROM t_category_access_user WHERE user_id = " + viewerUserId + ")";
        }

        if (viewerUserId == null) {
            wrapper.eq(ArticleDO::getVisibilityScope, 1)
                    .inSql(ArticleDO::getId, categoryAccessibleSubquery);
            return;
        }

        wrapper.and(w -> w.eq(ArticleDO::getUserId, viewerUserId)
                        .or()
                        .eq(ArticleDO::getVisibilityScope, 1)
                        .or()
                        .inSql(ArticleDO::getId, "SELECT article_id FROM t_article_access_user WHERE user_id = " + viewerUserId))
                .and(w -> w.eq(ArticleDO::getUserId, viewerUserId)
                        .or()
                        .inSql(ArticleDO::getId, categoryAccessibleSubquery));
    }

    /**
     * 增加文章阅读数
     * @param articleId
     * @return
     */
    default int increaseReadNum(Long articleId) {
        // 执行 SQL : UPDATE t_article SET read_num = read_num + 1 WHERE id = #{articleId}
        ArticleDO articleDO = selectById(articleId);
        if (articleDO != null) {
            Long currentReadNum = articleDO.getReadNum();
            // 处理readNum为null的情况
            articleDO.setReadNum(currentReadNum == null ? 1L : currentReadNum + 1);
            return updateById(articleDO);
        }
        return 0;
    }

    default int updateAuthorByUserId(Long userId, String author) {
        LambdaUpdateWrapper<ArticleDO> wrapper = Wrappers.<ArticleDO>lambdaUpdate()
                .eq(ArticleDO::getUserId, userId)
                .set(ArticleDO::getAuthor, author);
        return update(null, wrapper);
    }
}
