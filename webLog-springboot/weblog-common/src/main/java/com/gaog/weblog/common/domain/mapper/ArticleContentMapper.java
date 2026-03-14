package com.gaog.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gaog.weblog.common.domain.dos.ArticleContentDO;

/**
 * @author ZSJ
 * @date 2025/11/25 15:50
 * @description
 */
public interface ArticleContentMapper extends BaseMapper<ArticleContentDO> {

    default ArticleContentDO selectByArticleId(Long articleId) {
        LambdaQueryWrapper<ArticleContentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleContentDO::getArticleId, articleId);
        return selectOne(wrapper);
    }
}
