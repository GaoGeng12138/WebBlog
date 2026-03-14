package com.gaog.weblog.web.convert;

import com.gaog.weblog.common.domain.dos.ArticleDO;
import com.gaog.weblog.web.model.vo.archive.FindArchiveArticleRspVO;
import com.gaog.weblog.web.model.vo.article.FindIndexArticlePageListRspVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 *
 * @Author: gaoge
 * @Date: 2025/12/7 23:19
 * @Version: 1.0
 * @Description: convert 装换接口
 */
@Mapper
public interface ArticleConvert {
    /**
     * 初始化 convert 实例
     */
    ArticleConvert INSTANCE = Mappers.getMapper(ArticleConvert.class);

    /**
     * 将 DO 转化为 VO
     * @param bean
     * @return
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "cover", target = "cover")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "author", target = "author")
    @Mapping(source = "createTime", target = "createTime")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "summary", target = "summary")
    FindIndexArticlePageListRspVO convertDO2VO(ArticleDO bean);


    /**
     * 将 DO 转化为归档文章 VO
     * @param bean
     * @return
     */
    @Mapping(target = "createDate", expression = "java(java.time.LocalDate.from(bean.getCreateTime()))")
    @Mapping(target = "createMonth", expression = "java(java.time.YearMonth.from(bean.getCreateTime()))")
    FindArchiveArticleRspVO convertDO2ArchiveArticleVO(ArticleDO bean);

}