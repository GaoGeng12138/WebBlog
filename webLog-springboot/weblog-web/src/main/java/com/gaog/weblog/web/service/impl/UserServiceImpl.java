package com.gaog.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gaog.weblog.common.domain.dos.UserDO;
import com.gaog.weblog.common.domain.mapper.UserMapper;
import com.gaog.weblog.common.domain.mapper.UserRoleMapper;
import com.gaog.weblog.common.enums.ResponseCodeEnum;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.jwt.model.CustomUserDetails;
import com.gaog.weblog.jwt.utils.SecurityContextUtil;
import com.gaog.weblog.common.domain.dos.ArticleDO;
import com.gaog.weblog.common.domain.dos.BlogSettingDO;
import com.gaog.weblog.common.domain.dos.CommentDO;
import com.gaog.weblog.common.domain.dos.UserFavoriteArticleDO;
import com.gaog.weblog.common.domain.mapper.ArticleMapper;
import com.gaog.weblog.common.domain.mapper.CommentMapper;
import com.gaog.weblog.common.domain.mapper.SiteSettingMapper;
import com.gaog.weblog.common.domain.mapper.UserFavoriteArticleMapper;
import com.gaog.weblog.common.utils.IpLocationUtil;
import com.gaog.weblog.common.utils.IpUtil;
import com.gaog.weblog.web.model.vo.user.RegisterUserReqVO;
import com.gaog.weblog.web.model.vo.user.UserCurrentLocationRspVO;
import com.gaog.weblog.web.model.vo.user.RegisterUserRspVO;
import com.gaog.weblog.web.model.vo.user.UserCenterStatsVO;
import com.gaog.weblog.web.model.vo.user.UserCommentHistoryVO;
import com.gaog.weblog.web.model.vo.user.UserDynamicVO;
import com.gaog.weblog.web.model.vo.user.UserActivityScoreReqVO;
import com.gaog.weblog.web.model.vo.user.UserActivityScoreRspVO;
import com.gaog.weblog.web.model.vo.userinfo.UserInfoVO;
import com.gaog.weblog.web.service.UserActivityScoreService;
import com.gaog.weblog.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Gao
 * @description 前台用户服务实现类
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private ArticleMapper articleMapper;
    
    @Autowired
    private UserFavoriteArticleMapper userFavoriteArticleMapper;
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private UserActivityScoreService userActivityScoreService;

    @Autowired
    private SiteSettingMapper siteSettingMapper;

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    @Override
    public Response getUserInfo() {
        // 直接从 SecurityContext 中获取用户信息，无需查询数据库
        CustomUserDetails currentUser = SecurityContextUtil.getCurrentUser();

        if (currentUser == null) {
            return Response.fail("用户未登录");
        }

        // 组装返回数据，直接使用 CustomUserDetails 中的信息
        UserInfoVO userInfoVO = UserInfoVO.builder()
                .userId(currentUser.getUserId())
                .username(currentUser.getUsername())
                .avatar(currentUser.getAvatar())
                .introduction(currentUser.getIntroduction())
                .email(currentUser.getEmail())
                .nickname(currentUser.getNickname())
                .githubUrl(currentUser.getGithubUrl())
                .twitterUrl(currentUser.getTwitterUrl())
                .weiboUrl(currentUser.getWeiboUrl())
                .roles(new HashSet<>(currentUser.getRoles()))
                .registerTime(currentUser.getCreateTime())
                .build();

        return Response.success(userInfoVO);
    }

    /**
     * 获取用户中心统计数据（文章数、收藏数、评论数）
     *
     * @return
     */
    @Override
    public Response getUserCenterStats() {
        // 获取当前登录用户信息
        Long userId = SecurityContextUtil.getCurrentUserId();

        // 查询用户发布的文章数量
        Long articleCount = articleMapper.selectCount(
            new LambdaQueryWrapper<ArticleDO>()
                .eq(ArticleDO::getUserId, userId)
                .eq(ArticleDO::getIsDeleted, false)
        );
        
        // 查询用户收藏的文章数量
        Long favoriteCount = userFavoriteArticleMapper.selectCount(
            new LambdaQueryWrapper<UserFavoriteArticleDO>()
                .eq(UserFavoriteArticleDO::getUserId, userId)
        );
        
        // 查询用户发表的评论数量
        Long commentCount = commentMapper.selectCount(
            new LambdaQueryWrapper<CommentDO>()
                .eq(CommentDO::getUserId, userId)
                .eq(CommentDO::getIsDeleted, false)
        );
        
        // 组装返回数据
        UserCenterStatsVO statsVO = UserCenterStatsVO.builder()
                .articleCount(articleCount)
                .favoriteCount(favoriteCount)
                .commentCount(commentCount)
                .build();
        
        return Response.success(statsVO);
    }

    @Override
    public Response<UserCurrentLocationRspVO> getCurrentLocation(HttpServletRequest request) {
        String ipAddress = IpUtil.getClientIp(request);
        IpLocationUtil.LocationInfo locationInfo = IpLocationUtil.resolveLocationInfo(ipAddress);

        UserCurrentLocationRspVO rspVO = UserCurrentLocationRspVO.builder()
                .ipAddress(ipAddress)
                .province(locationInfo.getProvince())
                .city(locationInfo.getCity())
                .location(locationInfo.getDisplayText())
                .build();

        return Response.success(rspVO);
    }

    /**
     * 用户注册
     *
     * @param registerUserReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response register(RegisterUserReqVO registerUserReqVO) {
        BlogSettingDO setting = siteSettingMapper.findSingleton();
        if (setting != null && Boolean.FALSE.equals(setting.getUserRegisterEnabled())) {
            return Response.fail(ResponseCodeEnum.USER_REGISTER_DISABLED);
        }

        String username = registerUserReqVO.getUsername();
        String password = registerUserReqVO.getPassword();
        String email = registerUserReqVO.getEmail();
        String nickname = registerUserReqVO.getNickname();
        String introduction = registerUserReqVO.getIntroduction();
        String avatar = registerUserReqVO.getAvatar();
        String githubUrl = registerUserReqVO.getGithubUrl();
        String twitterUrl = registerUserReqVO.getTwitterUrl();
        String weiboUrl = registerUserReqVO.getWeiboUrl();


        // 1. 检查用户名是否已存在
        UserDO existUser = userMapper.findByUsername(username);
        if (existUser != null) {
            log.warn("用户注册失败，用户名已存在: {}", username);
            return Response.fail(ResponseCodeEnum.USERNAME_ALREADY_EXISTS);
        }

        // 2. 加密密码
        String encodedPassword = passwordEncoder.encode(password);

        // 3. 构建用户实体，默认未启用状态
        UserDO userDO = UserDO.builder()
                .username(username)
                .password(encodedPassword)
                .email(email)
                .nickname(nickname)
                .introduction(introduction)
                .avatar(avatar)
                .githubUrl(githubUrl)
                .twitterUrl(twitterUrl)
                .weiboUrl(weiboUrl)
                // 默认未启用，需要管理员审核
                .isEnabled(false)
                .createTime(new Date())
                .updateTime(new Date())
                .isDeleted(false)
                .build();

        // 4. 保存到数据库
        int count = userMapper.insert(userDO);
        if (count != 1) {
            log.error("用户注册失败，数据库插入失败: {}", username);
            return Response.fail(ResponseCodeEnum.REGISTRATION_FAILED);
        }

        log.info("用户注册成功，等待管理员审核: {}", username);

        // 5. 返回注册成功响应
        RegisterUserRspVO rspVO = RegisterUserRspVO.builder()
                .username(username)
                .message("注册成功，请等待管理员审核后再登录")
                .build();

        return Response.success(rspVO);
    }
    
    /**
     * 获取用户评论历史
     *
     * @param current 当前页码
     * @param size 每页大小
     * @return
     */
    @Override
    public PageResponse getUserCommentHistory(Long current, Long size) {
        // 获取当前登录用户ID
        Long userId = SecurityContextUtil.getCurrentUserId();
        
        // 分页查询用户评论
        Page<CommentDO> page = new Page<>(current, size);
        Page<CommentDO> commentPage = commentMapper.selectPage(page, 
            new LambdaQueryWrapper<CommentDO>()
                .eq(CommentDO::getUserId, userId)
                .eq(CommentDO::getIsDeleted, false)
                .orderByDesc(CommentDO::getCreateTime));
        
        // 转换为VO
        List<UserCommentHistoryVO> commentVOs = commentPage.getRecords().stream()
            .map(comment -> {
                // 查询对应的文章标题
                String articleTitle = "";
                ArticleDO article = articleMapper.selectById(comment.getArticleId());
                if (article != null) {
                    articleTitle = article.getTitle();
                }
                
                return UserCommentHistoryVO.builder()
                    .id(comment.getId())
                    .articleId(comment.getArticleId())
                    .articleTitle(articleTitle)
                    .content(comment.getContent())
                    .likeCount(comment.getLikeCount())
                    .createTime(comment.getCreateTime())
                    .build();
            })
            .collect(Collectors.toList());
        
        return PageResponse.success(commentPage, commentVOs);
    }
    
    /**
     * 获取用户动态（文章、评论、收藏等）
     *
     * @param current 当前页码
     * @param size 每页大小
     * @return
     */
    @Override
    public PageResponse getUserDynamics(Long current, Long size) {
        // 获取当前登录用户ID
        Long userId = SecurityContextUtil.getCurrentUserId();
        
        // 查询用户发布的文章
        List<ArticleDO> articles = articleMapper.selectList(
            new LambdaQueryWrapper<ArticleDO>()
                .eq(ArticleDO::getUserId, userId)
                .eq(ArticleDO::getIsDeleted, false)
                .orderByDesc(ArticleDO::getCreateTime)
                .last("LIMIT " + (current * size)));
        
        // 查询用户的评论
        List<CommentDO> comments = commentMapper.selectList(
            new LambdaQueryWrapper<CommentDO>()
                .eq(CommentDO::getUserId, userId)
                .eq(CommentDO::getIsDeleted, false)
                .orderByDesc(CommentDO::getCreateTime)
                .last("LIMIT " + (current * size)));
        
        // 查询用户的收藏
        List<UserFavoriteArticleDO> favorites = userFavoriteArticleMapper.selectList(
            new LambdaQueryWrapper<UserFavoriteArticleDO>()
                .eq(UserFavoriteArticleDO::getUserId, userId)
                .orderByDesc(UserFavoriteArticleDO::getCreateTime)
                .last("LIMIT " + (current * size)));
        
        // 合并所有动态并排序
        List<UserDynamicVO> dynamics = articles.stream()
            .map(article -> UserDynamicVO.builder()
                .id(article.getId())
                .type(1) // 文章类型
                .relatedId(article.getId())
                .title(article.getTitle())
                .contentPreview(article.getSummary() != null ? article.getSummary() : "")
                .createTime(article.getCreateTime())
                .build())
            .collect(Collectors.toList());
            
        comments.stream()
            .forEach(comment -> {
                // 查询对应的文章标题
                String articleTitle = "";
                ArticleDO article = articleMapper.selectById(comment.getArticleId());
                if (article != null) {
                    articleTitle = article.getTitle();
                }
                
                dynamics.add(UserDynamicVO.builder()
                    .id(comment.getId())
                    .type(2) // 评论类型
                    .relatedId(comment.getId())
                    .title("评论了文章: " + articleTitle)
                    .contentPreview(comment.getContent().length() > 50 ? 
                        comment.getContent().substring(0, 50) + "..." : comment.getContent())
                    .createTime(comment.getCreateTime())
                    .build());
            });
            
        favorites.stream()
            .forEach(favorite -> {
                // 查询对应的文章标题
                String articleTitle = "";
                ArticleDO article = articleMapper.selectById(favorite.getArticleId());
                if (article != null) {
                    articleTitle = article.getTitle();
                }
                
                dynamics.add(UserDynamicVO.builder()
                    .id(favorite.getId())
                    .type(3) // 收藏类型
                    .relatedId(favorite.getArticleId())
                    .title("收藏了文章: " + articleTitle)
                    .contentPreview("")
                    .createTime(favorite.getCreateTime())
                    .build());
            });
        
        // 按时间倒序排序
        dynamics.sort((d1, d2) -> d2.getCreateTime().compareTo(d1.getCreateTime()));
        
        // 分页处理
        int fromIndex = (int) ((current - 1) * size);
        int toIndex = (int) Math.min(fromIndex + size, dynamics.size());
        List<UserDynamicVO> pagedDynamics = dynamics.subList(fromIndex, toIndex);
        
        // 创建分页对象
        Page<UserDynamicVO> page = new Page<>(current, size);
        page.setTotal(dynamics.size());
        page.setRecords(pagedDynamics);
        
        return PageResponse.success(page, pagedDynamics);
    }
    
    /**
     * 获取用户活跃度积分
     *
     * @param reqVO 请求参数
     * @return
     */
    @Override
    public Response<UserActivityScoreRspVO> getUserActivityScore(UserActivityScoreReqVO reqVO) {
        return userActivityScoreService.getActivityScore(reqVO);
    }
}
