package com.gaog.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gaog.weblog.common.domain.dos.CommentDO;

import java.time.LocalDate;
import java.util.List;

/**
 * Comment Mapper
 */
public interface CommentMapper extends BaseMapper<CommentDO> {

    /**
     * Paginated query for comments
     *
     * @param current   Current page
     * @param size      Page size
     * @param articleId Article ID
     * @param status    Comment status
     * @param startDate Start date
     * @param endDate   End date
     * @return Page result
     */
    default Page<CommentDO> selectPageList(Long current, Long size, Long articleId, 
                                           Integer status, LocalDate startDate, LocalDate endDate) {
        Page<CommentDO> page = new Page<>(current, size);
        
        LambdaQueryWrapper<CommentDO> wrapper = Wrappers.<CommentDO>lambdaQuery()
                .eq(articleId != null, CommentDO::getArticleId, articleId)
                .eq(status != null, CommentDO::getStatus, status)
                .ge(startDate != null, CommentDO::getCreateTime, startDate)
                .le(endDate != null, CommentDO::getCreateTime, endDate)
                .eq(CommentDO::getIsDeleted, false)
                .orderByDesc(CommentDO::getCreateTime);
        
        return selectPage(page, wrapper);
    }

    /**
     * Get approved comments by article ID
     *
     * @param articleId Article ID
     * @return List of comments
     */
    default List<CommentDO> selectByArticleId(Long articleId) {
        return selectList(Wrappers.<CommentDO>lambdaQuery()
                .eq(CommentDO::getArticleId, articleId)
                .eq(CommentDO::getStatus, 1) // 1 = approved
                .eq(CommentDO::getIsDeleted, false)
                .orderByDesc(CommentDO::getIsTop)
                .orderByDesc(CommentDO::getCreateTime));
    }

    /**
     * Count comments by article ID
     *
     * @param articleId Article ID
     * @return Comment count
     */
    default Long countByArticleId(Long articleId) {
        return selectCount(Wrappers.<CommentDO>lambdaQuery()
                .eq(CommentDO::getArticleId, articleId)
                .eq(CommentDO::getStatus, 1) // Only approved comments
                .eq(CommentDO::getIsDeleted, false));
    }

    /**
     * Get comments by parent ID
     *
     * @param parentId Parent comment ID
     * @return List of reply comments
     */
    default List<CommentDO> selectByParentId(Long parentId) {
        return selectList(Wrappers.<CommentDO>lambdaQuery()
                .eq(CommentDO::getParentId, parentId)
                .eq(CommentDO::getStatus, 1)
                .eq(CommentDO::getIsDeleted, false)
                .orderByAsc(CommentDO::getCreateTime));
    }

    /**
     * Count pending review comments
     *
     * @return Count
     */
    default Long countPendingReview() {
        return selectCount(Wrappers.<CommentDO>lambdaQuery()
                .eq(CommentDO::getStatus, 0) // 0 = pending
                .eq(CommentDO::getIsDeleted, false));
    }
}
