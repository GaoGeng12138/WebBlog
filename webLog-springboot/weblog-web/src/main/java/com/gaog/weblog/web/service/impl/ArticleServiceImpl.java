package com.gaog.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gaog.weblog.admin.model.vo.article.PublishArticleReqVO;
import com.gaog.weblog.admin.model.vo.article.UpdateArticleReqVO;
import com.gaog.weblog.common.domain.dos.ArticleCategoryRelDO;
import com.gaog.weblog.common.domain.dos.ArticleContentDO;
import com.gaog.weblog.common.domain.dos.ArticleDO;
import com.gaog.weblog.common.domain.dos.ArticleTagRelDO;
import com.gaog.weblog.common.domain.dos.CategoryDO;
import com.gaog.weblog.common.domain.dos.TagDO;
import com.gaog.weblog.common.domain.dos.VisitorLogDO;
import com.gaog.weblog.common.domain.mapper.ArticleCategoryRelMapper;
import com.gaog.weblog.common.domain.mapper.ArticleContentMapper;
import com.gaog.weblog.common.domain.mapper.ArticleMapper;
import com.gaog.weblog.common.domain.mapper.ArticleTagRelMapper;
import com.gaog.weblog.common.domain.mapper.CategoryMapper;
import com.gaog.weblog.common.domain.mapper.StatisticsArticlePvMapper;
import com.gaog.weblog.common.domain.mapper.TagMapper;
import com.gaog.weblog.common.domain.mapper.VisitorLogMapper;
import com.gaog.weblog.common.enums.ArticleStatusEnum;
import com.gaog.weblog.common.enums.ResponseCodeEnum;
import com.gaog.weblog.common.exception.BizException;
import com.gaog.weblog.common.utils.IpUtil;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.jwt.utils.SecurityContextUtil;
import com.gaog.weblog.web.convert.ArticleConvert;
import com.gaog.weblog.web.markdown.MarkdownHelper;
import com.gaog.weblog.web.model.vo.article.FindArticleByCategoryReqVO;
import com.gaog.weblog.web.model.vo.article.FindArticleByTagReqVO;
import com.gaog.weblog.web.model.vo.article.FindArticleDetailReqVO;
import com.gaog.weblog.web.model.vo.article.FindArticleDetailRspVO;
import com.gaog.weblog.web.model.vo.article.FindCategoryListRspVO;
import com.gaog.weblog.web.model.vo.article.FindIndexArticlePageListReqVO;
import com.gaog.weblog.web.model.vo.article.FindIndexArticlePageListRspVO;
import com.gaog.weblog.web.model.vo.article.FindPreNextArticleRspVO;
import com.gaog.weblog.web.model.vo.article.FindTagListRspVO;
import com.gaog.weblog.web.model.vo.article.FrontendUpdateArticleReqVO;
import com.gaog.weblog.web.service.ArticleService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @Author: gaoge
 * @Date: 2025/12/7 23:17
 * @Version: 1.0
 * @Description:
 */
@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleContentMapper articleContentMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;
    @Autowired
    private StatisticsArticlePvMapper statisticsArticlePvMapper;
    @Autowired
    private VisitorLogMapper visitorLogMapper;

    /**
     * 获取首页文章分页数据  支持个人中心查询自己发布的文章
     *
     * @param findIndexArticlePageListReqVO
     * @param request
     * @return
     */
    @Override
    public Response findArticlePageList(FindIndexArticlePageListReqVO findIndexArticlePageListReqVO, HttpServletRequest request) {
        Long current = findIndexArticlePageListReqVO.getCurrent();
        Long size = findIndexArticlePageListReqVO.getSize();
        String keyword = findIndexArticlePageListReqVO.getName();
        // 从 SecurityContext 获取用户ID
        Long userId = findIndexArticlePageListReqVO.getUserId();

        // 记录网站访问量（只有当userId为null时才统计，即非个人中心查询）
        if (Objects.isNull(userId)) {
            try {
                trackUniqueVisitor(request);
            } catch (Exception e) {
                log.error("==> 统计PV失败", e);
            }
        }

        Page<ArticleDO> articleDOPage = null;
        if (Objects.nonNull(userId)) {
            articleDOPage = articleMapper.selectPageList(current, size, keyword, userId, null, null);
        } else {
            articleDOPage = articleMapper.selectPageList(current, size, keyword, null, null, null);
        }

        // 返回的分页数据
        List<ArticleDO> articleDOS = articleDOPage.getRecords();

        List<FindIndexArticlePageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(articleDOS)) {
            // 文章 DO 转 VO
            vos = articleDOS.stream()
                    .map(articleDO -> ArticleConvert.INSTANCE.convertDO2VO(articleDO))
                    .collect(Collectors.toList());


            // 拿到所有文章的 ID 集合
            List<Long> articleIds = articleDOS.stream().map(ArticleDO::getId).collect(Collectors.toList());

            // 第二步：设置文章所属分类
            // 查询所有分类
            List<CategoryDO> categoryDOS = categoryMapper.selectList(Wrappers.emptyWrapper());
            // 转 Map, 方便后续根据分类 ID 拿到对应的分类名称
            Map<Long, String> categoryIdNameMap = categoryDOS.stream().collect(Collectors.toMap(CategoryDO::getId, CategoryDO::getName));

            // 根据文章 ID 批量查询所有关联记录
            List<ArticleCategoryRelDO> articleCategoryRelDOS = articleCategoryRelMapper.selectByArticleIds(articleIds);

            vos.forEach(vo -> {
                Long currArticleId = vo.getId();
                // 过滤出当前文章对应的关联数据
                Optional<ArticleCategoryRelDO> optional = articleCategoryRelDOS.stream().filter(rel -> Objects.equals(rel.getArticleId(), currArticleId)).findAny();

                // 获取文章阅读数
                ArticleDO articleDO = articleMapper.selectById(currArticleId);
                vo.setReadNum(articleDO.getReadNum());

                // 若不为空
                if (optional.isPresent()) {
                    ArticleCategoryRelDO articleCategoryRelDO = optional.get();
                    Long categoryId = articleCategoryRelDO.getCategoryId();
                    // 通过分类 ID 从 map 中拿到对应的分类名称
                    String categoryName = categoryIdNameMap.get(categoryId);

                    FindCategoryListRspVO findCategoryListRspVO = FindCategoryListRspVO.builder()
                            .id(categoryId)
                            .name(categoryName)
                            .build();
                    // 设置到当前 vo 类中
                    vo.setCategory(findCategoryListRspVO);
                }
            });

            // 第三步：设置文章标签
            // 查询所有标签
            List<TagDO> tagDOS = tagMapper.selectList(Wrappers.emptyWrapper());
            // 转 Map, 方便后续根据标签 ID 拿到对应的标签名称
            Map<Long, String> mapIdNameMap = tagDOS.stream().collect(Collectors.toMap(TagDO::getId, TagDO::getName));

            // 拿到所有文章的标签关联记录
            List<ArticleTagRelDO> articleTagRelDOS = articleTagRelMapper.selectByArticleIds(articleIds);
            vos.forEach(vo -> {
                Long currArticleId = vo.getId();
                // 过滤出当前文章的标签关联记录
                List<ArticleTagRelDO> articleTagRelDOList = articleTagRelDOS.stream().filter(rel -> Objects.equals(rel.getArticleId(), currArticleId)).collect(Collectors.toList());

                List<FindTagListRspVO> findTagListRspVOS = Lists.newArrayList();
                // 将关联记录 DO 转 VO, 并设置对应的标签名称
                articleTagRelDOList.forEach(articleTagRelDO -> {
                    Long tagId = articleTagRelDO.getTagId();
                    String tagName = mapIdNameMap.get(tagId);

                    FindTagListRspVO findTagListRspVO = FindTagListRspVO.builder()
                            .id(tagId)
                            .name(tagName)
                            .build();
                    findTagListRspVOS.add(findTagListRspVO);
                });
                // 设置转换后的标签数据
                vo.setTags(findTagListRspVOS);
            });
        }

        return PageResponse.success(articleDOPage, vos);
    }

    /**
     * 根据分类ID获取文章分页数据
     *
     * @param findArticleByCategoryReqVO
     * @return
     */
    @Override
    public PageResponse findArticlePageListByCategoryId(FindArticleByCategoryReqVO findArticleByCategoryReqVO) {
        Long current = findArticleByCategoryReqVO.getCurrent();
        Long size = findArticleByCategoryReqVO.getSize();
        String keyword = findArticleByCategoryReqVO.getName();
        Long categoryId = findArticleByCategoryReqVO.getCategoryId();

        // 先查询该分类下所有的文章ID
        List<ArticleCategoryRelDO> articleCategoryRelDOS = articleCategoryRelMapper.selectList(
                Wrappers.<ArticleCategoryRelDO>lambdaQuery()
                        .eq(ArticleCategoryRelDO::getCategoryId, categoryId)
        );

        if (CollectionUtils.isEmpty(articleCategoryRelDOS)) {
            // 如果该分类下没有文章，返回空数据
            Page<ArticleDO> emptyPage = new Page<>(current, size);
            emptyPage.setTotal(0);
            return PageResponse.success(emptyPage, Lists.newArrayList());
        }

        // 提取文章ID列表
        List<Long> articleIds = articleCategoryRelDOS.stream()
                .map(ArticleCategoryRelDO::getArticleId)
                .collect(Collectors.toList());

        // 根据文章ID列表分页查询文章
        Page<ArticleDO> articleDOPage = articleMapper.selectPage(
                new Page<>(current, size),
                Wrappers.<ArticleDO>lambdaQuery()
                        .in(ArticleDO::getId, articleIds)
                        .like(org.apache.commons.lang3.StringUtils.isNotBlank(keyword), ArticleDO::getTitle, keyword)
                        .orderByDesc(ArticleDO::getCreateTime)
        );

        // 返回的分页数据
        List<ArticleDO> articleDOS = articleDOPage.getRecords();

        List<FindIndexArticlePageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(articleDOS)) {
            // 文章 DO 转 VO
            vos = articleDOS.stream()
                    .map(articleDO -> ArticleConvert.INSTANCE.convertDO2VO(articleDO))
                    .collect(Collectors.toList());

            // 拿到所有文章的 ID 集合
            List<Long> currentPageArticleIds = articleDOS.stream().map(ArticleDO::getId).collect(Collectors.toList());

            // 第二步：设置文章所属分类
            // 查询所有分类
            List<CategoryDO> categoryDOS = categoryMapper.selectList(Wrappers.emptyWrapper());
            // 转 Map, 方便后续根据分类 ID 拿到对应的分类名称
            Map<Long, String> categoryIdNameMap = categoryDOS.stream().collect(Collectors.toMap(CategoryDO::getId, CategoryDO::getName));

            // 根据文章 ID 批量查询所有关联记录
            List<ArticleCategoryRelDO> currentPageArticleCategoryRelDOS = articleCategoryRelMapper.selectByArticleIds(currentPageArticleIds);

            vos.forEach(vo -> {
                Long currArticleId = vo.getId();
                // 过滤出当前文章对应的关联数据
                Optional<ArticleCategoryRelDO> optional = currentPageArticleCategoryRelDOS.stream().filter(rel -> Objects.equals(rel.getArticleId(), currArticleId)).findAny();

                // 若不为空
                if (optional.isPresent()) {
                    ArticleCategoryRelDO articleCategoryRelDO = optional.get();
                    Long currCategoryId = articleCategoryRelDO.getCategoryId();
                    // 通过分类 ID 从 map 中拿到对应的分类名称
                    String categoryName = categoryIdNameMap.get(currCategoryId);

                    FindCategoryListRspVO findCategoryListRspVO = FindCategoryListRspVO.builder()
                            .id(currCategoryId)
                            .name(categoryName)
                            .build();
                    // 设置到当前 vo 类中
                    vo.setCategory(findCategoryListRspVO);
                }
            });

            // 第三步：设置文章标签
            // 查询所有标签
            List<TagDO> tagDOS = tagMapper.selectList(Wrappers.emptyWrapper());
            // 转 Map, 方便后续根据标签 ID 拿到对应的标签名称
            Map<Long, String> mapIdNameMap = tagDOS.stream().collect(Collectors.toMap(TagDO::getId, TagDO::getName));

            // 拿到所有文章的标签关联记录
            List<ArticleTagRelDO> currentPageArticleTagRelDOS = articleTagRelMapper.selectByArticleIds(currentPageArticleIds);
            vos.forEach(vo -> {
                Long currArticleId = vo.getId();
                // 过滤出当前文章的标签关联记录
                List<ArticleTagRelDO> articleTagRelDOList = currentPageArticleTagRelDOS.stream().filter(rel -> Objects.equals(rel.getArticleId(), currArticleId)).collect(Collectors.toList());

                List<FindTagListRspVO> findTagListRspVOS = Lists.newArrayList();
                // 将关联记录 DO 转 VO, 并设置对应的标签名称
                articleTagRelDOList.forEach(articleTagRelDO -> {
                    Long tagId = articleTagRelDO.getTagId();
                    String tagName = mapIdNameMap.get(tagId);

                    FindTagListRspVO findTagListRspVO = FindTagListRspVO.builder()
                            .id(tagId)
                            .name(tagName)
                            .build();
                    findTagListRspVOS.add(findTagListRspVO);
                });
                // 设置转换后的标签数据
                vo.setTags(findTagListRspVOS);
            });
        }

        return PageResponse.success(articleDOPage, vos);
    }

    /**
     * 获取文章详情
     *
     * @param findArticleDetailReqVO
     * @return
     */
    @Override
    public Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO) {
        Long articleId = findArticleDetailReqVO.getArticleId();

        ArticleDO articleDO = articleMapper.selectById(articleId);

        // 判断文章是否存在
        if (Objects.isNull(articleDO)) {
            log.warn("==> 该文章不存在, articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 增加文章阅读数
        articleMapper.increaseReadNum(articleId);
        // 重新查询文章以获取更新后的阅读数
        articleDO = articleMapper.selectById(articleId);

        // 查询正文
        ArticleContentDO articleContentDO = articleContentMapper.selectByArticleId(articleId);

        // DO 转 VO
        FindArticleDetailRspVO vo = FindArticleDetailRspVO.builder()
                .id(articleId)
                .title(articleDO.getTitle())
                .createTime(articleDO.getCreateTime())
                .content(MarkdownHelper.convertMarkdown2Html(articleContentDO.getContent()))
                .readNum(articleDO.getReadNum())
                .build();

        // 查询所属分类
        ArticleCategoryRelDO articleCategoryRelDO = articleCategoryRelMapper.selectByArticleId(articleId);
        CategoryDO categoryDO = categoryMapper.selectById(articleCategoryRelDO.getCategoryId());
        // DO 转 VO
        FindCategoryListRspVO findCategoryListRspVO = FindCategoryListRspVO.builder()
                .id(categoryDO.getId())
                .name(categoryDO.getName())
                .build();
        vo.setCategory(findCategoryListRspVO);

        // 查询标签
        List<ArticleTagRelDO> articleTagRelDOS = articleTagRelMapper.selectByArticleId(articleId);
        List<Long> tagIds = articleTagRelDOS.stream().map(ArticleTagRelDO::getTagId).collect(Collectors.toList());
        List<TagDO> tagDOS = tagMapper.selectByIds(tagIds);

        // 标签 DO 转 VO
        List<FindTagListRspVO> tagVOS = tagDOS.stream()
                .map(tagDO -> FindTagListRspVO.builder().id(tagDO.getId()).name(tagDO.getName()).build())
                .collect(Collectors.toList());
        vo.setTags(tagVOS);

        // 上一篇
        ArticleDO preArticleDO = articleMapper.selectPreArticle(articleId);
        if (Objects.nonNull(preArticleDO)) {
            FindPreNextArticleRspVO preArticleVO = FindPreNextArticleRspVO.builder()
                    .articleId(preArticleDO.getId())
                    .articleTitle(preArticleDO.getTitle())
                    .build();
            vo.setPreArticle(preArticleVO);
        }

        // 下一篇
        ArticleDO nextArticleDO = articleMapper.selectNextArticle(articleId);
        if (Objects.nonNull(nextArticleDO)) {
            FindPreNextArticleRspVO nextArticleVO = FindPreNextArticleRspVO.builder()
                    .articleId(nextArticleDO.getId())
                    .articleTitle(nextArticleDO.getTitle())
                    .build();
            vo.setNextArticle(nextArticleVO);
        }

        return Response.success(vo);
    }

    /**
     * 根据标签ID获取文章分页数据
     *
     * @param findArticleByTagReqVO
     * @return
     */
    @Override
    public PageResponse findArticlePageListByTagId(FindArticleByTagReqVO findArticleByTagReqVO) {
        Long current = findArticleByTagReqVO.getCurrent();
        Long size = findArticleByTagReqVO.getSize();
        String keyword = findArticleByTagReqVO.getName();
        Long tagId = findArticleByTagReqVO.getTagId();

        // 先查询该标签下所有的文章ID
        List<ArticleTagRelDO> tagArticleRelDOS = articleTagRelMapper.selectList(
                Wrappers.<ArticleTagRelDO>lambdaQuery()
                        .eq(ArticleTagRelDO::getTagId, tagId)
        );

        if (CollectionUtils.isEmpty(tagArticleRelDOS)) {
            // 如果该标签下没有文章，返回空数据
            Page<ArticleDO> emptyPage = new Page<>(current, size);
            emptyPage.setTotal(0);
            return PageResponse.success(emptyPage, Lists.newArrayList());
        }

        // 提取文章ID列表
        List<Long> articleIds = tagArticleRelDOS.stream()
                .map(ArticleTagRelDO::getArticleId)
                .collect(Collectors.toList());

        // 根据文章ID列表分页查询文章，并增加userId过滤条件
        Page<ArticleDO> articleDOPage = articleMapper.selectPage(
                new Page<>(current, size),
                Wrappers.<ArticleDO>lambdaQuery()
                        .in(ArticleDO::getId, articleIds)
                        .like(org.apache.commons.lang3.StringUtils.isNotBlank(keyword), ArticleDO::getTitle, keyword)
                        .orderByDesc(ArticleDO::getCreateTime)
        );

        // 返回的分页数据
        List<ArticleDO> articleDOS = articleDOPage.getRecords();

        List<FindIndexArticlePageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(articleDOS)) {
            // 文章 DO 转 VO
            vos = articleDOS.stream()
                    .map(articleDO -> ArticleConvert.INSTANCE.convertDO2VO(articleDO))
                    .collect(Collectors.toList());

            // 拿到所有文章的 ID 集合
            List<Long> currentPageArticleIds = articleDOS.stream().map(ArticleDO::getId).collect(Collectors.toList());

            // 第二步：设置文章所属分类
            // 查询所有分类
            List<CategoryDO> categoryDOS = categoryMapper.selectList(Wrappers.emptyWrapper());
            // 转 Map, 方便后续根据分类 ID 拿到对应的分类名称
            Map<Long, String> categoryIdNameMap = categoryDOS.stream().collect(Collectors.toMap(CategoryDO::getId, CategoryDO::getName));

            // 根据文章 ID 批量查询所有关联记录
            List<ArticleCategoryRelDO> currentPageArticleCategoryRelDOS = articleCategoryRelMapper.selectByArticleIds(currentPageArticleIds);

            vos.forEach(vo -> {
                Long currArticleId = vo.getId();
                // 过滤出当前文章对应的关联数据
                Optional<ArticleCategoryRelDO> optional = currentPageArticleCategoryRelDOS.stream().filter(rel -> Objects.equals(rel.getArticleId(), currArticleId)).findAny();

                // 若不为空
                if (optional.isPresent()) {
                    ArticleCategoryRelDO articleCategoryRelDO = optional.get();
                    Long currCategoryId = articleCategoryRelDO.getCategoryId();
                    // 通过分类 ID 从 map 中拿到对应的分类名称
                    String categoryName = categoryIdNameMap.get(currCategoryId);

                    FindCategoryListRspVO findCategoryListRspVO = FindCategoryListRspVO.builder()
                            .id(currCategoryId)
                            .name(categoryName)
                            .build();
                    // 设置到当前 vo 类中
                    vo.setCategory(findCategoryListRspVO);
                }
            });

            // 第三步：设置文章标签
            // 查询所有标签
            List<TagDO> tagDOS = tagMapper.selectList(Wrappers.emptyWrapper());
            // 转 Map, 方便后续根据标签 ID 拿到对应的标签名称
            Map<Long, String> mapIdNameMap = tagDOS.stream().collect(Collectors.toMap(TagDO::getId, TagDO::getName));

            // 拿到所有文章的标签关联记录
            List<ArticleTagRelDO> currentPageArticleTagRelDOS = articleTagRelMapper.selectByArticleIds(currentPageArticleIds);
            vos.forEach(vo -> {
                Long currArticleId = vo.getId();
                // 过滤出当前文章的标签关联记录
                List<ArticleTagRelDO> articleTagRelDOList = currentPageArticleTagRelDOS.stream().filter(rel -> Objects.equals(rel.getArticleId(), currArticleId)).collect(Collectors.toList());

                List<FindTagListRspVO> findTagListRspVOS = Lists.newArrayList();
                // 将关联记录 DO 转 VO, 并设置对应的标签名称
                articleTagRelDOList.forEach(articleTagRelDO -> {
                    Long tagIdValue = articleTagRelDO.getTagId();
                    String tagName = mapIdNameMap.get(tagIdValue);

                    FindTagListRspVO findTagListRspVO = FindTagListRspVO.builder()
                            .id(tagIdValue)
                            .name(tagName)
                            .build();
                    findTagListRspVOS.add(findTagListRspVO);
                });
                // 设置转换后的标签数据
                vo.setTags(findTagListRspVOS);
            });
        }

        return PageResponse.success(articleDOPage, vos);
    }

    /**
     * 发布文章
     *
     * @param publishArticleReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response publishArticle(PublishArticleReqVO publishArticleReqVO) {

        Long userId = SecurityContextUtil.getCurrentUserId();
        String nickName = SecurityContextUtil.getCurrentUserNickname();

        ArticleDO articleDO = ArticleDO.builder()
                .title(publishArticleReqVO.getTitle())
                .cover(publishArticleReqVO.getCover())
                .summary(publishArticleReqVO.getSummary())
                .author(nickName)
                .userId(userId)
                .readNum(0L)
                .status(ArticleStatusEnum.PUBLISH.getCode())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        articleMapper.insert(articleDO);


        //获取插入记录的主键Id
        Long articleId = articleDO.getId();

        //保存文章内容
        ArticleContentDO articleContentDO = ArticleContentDO.builder()
                .articleId(articleId)
                .content(publishArticleReqVO.getContent())
                .build();

        //获取分类id
        Long categoryId = publishArticleReqVO.getCategoryId();

        //保存文章分类关联
        articleContentMapper.insert(articleContentDO);

        CategoryDO categoryDO = categoryMapper.selectById(categoryId);
        if (Objects.isNull(categoryDO)) {
            log.warn("==> 分类不存在, categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }

        ArticleCategoryRelDO articleCategoryRelDO = ArticleCategoryRelDO.builder()
                .articleId(articleId)
                .categoryId(categoryId)
                .build();
        articleCategoryRelMapper.insert(articleCategoryRelDO);

        // 4. 保存文章关联的标签集合
        List<String> publishTags = publishArticleReqVO.getTags();
        insertTags(articleId, publishTags);

        return Response.success();

    }

    /**
     * 保存标签
     *
     * @param articleId
     * @param publishTags
     */
    private void insertTags(Long articleId, List<String> publishTags) {
        // 筛选提交的标签（表中不存在的标签）
        List<String> notExistTags = null;
        // 筛选提交的标签（表中已存在的标签）
        List<String> existedTags = null;

        // 查询出所有标签
        List<TagDO> tagDOS = tagMapper.selectList(null);

        //如果表中还没有添加过标签
        if (CollectionUtils.isEmpty(tagDOS)) {
            notExistTags = publishTags;
        } else {
            List<String> tagIds = tagDOS.stream().map(tagDO -> String.valueOf(tagDO.getId())).collect(Collectors.toList());
            // 表中已添加相关标签，则需要筛选
            // 通过标签 ID 来筛选，包含对应 ID 则表示提交的标签是表中存在的
            existedTags = publishTags.stream().filter(publishTag -> tagIds.contains(publishTag)).collect(Collectors.toList());
            // 否则则是不存在的
            notExistTags = publishTags.stream().filter(publishTag -> !tagIds.contains(publishTag)).collect(Collectors.toList());

            // 补充逻辑：
            // 还有一种可能：按字符串名称提交上来的标签，也有可能是表中已存在的，比如表中已经有了 Java 标签，用户提交了个 java 小写的标签，需要内部装换为 Java 标签
            Map<String, Long> tagNameIdMap = tagDOS.stream().collect(Collectors.toMap(tagDO -> tagDO.getName().toLowerCase(), TagDO::getId));

            // 使用迭代器进行安全的删除操作
            Iterator<String> iterator = notExistTags.iterator();
            while (iterator.hasNext()) {
                String notExistTag = iterator.next();
                // 转小写, 若 Map 中相同的 key，则表示该新标签是重复标签
                if (tagNameIdMap.containsKey(notExistTag.toLowerCase())) {
                    // 从不存在的标签集合中清除
                    iterator.remove();
                    // 并将对应的 ID 添加到已存在的标签集合
                    existedTags.add(String.valueOf(tagNameIdMap.get(notExistTag.toLowerCase())));
                }
            }
        }

        // 将提交的上来的，已存在于表中的标签，文章-标签关联关系入库
        if (!CollectionUtils.isEmpty(existedTags)) {
            List<ArticleTagRelDO> articleTagRelDOS = Lists.newArrayList();
            existedTags.forEach(tagId -> {
                ArticleTagRelDO articleTagRelDO = ArticleTagRelDO.builder()
                        .articleId(articleId)
                        .tagId(Long.valueOf(tagId))
                        .build();
                articleTagRelDOS.add(articleTagRelDO);
            });
            // 批量插入
            articleTagRelMapper.insertBatchSomeColumn(articleTagRelDOS);
        }

        // 将提交的上来的，不存在于表中的标签，入库保存
        if (!CollectionUtils.isEmpty(notExistTags)) {
            // 需要先将标签入库，拿到对应标签 ID 后，再把文章-标签关联关系入库
            List<ArticleTagRelDO> articleTagRelDOS = Lists.newArrayList();
            notExistTags.forEach(tagName -> {
                TagDO tagDO = TagDO.builder()
                        .name(tagName)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build();

                tagMapper.insert(tagDO);

                // 拿到保存的标签 ID
                Long tagId = tagDO.getId();

                // 文章-标签关联关系
                ArticleTagRelDO articleTagRelDO = ArticleTagRelDO.builder()
                        .articleId(articleId)
                        .tagId(tagId)
                        .build();
                articleTagRelDOS.add(articleTagRelDO);
            });
            // 批量插入
            articleTagRelMapper.insertBatchSomeColumn(articleTagRelDOS);
        }
    }

    /**
     * 更新文章
     *
     * @param updateArticleReqVO
     * @return
     */
    @Override
    public Response updateArticle(FrontendUpdateArticleReqVO updateArticleReqVO) {
        Long articleId = updateArticleReqVO.getId();
        // 从 SecurityContext 获取用户ID
        Long userId = SecurityContextUtil.getCurrentUserId();

        // 1. 检查文章是否存在
        ArticleDO articleDO = articleMapper.selectByUserId(articleId, userId);
        if (Objects.isNull(articleDO)) {
            log.warn("==> 文章不存在, articleId: {},userId:{}", articleId, userId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 2. 更新文章基本信息
        ArticleDO updateArticleDO = ArticleDO.builder()
                .id(articleId)
                .title(updateArticleReqVO.getTitle())
                .cover(updateArticleReqVO.getCover())
                .summary(updateArticleReqVO.getSummary())
                .updateTime(LocalDateTime.now())
                .build();
        articleMapper.updateById(updateArticleDO);

        // 3. 更新文章内容
        ArticleContentDO articleContentDO = ArticleContentDO.builder()
                .articleId(articleId)
                .content(updateArticleReqVO.getContent())
                .build();
        // 先删除旧的内容记录
        articleContentMapper.delete(new LambdaQueryWrapper<ArticleContentDO>()
                .eq(ArticleContentDO::getArticleId, articleId));
        // 插入新的内容记录
        articleContentMapper.insert(articleContentDO);

        // 4. 更新文章分类
        Long categoryId = updateArticleReqVO.getCategoryId();
        CategoryDO categoryDO = categoryMapper.selectById(categoryId);
        if (Objects.isNull(categoryDO)) {
            log.warn("==> 分类不存在, categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }
        // 先删除旧的分类关联
        articleCategoryRelMapper.delete(new LambdaQueryWrapper<ArticleCategoryRelDO>()
                .eq(ArticleCategoryRelDO::getArticleId, articleId));
        // 插入新的分类关联
        ArticleCategoryRelDO articleCategoryRelDO = ArticleCategoryRelDO.builder()
                .articleId(articleId)
                .categoryId(categoryId)
                .build();
        articleCategoryRelMapper.insert(articleCategoryRelDO);

        // 5. 更新文章标签
        // 先删除旧的标签关联
        articleTagRelMapper.delete(new LambdaQueryWrapper<ArticleTagRelDO>()
                .eq(ArticleTagRelDO::getArticleId, articleId));
        // 插入新的标签关联
        List<String> publishTags = updateArticleReqVO.getTags();
        insertTags(articleId, publishTags);

        return Response.success();
    }

    /**
     * 跟踪独立访客（同一用户/IP每天只计算一次）
     *
     * @param request HttpServletRequest
     */
    private void trackUniqueVisitor(HttpServletRequest request) {
        LocalDate today = LocalDate.now();
        String ipAddress = IpUtil.getClientIp(request);

        // 尝试获取当前登录用户ID（如果有）
        Long currentUserId = null;
        try {
            currentUserId = SecurityContextUtil.getCurrentUserId();
        } catch (Exception e) {
            // 未登录用户，忽略
        }

        // 检查今天是否已经记录过该访客
        LambdaQueryWrapper<VisitorLogDO> wrapper = Wrappers.<VisitorLogDO>lambdaQuery()
                .eq(VisitorLogDO::getVisitDate, today);

        if (currentUserId != null) {
            // 登录用户：按userId判断
            wrapper.eq(VisitorLogDO::getUserId, currentUserId);
        } else {
            // 匿名用户：按IP地址判断
            wrapper.eq(VisitorLogDO::getIpAddress, ipAddress);
        }

        Long count = visitorLogMapper.selectCount(wrapper);

        // 如果今天还没有记录，则新增记录并更新PV统计
        if (count == null || count == 0) {
            // 记录访客
            VisitorLogDO visitorLog = VisitorLogDO.builder()
                    .userId(currentUserId)
                    .ipAddress(ipAddress)
                    .visitDate(today)
                    .build();

            try {
                visitorLogMapper.insert(visitorLog);
                // 增加PV统计
                statisticsArticlePvMapper.increasePvCount(today);
            } catch (Exception e) {
                // 忽略重复插入错误（并发情况下可能出现）
                log.warn("==> 访客记录已存在，忽略: userId={}, ip={}, date={}", currentUserId, ipAddress, today);
            }
        }
    }
}
