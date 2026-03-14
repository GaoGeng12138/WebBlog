package com.gaog.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gaog.weblog.common.domain.dos.ArticleTagRelDO;
import com.gaog.weblog.common.domain.dos.TagDO;
import com.gaog.weblog.common.domain.mapper.ArticleTagRelMapper;
import com.gaog.weblog.common.domain.mapper.TagMapper;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.web.model.vo.tag.FindTagPageListReqVO;
import com.gaog.weblog.web.model.vo.tag.FindTagPageListRspVO;
import com.gaog.weblog.web.model.vo.tag.FindTagArticleReqVO;
import com.gaog.weblog.web.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gaog
 * @description 标签服务实现类
 */
@Service
@Slf4j
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;

    /**
     * 获取标签分页数据，包含每个标签的文章数量
     *
     * @param findTagPageListReqVO
     * @return
     */
    @Override
    public PageResponse findTagList(FindTagPageListReqVO findTagPageListReqVO) {
        // 获取当前页、以及每页需要展示的数据数量
        Long current = findTagPageListReqVO.getCurrent();
        Long size = findTagPageListReqVO.getSize();

        // 分页对象(查询第几页、每页多少数据)
        Page<TagDO> page = new Page<>(current, size);

        // 构建查询条件
        LambdaQueryWrapper<TagDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(TagDO::getCreateTime);

        // 执行分页查询
        Page<TagDO> tagDOPage = tagMapper.selectPage(page, wrapper);

        List<TagDO> tagDOS = tagDOPage.getRecords();

        // DO 转 VO
        List<FindTagPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(tagDOS)) {
            // 获取所有标签的ID
            List<Long> tagIds = tagDOS.stream()
                    .map(TagDO::getId)
                    .collect(Collectors.toList());

            // 查询每个标签下的文章数量
            Map<Long, Integer> tagArticleCountMap = articleTagRelMapper.selectList(
                            new LambdaQueryWrapper<ArticleTagRelDO>()
                                    .in(ArticleTagRelDO::getTagId, tagIds))
                    .stream()
                    .collect(Collectors.groupingBy(
                            ArticleTagRelDO::getTagId,
                            Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                    ));

            vos = tagDOS.stream()
                    .map(tagDO -> FindTagPageListRspVO.builder()
                            .id(tagDO.getId())
                            .name(tagDO.getName())
                            .articleCount(tagArticleCountMap.getOrDefault(tagDO.getId(), 0))
                            .createTime(tagDO.getCreateTime())
                            .build())
                    .collect(Collectors.toList());
        }

        return PageResponse.success(tagDOPage, vos);
    }
    
    /**
     * 获取所有标签数据，包含每个标签的文章数量
     *
     * @return
     */
    @Override
    public Response findAllTags() {
        // 查询所有标签
        List<TagDO> tagDOS = tagMapper.selectList(null);
        
        // DO 转 VO
        List<FindTagPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(tagDOS)) {
            // 获取所有标签的ID
            List<Long> tagIds = tagDOS.stream()
                    .map(TagDO::getId)
                    .collect(Collectors.toList());

            // 查询每个标签下的文章数量
            Map<Long, Integer> tagArticleCountMap = articleTagRelMapper.selectList(
                            new LambdaQueryWrapper<ArticleTagRelDO>()
                                    .in(ArticleTagRelDO::getTagId, tagIds))
                    .stream()
                    .collect(Collectors.groupingBy(
                            ArticleTagRelDO::getTagId,
                            Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                    ));

            vos = tagDOS.stream()
                    .map(tagDO -> FindTagPageListRspVO.builder()
                            .id(tagDO.getId())
                            .name(tagDO.getName())
                            .articleCount(tagArticleCountMap.getOrDefault(tagDO.getId(), 0))
                            .createTime(tagDO.getCreateTime())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(vos);
    }
    
    /**
     * 根据标签ID获取文章分页数据
     *
     * @param findTagArticleReqVO
     * @return
     */
    @Override
    public PageResponse findArticlePageListByTagId(FindTagArticleReqVO findTagArticleReqVO) {
        // Simply return an empty response as this should be handled by ArticleService
        // In a real implementation, this would delegate to ArticleService
        Page page = new Page(findTagArticleReqVO.getCurrent(), findTagArticleReqVO.getSize());
        page.setTotal(0);
        return PageResponse.success(page, null);
    }
}