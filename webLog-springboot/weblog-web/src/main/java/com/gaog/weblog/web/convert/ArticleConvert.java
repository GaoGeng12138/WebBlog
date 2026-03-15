package com.gaog.weblog.web.convert;

import com.gaog.weblog.common.domain.dos.ArticleDO;
import com.gaog.weblog.web.model.vo.archive.FindArchiveArticleRspVO;
import com.gaog.weblog.web.model.vo.article.FindIndexArticlePageListRspVO;

/**
 *
 * @Author: gaoge
 * @Date: 2025/12/7 23:19
 * @Version: 1.0
 * @Description: convert 装换接口
 */
public class ArticleConvert {
    /**
     * 初始化 convert 实例
     */
    public static final ArticleConvert INSTANCE = new ArticleConvert();

    private ArticleConvert() {
    }

    /**
     * 将 DO 转化为 VO
     * @param bean
     * @return
     */
    public FindIndexArticlePageListRspVO convertDO2VO(ArticleDO bean) {
        if (bean == null) {
            return null;
        }

        return FindIndexArticlePageListRspVO.builder()
                .id(bean.getId())
                .cover(bean.getCover())
                .title(bean.getTitle())
                .author(bean.getAuthor())
                .createTime(bean.getCreateTime())
                .updateTime(bean.getUpdateTime())
                .status(bean.getStatus())
                .summary(bean.getSummary())
                .readNum(bean.getReadNum())
                .build();
    }


    /**
     * 将 DO 转化为归档文章 VO
     * @param bean
     * @return
     */
    public FindArchiveArticleRspVO convertDO2ArchiveArticleVO(ArticleDO bean) {
        if (bean == null) {
            return null;
        }

        return FindArchiveArticleRspVO.builder()
                .id(bean.getId())
                .cover(bean.getCover())
                .title(bean.getTitle())
                .createDate(java.time.LocalDate.from(bean.getCreateTime()))
                .createMonth(java.time.YearMonth.from(bean.getCreateTime()))
                .build();
    }

}
