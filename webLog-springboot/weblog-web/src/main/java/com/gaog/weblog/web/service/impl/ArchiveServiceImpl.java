package com.gaog.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.gaog.weblog.common.domain.dos.ArticleCategoryRelDO;
import com.gaog.weblog.common.domain.dos.ArticleDO;
import com.gaog.weblog.common.domain.dos.CategoryDO;
import com.gaog.weblog.common.domain.mapper.ArticleCategoryRelMapper;
import com.gaog.weblog.common.domain.mapper.ArticleMapper;
import com.gaog.weblog.common.domain.mapper.CategoryMapper;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.jwt.utils.SecurityContextUtil;
import com.gaog.weblog.web.convert.ArticleConvert;
import com.gaog.weblog.web.model.vo.archive.FindArchiveArticlePageListReqVO;
import com.gaog.weblog.web.model.vo.archive.FindArchiveArticlePageListRspVO;
import com.gaog.weblog.web.model.vo.archive.FindArchiveArticleRspVO;
import com.gaog.weblog.web.model.vo.article.FindCategoryListRspVO;
import com.gaog.weblog.web.service.ArchiveService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 *
 * @Author: gaoge
 * @Date: 2025/12/9 23:42
 * @Version: 1.0
 * @Description:
 */
@Service
public class ArchiveServiceImpl implements ArchiveService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 获取文章归档分页数据
     *
     * @param findArchiveArticlePageListReqVO
     * @return
     */
    @Override
    public Response findArchivePageList(FindArchiveArticlePageListReqVO findArchiveArticlePageListReqVO) {
        Long current = findArchiveArticlePageListReqVO.getCurrent();
        Long size = findArchiveArticlePageListReqVO.getSize();
        String title = findArchiveArticlePageListReqVO.getKeyword();
        Long userId = SecurityContextUtil.getCurrentUserId();

        // 分页查询
        IPage<ArticleDO> page = articleMapper.selectPageList(current, size, title, userId, null, null);
        List<ArticleDO> articleDOS = page.getRecords();

        List<FindArchiveArticlePageListRspVO> vos = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(articleDOS)) {
            // DO 转 VO
            List<FindArchiveArticleRspVO> archiveArticleRspVOS = articleDOS.stream()
                    .map(ArticleConvert.INSTANCE::convertDO2ArchiveArticleVO)
                    .collect(Collectors.toList());
            //插入分类
            archiveArticleRspVOS.stream().forEach(archiveArticleRspVO -> {
                // 查询所属分类
                ArticleCategoryRelDO articleCategoryRelDO = articleCategoryRelMapper.selectByArticleId(archiveArticleRspVO.getId());
                CategoryDO categoryDO = categoryMapper.selectById(articleCategoryRelDO.getCategoryId());
                // DO 转 VO
                FindCategoryListRspVO findCategoryListRspVO = FindCategoryListRspVO.builder()
                        .id(categoryDO.getId())
                        .name(categoryDO.getName())
                        .build();
                archiveArticleRspVO.setCategory(findCategoryListRspVO);
            });

            // 按创建的月份进行分组
            Map<YearMonth, List<FindArchiveArticleRspVO>> map = archiveArticleRspVOS.stream().collect(Collectors.groupingBy(FindArchiveArticleRspVO::getCreateMonth));
            // 使用 TreeMap 按月份倒序排列
            Map<YearMonth, List<FindArchiveArticleRspVO>> sortedMap = new TreeMap<>(Collections.reverseOrder());
            sortedMap.putAll(map);

            // 遍历排序后的 Map，将其转换为归档 VO
            sortedMap.forEach((k, v) -> vos.add(FindArchiveArticlePageListRspVO.builder().month(k).articles(v).build()));
        }

        return PageResponse.success(page, vos);
    }
}
