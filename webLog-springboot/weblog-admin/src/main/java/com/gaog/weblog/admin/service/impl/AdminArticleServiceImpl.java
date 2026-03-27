package com.gaog.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gaog.weblog.admin.model.vo.article.AuditArticleReqVO;
import com.gaog.weblog.admin.model.vo.article.DeleteArticleReqVO;
import com.gaog.weblog.admin.model.vo.article.FindArticleDetailReqVO;
import com.gaog.weblog.admin.model.vo.article.FindArticleDetailRspVO;
import com.gaog.weblog.admin.model.vo.article.FindArticlePageListReqVO;
import com.gaog.weblog.admin.model.vo.article.FindArticlePageListRspVO;
import com.gaog.weblog.admin.model.vo.article.PublishArticleReqVO;
import com.gaog.weblog.admin.model.vo.article.UpdateArticleReqVO;
import com.gaog.weblog.admin.service.AdminArticleService;
import com.gaog.weblog.common.domain.dos.ArticleAccessUserDO;
import com.gaog.weblog.common.domain.dos.ArticleCategoryRelDO;
import com.gaog.weblog.common.domain.dos.ArticleContentDO;
import com.gaog.weblog.common.domain.dos.ArticleDO;
import com.gaog.weblog.common.domain.dos.ArticleTagRelDO;
import com.gaog.weblog.common.domain.dos.CategoryDO;
import com.gaog.weblog.common.domain.dos.TagDO;
import com.gaog.weblog.common.domain.mapper.ArticleAccessUserMapper;
import com.gaog.weblog.common.domain.mapper.ArticleCategoryRelMapper;
import com.gaog.weblog.common.domain.mapper.ArticleContentMapper;
import com.gaog.weblog.common.domain.mapper.ArticleMapper;
import com.gaog.weblog.common.domain.mapper.ArticleTagRelMapper;
import com.gaog.weblog.common.domain.mapper.CategoryMapper;
import com.gaog.weblog.common.domain.mapper.TagMapper;
import com.gaog.weblog.common.enums.ArticleSourceEnum;
import com.gaog.weblog.common.enums.ArticleStatusEnum;
import com.gaog.weblog.common.enums.ResponseCodeEnum;
import com.gaog.weblog.common.enums.VisibilityScopeEnum;
import com.gaog.weblog.common.exception.BizException;
import com.gaog.weblog.common.model.vo.SelectRspVO;
import com.gaog.weblog.common.service.ContentVisibilityService;
import com.gaog.weblog.common.utils.CategoryTreeUtil;
import com.gaog.weblog.common.utils.FileUtil;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.jwt.utils.SecurityContextUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ZSJ
 * @date 2025/11/25 16:10
 * @description
 */
@Service
@Slf4j
public class AdminArticleServiceImpl implements AdminArticleService {

    private static final String ACTION_DRAFT = "draft";
    private static final String ACTION_PUBLISH = "publish";

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleContentMapper articleContentMapper;
    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;
    @Autowired
    private ArticleAccessUserMapper articleAccessUserMapper;
    @Autowired
    private ContentVisibilityService contentVisibilityService;

    /**
     * 发布文章
     *
     * @param publishArticleReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response publishArticle(PublishArticleReqVO publishArticleReqVO) {
        boolean savingDraft = isDraftAction(publishArticleReqVO.getSubmitAction());
        String validateMessage = validateArticlePayloadForAction(publishArticleReqVO.getContent(), publishArticleReqVO.getCover(),
                publishArticleReqVO.getCategoryId(), savingDraft);
        if (StringUtils.isNotBlank(validateMessage)) {
            return Response.fail(validateMessage);
        }

        Long userId = SecurityContextUtil.getCurrentUserId();
        String nickname = SecurityContextUtil.getCurrentUserNickname();

        ArticleDO articleDO = ArticleDO.builder()
                .title(StringUtils.trimToEmpty(publishArticleReqVO.getTitle()))
                .cover(publishArticleReqVO.getCover())
                .summary(publishArticleReqVO.getSummary())
                .status(resolveAdminStatus(publishArticleReqVO.getSubmitAction()))
                .userId(userId)
                .author(nickname)
                .articleSource(ArticleSourceEnum.ADMIN.getCode())
                .visibilityScope(resolveVisibilityScope(publishArticleReqVO.getVisibilityScope()))
                .readNum(0L)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        articleMapper.insert(articleDO);


        //获取插入记录的主键Id
        Long articleId = articleDO.getId();

        //保存文章内容
        ArticleContentDO articleContentDO = ArticleContentDO.builder()
                .articleId(articleId)
                .content(StringUtils.defaultString(publishArticleReqVO.getContent()))
                .build();

        //获取分类id
        Long categoryId = publishArticleReqVO.getCategoryId();

        //保存文章分类关联
        articleContentMapper.insert(articleContentDO);

        if (Objects.nonNull(categoryId)) {
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
        }

        // 4. 保存文章关联的标签集合
        List<String> publishTags = publishArticleReqVO.getTags();
        insertTags(articleId, publishTags);
        saveVisibleUsers(articleId, articleDO.getVisibilityScope(), publishArticleReqVO.getVisibleUserIds());

        return Response.success();

    }

    /**
     * 保存标签
     *
     * @param articleId
     * @param publishTags
     */
    private void insertTags(Long articleId, List<String> publishTags) {
        if (CollectionUtils.isEmpty(publishTags)) {
            return;
        }

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
     * 删除文章
     *
     * @param deleteArticleReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response deleteArticle(DeleteArticleReqVO deleteArticleReqVO) {
        Long articleId = deleteArticleReqVO.getId();

        //查询文章是否存在
        ArticleDO articleDO = articleMapper.selectById(articleId);
        if (Objects.isNull(articleDO)) {
            log.warn("==> 文章不存在, articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_EXISTED);
        }
        String cover = articleDO.getCover();
        //获取文章图片地址
        boolean deleteFile = FileUtil.deleteFile(cover);
        if (!deleteFile) {
            log.warn("==> 删除文章图片失败, articleId: {}", articleId);
        } else {
            log.info("==> 删除文章图片成功, articleId: {}", articleId);
        }

        // 1. 删除文章
        articleMapper.deleteById(articleId);

        // 2. 删除文章内容
        articleContentMapper.delete(new LambdaQueryWrapper<ArticleContentDO>()
                .eq(ArticleContentDO::getArticleId, articleId));

        // 3. 删除文章-分类关联记录
        articleCategoryRelMapper.delete(new LambdaQueryWrapper<ArticleCategoryRelDO>()
                .eq(ArticleCategoryRelDO::getArticleId, articleId));

        // 4. 删除文章-标签关联记录
        articleTagRelMapper.delete(new LambdaQueryWrapper<ArticleTagRelDO>()
                .eq(ArticleTagRelDO::getArticleId, articleId));

        return Response.success();
    }

    /**
     * 查询文章分页数据
     *
     * @param findArticlePageListReqVO
     * @return
     */
    @Override
    public PageResponse findArticlePageList(FindArticlePageListReqVO findArticlePageListReqVO) {
        // 获取当前页、以及每页需要展示的数据数量
        Long current = findArticlePageListReqVO.getCurrent();
        Long size = findArticlePageListReqVO.getSize();

        // 分页对象(查询第几页、每页多少数据)
        Page<ArticleDO> page = new Page<>(current, size);

        // 构建查询条件
        LambdaQueryWrapper<ArticleDO> wrapper = new LambdaQueryWrapper<>();

        String title = findArticlePageListReqVO.getTitle();
        LocalDate startDate = findArticlePageListReqVO.getStartDate();
        LocalDate endDate = findArticlePageListReqVO.getEndDate();

        wrapper
                // like 模糊查询
                .like(StringUtils.isNotBlank(title), ArticleDO::getTitle, title.trim())
                // 大于等于 startDate
                .ge(Objects.nonNull(startDate), ArticleDO::getCreateTime, startDate)
                // 小于等于 endDate
                .le(Objects.nonNull(endDate), ArticleDO::getCreateTime, endDate)
                // 按创建时间倒序
                .orderByDesc(ArticleDO::getCreateTime);

        // 执行分页查询
        Page<ArticleDO> articleDOPage = articleMapper.selectPage(page, wrapper);

        List<ArticleDO> articleDOS = articleDOPage.getRecords();
        List<CategoryDO> categoryDOS = categoryMapper.selectList(null);
        Map<Long, CategoryDO> categoryMap = categoryDOS.stream()
                .collect(Collectors.toMap(CategoryDO::getId, item -> item, (left, right) -> left));

        // DO 转 VO
        List<FindArticlePageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(articleDOS)) {
            vos = articleDOS.stream()
                    .map(articleDO -> {
                        Long articleId = articleDO.getId();

                        // 查询文章分类
                        ArticleCategoryRelDO articleCategoryRelDO = articleCategoryRelMapper.selectOne(
                                new LambdaQueryWrapper<ArticleCategoryRelDO>()
                                        .eq(ArticleCategoryRelDO::getArticleId, articleId));

                        String categoryName = null;
                        if (Objects.nonNull(articleCategoryRelDO)) {
                            categoryName = CategoryTreeUtil.buildCategoryPathName(articleCategoryRelDO.getCategoryId(), categoryMap);
                        }

                        // 查询文章标签列表
                        List<ArticleTagRelDO> articleTagRelDOS = articleTagRelMapper.selectList(
                                new LambdaQueryWrapper<ArticleTagRelDO>()
                                        .eq(ArticleTagRelDO::getArticleId, articleId));

                        List<String> tagNames = null;
                        if (!CollectionUtils.isEmpty(articleTagRelDOS)) {
                            // 获取标签 ID 集合
                            List<Long> tagIds = articleTagRelDOS.stream()
                                    .map(ArticleTagRelDO::getTagId)
                                    .collect(Collectors.toList());

                            // 批量查询标签信息
                            if (!CollectionUtils.isEmpty(tagIds)) {
                                List<TagDO> tagDOS = tagMapper.selectBatchIds(tagIds);
                                if (!CollectionUtils.isEmpty(tagDOS)) {
                                    tagNames = tagDOS.stream()
                                            .map(TagDO::getName)
                                            .collect(Collectors.toList());
                                }
                            }
                        }

                        return FindArticlePageListRspVO.builder()
                                .id(articleId)
                                .title(articleDO.getTitle())
                                .cover(articleDO.getCover())
                                .summary(articleDO.getSummary())
                                .category(categoryName)
                                .tags(tagNames)
                                .articleSource(articleDO.getArticleSource())
                                .articleSourceLabel(ArticleSourceEnum.getDescByCode(articleDO.getArticleSource()))
                                .status(articleDO.getStatus())
                                .statusLabel(resolveStatusLabel(articleDO.getStatus()))
                                .createTime(articleDO.getCreateTime())
                                .build();
                    })
                    .collect(Collectors.toList());
        }

        return PageResponse.success(articleDOPage, vos);
    }

    /**
     * 查询文章详情
     *
     * @param findArticleDetailReqVO
     * @return
     */
    @Override
    public Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO) {
        Long articleId = findArticleDetailReqVO.getId();

        // 查询文章基本信息
        ArticleDO articleDO = articleMapper.selectById(articleId);
        if (Objects.isNull(articleDO)) {
            log.warn("==> 文章不存在, articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 查询文章内容
        ArticleContentDO articleContentDO = articleContentMapper.selectOne(
                new LambdaQueryWrapper<ArticleContentDO>()
                        .eq(ArticleContentDO::getArticleId, articleId));

        // 查询文章分类
        ArticleCategoryRelDO articleCategoryRelDO = articleCategoryRelMapper.selectOne(
                new LambdaQueryWrapper<ArticleCategoryRelDO>()
                        .eq(ArticleCategoryRelDO::getArticleId, articleId));

        // 查询文章标签列表
        List<ArticleTagRelDO> articleTagRelDOS = articleTagRelMapper.selectList(
                new LambdaQueryWrapper<ArticleTagRelDO>()
                        .eq(ArticleTagRelDO::getArticleId, articleId));

        // 获取标签集合 (SelectRspVO格式)
        List<SelectRspVO> tags = null;
        if (!CollectionUtils.isEmpty(articleTagRelDOS)) {
            // 获取标签 ID 集合
            List<Long> tagIds = articleTagRelDOS.stream()
                    .map(ArticleTagRelDO::getTagId)
                    .collect(Collectors.toList());

            // 批量查询标签信息
            if (!CollectionUtils.isEmpty(tagIds)) {
                List<TagDO> tagDOS = tagMapper.selectBatchIds(tagIds);
                if (!CollectionUtils.isEmpty(tagDOS)) {
                    tags = tagDOS.stream()
                            .map(tagDO -> SelectRspVO.builder()
                                    .label(tagDO.getName())
                                    .value(tagDO.getId())
                                    .build())
                            .collect(Collectors.toList());
                }
            }
        }

        // 组装 VO 实体类
        FindArticleDetailRspVO vo = FindArticleDetailRspVO.builder()
                .id(articleId)
                .title(articleDO.getTitle())
                .cover(articleDO.getCover())
                .summary(articleDO.getSummary())
                .content(Objects.nonNull(articleContentDO) ? articleContentDO.getContent() : "")
                .categoryId(Objects.nonNull(articleCategoryRelDO) ? articleCategoryRelDO.getCategoryId() : null)
                .tags(tags)
                .articleSource(articleDO.getArticleSource())
                .articleSourceLabel(ArticleSourceEnum.getDescByCode(articleDO.getArticleSource()))
                .status(articleDO.getStatus())
                .statusLabel(resolveStatusLabel(articleDO.getStatus()))
                .visibilityScope(contentVisibilityService.normalizeScope(articleDO.getVisibilityScope()))
                .visibleUserIds(findVisibleUserIds(articleId))
                .createTime(articleDO.getCreateTime())
                .build();

        return Response.success(vo);
    }

    /**
     * 更新文章
     *
     * @param updateArticleReqVO
     * @return
     */
    @Override
    public Response updateArticle(UpdateArticleReqVO updateArticleReqVO) {
        boolean savingDraft = isDraftAction(updateArticleReqVO.getSubmitAction());
        String validateMessage = validateArticlePayloadForAction(updateArticleReqVO.getContent(), updateArticleReqVO.getCover(),
                updateArticleReqVO.getCategoryId(), savingDraft);
        if (StringUtils.isNotBlank(validateMessage)) {
            return Response.fail(validateMessage);
        }

        Long articleId = updateArticleReqVO.getId();

        // 1. 检查文章是否存在
        ArticleDO articleDO = articleMapper.selectById(articleId);
        if (Objects.isNull(articleDO)) {
            log.warn("==> 文章不存在, articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 2. 更新文章基本信息
        ArticleDO updateArticleDO = ArticleDO.builder()
                .id(articleId)
                .title(StringUtils.trimToEmpty(updateArticleReqVO.getTitle()))
                .cover(updateArticleReqVO.getCover())
                .summary(updateArticleReqVO.getSummary())
                .status(resolveAdminStatus(updateArticleReqVO.getSubmitAction()))
                .visibilityScope(resolveVisibilityScope(updateArticleReqVO.getVisibilityScope()))
                .updateTime(LocalDateTime.now())
                .build();
        articleMapper.updateById(updateArticleDO);

        // 3. 更新文章内容
        ArticleContentDO articleContentDO = ArticleContentDO.builder()
                .articleId(articleId)
                .content(StringUtils.defaultString(updateArticleReqVO.getContent()))
                .build();
        // 先删除旧的内容记录
        articleContentMapper.delete(new LambdaQueryWrapper<ArticleContentDO>()
                .eq(ArticleContentDO::getArticleId, articleId));
        // 插入新的内容记录
        articleContentMapper.insert(articleContentDO);

        // 4. 更新文章分类
        Long categoryId = updateArticleReqVO.getCategoryId();
        // 先删除旧的分类关联
        articleCategoryRelMapper.delete(new LambdaQueryWrapper<ArticleCategoryRelDO>()
                .eq(ArticleCategoryRelDO::getArticleId, articleId));
        if (Objects.nonNull(categoryId)) {
            CategoryDO categoryDO = categoryMapper.selectById(categoryId);
            if (Objects.isNull(categoryDO)) {
                log.warn("==> 分类不存在, categoryId: {}", categoryId);
                throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
            }
            // 插入新的分类关联
            ArticleCategoryRelDO articleCategoryRelDO = ArticleCategoryRelDO.builder()
                    .articleId(articleId)
                    .categoryId(categoryId)
                    .build();
            articleCategoryRelMapper.insert(articleCategoryRelDO);
        }

        // 5. 更新文章标签
        // 先删除旧的标签关联
        articleTagRelMapper.delete(new LambdaQueryWrapper<ArticleTagRelDO>()
                .eq(ArticleTagRelDO::getArticleId, articleId));
        // 插入新的标签关联
        List<String> publishTags = updateArticleReqVO.getTags();
        insertTags(articleId, publishTags);
        saveVisibleUsers(articleId, updateArticleDO.getVisibilityScope(), updateArticleReqVO.getVisibleUserIds());

        return Response.success();
    }

    /**
     * 审核文章
     *
     * @param aditArticleReqVO
     * @return
     */
    @Override
    public Response auditArticle(AuditArticleReqVO aditArticleReqVO) {
        Long articleId = aditArticleReqVO.getId();
        Integer status = aditArticleReqVO.getStatus();

        // 1. 检查文章是否存在
        ArticleDO articleDO = articleMapper.selectById(articleId);
        if (Objects.isNull(articleDO)) {
            log.warn("==> 文章不存在, articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        ArticleDO updateArticleDO = ArticleDO.builder()
                .id(articleId)
                .status(status)
                .updateTime(LocalDateTime.now())
                .build();
        articleMapper.updateById(updateArticleDO);

        return Response.success();
    }

    private Integer resolveVisibilityScope(Integer visibilityScope) {
        if (!contentVisibilityService.canCurrentUserConfigureVisibility()) {
            return VisibilityScopeEnum.PUBLIC.getCode();
        }
        return contentVisibilityService.normalizeScope(visibilityScope);
    }

    private void saveVisibleUsers(Long articleId, Integer visibilityScope, List<Long> visibleUserIds) {
        articleAccessUserMapper.delete(new LambdaQueryWrapper<ArticleAccessUserDO>()
                .eq(ArticleAccessUserDO::getArticleId, articleId));

        if (!VisibilityScopeEnum.ASSIGNED_USERS.getCode().equals(visibilityScope)) {
            return;
        }

        List<Long> normalizedUserIds = contentVisibilityService.normalizeAssignedUserIds(visibleUserIds);
        if (CollectionUtils.isEmpty(normalizedUserIds)) {
            return;
        }

        normalizedUserIds.forEach(userId -> articleAccessUserMapper.insert(ArticleAccessUserDO.builder()
                .articleId(articleId)
                .userId(userId)
                .createTime(LocalDateTime.now())
                .build()));
    }

    private List<Long> findVisibleUserIds(Long articleId) {
        return articleAccessUserMapper.selectList(new LambdaQueryWrapper<ArticleAccessUserDO>()
                        .eq(ArticleAccessUserDO::getArticleId, articleId))
                .stream()
                .map(ArticleAccessUserDO::getUserId)
                .collect(Collectors.toList());
    }

    private Integer resolveAdminStatus(String submitAction) {
        return isDraftAction(submitAction)
                ? ArticleStatusEnum.NON_PUBLISH.getCode()
                : ArticleStatusEnum.PUBLISH.getCode();
    }

    private boolean isDraftAction(String submitAction) {
        return ACTION_DRAFT.equalsIgnoreCase(StringUtils.defaultString(submitAction, ACTION_PUBLISH));
    }

    private String validateArticlePayloadForAction(String content, String cover, Long categoryId, boolean savingDraft) {
        if (savingDraft) {
            return null;
        }

        if (StringUtils.isBlank(content)) {
            return "文章内容不能为空";
        }
        if (StringUtils.isBlank(cover)) {
            return "文章封面不能为空";
        }
        if (Objects.isNull(categoryId)) {
            return "文章分类不能为空";
        }
        return null;
    }

    private String resolveStatusLabel(Integer status) {
        if (Objects.isNull(status)) {
            return "";
        }

        for (ArticleStatusEnum value : ArticleStatusEnum.values()) {
            if (Objects.equals(value.getCode(), status)) {
                return value.getStatus();
            }
        }

        return "";
    }

}
