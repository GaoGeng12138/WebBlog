package com.gaog.weblog.admin.service.impl;

import com.gaog.weblog.admin.model.vo.setting.FindSiteSettingRspVO;
import com.gaog.weblog.admin.model.vo.setting.UpdateSiteSettingReqVO;
import com.gaog.weblog.admin.service.AdminSettingService;
import com.gaog.weblog.common.domain.dos.BlogSettingDO;
import com.gaog.weblog.common.domain.mapper.SiteSettingMapper;
import com.gaog.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class AdminSettingServiceImpl implements AdminSettingService {

    private static final String DEFAULT_ACTIVITY_LEVEL_RULES = "[{\"level\":1,\"minScore\":0,\"name\":\"新手\"},{\"level\":2,\"minScore\":100,\"name\":\"作者\"},{\"level\":3,\"minScore\":300,\"name\":\"进阶作者\"},{\"level\":4,\"minScore\":600,\"name\":\"资深作者\"},{\"level\":5,\"minScore\":1000,\"name\":\"传奇作者\"}]";
    private static final String DEFAULT_ACTIVITY_SCORE_RULES = "[{\"type\":\"article\",\"name\":\"发布文章\",\"score\":10},{\"type\":\"comment\",\"name\":\"发表评论\",\"score\":2},{\"type\":\"favorite\",\"name\":\"收藏文章\",\"score\":1},{\"type\":\"like\",\"name\":\"点赞评论\",\"score\":1},{\"type\":\"login\",\"name\":\"每日登录\",\"score\":1}]";

    @Autowired
    private SiteSettingMapper siteSettingMapper;

    @Override
    public Response<FindSiteSettingRspVO> findSiteSetting() {
        BlogSettingDO setting = siteSettingMapper.findSingleton();
        if (setting == null) {
            // 初始化一条默认记录 id=1
            setting = BlogSettingDO.builder()
                    .id(1L)
                    .title("我的博客")
                    .description("")
                    .frontendArticlePageSize(12)
                    .activityLevelRules(DEFAULT_ACTIVITY_LEVEL_RULES)
                    .activityScoreRules(DEFAULT_ACTIVITY_SCORE_RULES)
                    .githubEnabled(false)
                    .githubShowFront(false)
                    .githubShowRegister(false)
                    .twitterEnabled(false)
                    .twitterShowFront(false)
                    .twitterShowRegister(false)
                    .weiboEnabled(false)
                    .weiboShowFront(false)
                    .weiboShowRegister(false)
                    .commentEnabled(true)
                    .likeEnabled(true)
                    .favoriteEnabled(true)
                    .userRegisterEnabled(false)
                    .userPublishEnabled(false)
                    .articleReviewRequired(false)
                    .commentReviewRequired(false)
                    .anonymousCommentEnabled(false)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .isDeleted(false)
                    .build();
            siteSettingMapper.insert(setting);
        }
        FindSiteSettingRspVO rsp = FindSiteSettingRspVO.fromDO(setting);
        return Response.success(rsp);
    }

    @Override
    public Response<Void> updateSiteSetting(UpdateSiteSettingReqVO reqVO) {
        BlogSettingDO setting = siteSettingMapper.findSingleton();
        if (setting == null) {
            setting = BlogSettingDO.builder().id(1L).createTime(new Date()).isDeleted(false).build();
        }
        setting.setTitle(reqVO.getTitle());
        setting.setDescription(reqVO.getDescription());
        setting.setLogoUrl(reqVO.getLogoUrl());
        setting.setFrontendArticlePageSize(reqVO.getFrontendArticlePageSize() == null ? 12 : reqVO.getFrontendArticlePageSize());
        setting.setActivityLevelRules(reqVO.getActivityLevelRules());
        setting.setActivityScoreRules(reqVO.getActivityScoreRules());
        setting.setGithubEnabled(reqVO.getGithubEnabled());
        setting.setGithubShowFront(reqVO.getGithubShowFront());
        setting.setGithubShowRegister(reqVO.getGithubShowRegister());
        setting.setTwitterEnabled(reqVO.getTwitterEnabled());
        setting.setTwitterShowFront(reqVO.getTwitterShowFront());
        setting.setTwitterShowRegister(reqVO.getTwitterShowRegister());
        setting.setWeiboEnabled(reqVO.getWeiboEnabled());
        setting.setWeiboShowFront(reqVO.getWeiboShowFront());
        setting.setWeiboShowRegister(reqVO.getWeiboShowRegister());
        setting.setCommentEnabled(reqVO.getCommentEnabled());
        setting.setLikeEnabled(reqVO.getLikeEnabled());
        setting.setFavoriteEnabled(reqVO.getFavoriteEnabled());
        setting.setUserRegisterEnabled(reqVO.getUserRegisterEnabled());
        setting.setUserPublishEnabled(reqVO.getUserPublishEnabled());
        setting.setArticleReviewRequired(reqVO.getArticleReviewRequired());
        setting.setCommentReviewRequired(reqVO.getCommentReviewRequired());
        setting.setAnonymousCommentEnabled(reqVO.getAnonymousCommentEnabled());
        setting.setUpdateTime(new Date());

        if (setting.getId() == null) {
            setting.setId(1L);
            siteSettingMapper.insert(setting);
        } else {
            siteSettingMapper.updateById(setting);
        }
        return Response.success();
    }
}
