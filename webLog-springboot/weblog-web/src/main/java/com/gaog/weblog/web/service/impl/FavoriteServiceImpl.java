package com.gaog.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gaog.weblog.admin.model.vo.article.FindArticlePageListRspVO;
import com.gaog.weblog.common.domain.dos.ArticleDO;
import com.gaog.weblog.common.domain.dos.BlogSettingDO;
import com.gaog.weblog.common.domain.dos.UserFavoriteArticleDO;
import com.gaog.weblog.common.domain.mapper.ArticleMapper;
import com.gaog.weblog.common.domain.mapper.SiteSettingMapper;
import com.gaog.weblog.common.domain.mapper.UserFavoriteArticleMapper;
import com.gaog.weblog.common.enums.ResponseCodeEnum;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.jwt.model.CustomUserDetails;
import com.gaog.weblog.jwt.utils.SecurityContextUtil;
import com.gaog.weblog.web.service.FavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private UserFavoriteArticleMapper userFavoriteArticleMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private SiteSettingMapper siteSettingMapper;

    private Response<?> checkFavoriteEnabled() {
        BlogSettingDO setting = siteSettingMapper.findSingleton();
        if (setting != null && Boolean.FALSE.equals(setting.getFavoriteEnabled())) {
            return Response.fail(ResponseCodeEnum.FAVORITE_DISABLED);
        }
        return null;
    }

    @Override
    public Response favoriteArticle(Long articleId) {
        Response<?> disabledResponse = checkFavoriteEnabled();
        if (disabledResponse != null) {
            return disabledResponse;
        }

        // 获取当前登录用户信息
        CustomUserDetails userDetails = SecurityContextUtil.getCurrentUser();
        Long userId = userDetails.getUserId();
        
        // 检查文章是否存在
        ArticleDO articleDO = articleMapper.selectById(articleId);
        if (Objects.isNull(articleDO)) {
            return Response.fail("文章不存在");
        }
        
        // 检查是否已收藏
        UserFavoriteArticleDO favoriteDO = userFavoriteArticleMapper.selectOne(new LambdaQueryWrapper<UserFavoriteArticleDO>()
                .eq(UserFavoriteArticleDO::getUserId, userId)
                .eq(UserFavoriteArticleDO::getArticleId, articleId));
                
        if (Objects.nonNull(favoriteDO)) {
            return Response.fail("已收藏该文章");
        }
        
        // 添加收藏记录
        UserFavoriteArticleDO userFavoriteArticleDO = UserFavoriteArticleDO.builder()
                .userId(userId)
                .articleId(articleId)
                .build();
        userFavoriteArticleMapper.insert(userFavoriteArticleDO);
        
        return Response.success("收藏成功");
    }

    @Override
    public Response unfavoriteArticle(Long articleId) {
        Response<?> disabledResponse = checkFavoriteEnabled();
        if (disabledResponse != null) {
            return disabledResponse;
        }

        // 获取当前登录用户信息
        CustomUserDetails userDetails = SecurityContextUtil.getCurrentUser();

        Long userId = userDetails.getUserId();
        
        // 删除收藏记录
        userFavoriteArticleMapper.delete(new LambdaQueryWrapper<UserFavoriteArticleDO>()
                .eq(UserFavoriteArticleDO::getUserId, userId)
                .eq(UserFavoriteArticleDO::getArticleId, articleId));
        
        return Response.success("取消收藏成功");
    }

    @Override
    public Response getFavoriteArticles(Long current, Long size) {
        Response<?> disabledResponse = checkFavoriteEnabled();
        if (disabledResponse != null) {
            return disabledResponse;
        }

        // 获取当前登录用户信息
        CustomUserDetails userDetails = SecurityContextUtil.getCurrentUser();
        Long userId = userDetails.getUserId();
        
        // 分页查询用户收藏的文章ID列表
        IPage<UserFavoriteArticleDO> page = new Page<>(current, size);
        IPage<UserFavoriteArticleDO> favoritePage = userFavoriteArticleMapper.selectPage(page, new LambdaQueryWrapper<UserFavoriteArticleDO>()
                .eq(UserFavoriteArticleDO::getUserId, userId)
                .orderByDesc(UserFavoriteArticleDO::getCreateTime));
        long total = favoritePage.getTotal();
        if (total == 0){
            return PageResponse.success(page, null);
        }

        // 获取文章ID列表
        List<Long> articleIds = favoritePage.getRecords().stream()
                .map(UserFavoriteArticleDO::getArticleId)
                .collect(Collectors.toList());
        
        // 查询文章详情
        List<ArticleDO> articleDOS = articleMapper.selectBatchIds(articleIds);
        
        // DO 转 VO
        List<FindArticlePageListRspVO> vos = articleDOS.stream()
                .map(articleDO -> FindArticlePageListRspVO.builder()
                        .id(articleDO.getId())
                        .title(articleDO.getTitle())
                        .author(articleDO.getAuthor())
                        .cover(articleDO.getCover())
                        .summary(articleDO.getSummary())
                        .createTime(articleDO.getCreateTime())
                        .build())
                .collect(Collectors.toList());
        
        return PageResponse.success(favoritePage, vos);
    }

    @Override
    public Response isFavorited(Long articleId) {
        Response<?> disabledResponse = checkFavoriteEnabled();
        if (disabledResponse != null) {
            return disabledResponse;
        }

        // 获取当前登录用户信息
        CustomUserDetails userDetails = SecurityContextUtil.getCurrentUser();
        if (userDetails == null) {
            log.error("Failed to get current user from security context");
            return Response.fail("用户未登录");
        }
        Long userId = userDetails.getUserId();
        
        // 查询是否已收藏
        UserFavoriteArticleDO favoriteDO = userFavoriteArticleMapper.selectOne(new LambdaQueryWrapper<UserFavoriteArticleDO>()
                .eq(UserFavoriteArticleDO::getUserId, userId)
                .eq(UserFavoriteArticleDO::getArticleId, articleId));
        
        boolean isFavorited = Objects.nonNull(favoriteDO);
        return Response.success(isFavorited);
    }
}
