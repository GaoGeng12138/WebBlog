package com.gaog.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gaog.weblog.admin.model.vo.tag.FindTagSelectListReqVO;
import com.gaog.weblog.admin.model.vo.tag.AddTagReqVO;
import com.gaog.weblog.admin.model.vo.tag.DeleteTagReqVO;
import com.gaog.weblog.admin.model.vo.tag.FindTagPageListReqVO;
import com.gaog.weblog.admin.model.vo.tag.FindTagPageListRspVO;
import com.gaog.weblog.admin.service.AdminTagService;
import com.gaog.weblog.common.domain.dos.TagDO;
import com.gaog.weblog.common.domain.mapper.TagMapper;
import com.gaog.weblog.common.enums.ResponseCodeEnum;
import com.gaog.weblog.common.exception.BizException;
import com.gaog.weblog.common.model.vo.SelectRspVO;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ZSJ
 * @date 2025/11/24 17:00
 * @description
 */
@Service
@Slf4j
public class AdminTagServiceImpl implements AdminTagService {

    @Autowired
    private TagMapper tagMapper;


    /**
     * 添加标签
     *
     * @param addTagReqVO
     * @return
     */
    @Override
    public Response addTag(AddTagReqVO addTagReqVO) {
        String tagName = addTagReqVO.getName();

        TagDO tagDO = tagMapper.selectByName(tagName);
        if (Objects.nonNull(tagDO)) {
            log.warn("标签名称： {}, 此标签已存在", tagName);
            throw new BizException(ResponseCodeEnum.TAG_NAME_IS_EXISTED);
        }
        // 构建 DO 类
        TagDO insertTagDO = TagDO.builder()
                .name(addTagReqVO.getName().trim())
                .build();

        // 执行 insert
        tagMapper.insert(insertTagDO);

        return Response.success();
    }

    /**
     * 分类分页数据查询
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

        String name = findTagPageListReqVO.getName();
        LocalDate startDate = findTagPageListReqVO.getStartDate();
        LocalDate endDate = findTagPageListReqVO.getEndDate();

        wrapper
                // like 模块查询
                .like(StringUtils.isNotBlank(name), TagDO::getName, name.trim())
                // 大于等于 startDate
                .ge(Objects.nonNull(startDate), TagDO::getCreateTime, startDate)
                // 小于等于 endDate
                .le(Objects.nonNull(endDate), TagDO::getCreateTime, endDate)
                // 按创建时间倒叙
                .orderByDesc(TagDO::getCreateTime);

        // 执行分页查询
        Page<TagDO> categoryDOPage = tagMapper.selectPage(page, wrapper);

        List<TagDO> categoryDOS = categoryDOPage.getRecords();

        // DO 转 VO
        List<FindTagPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(categoryDOS)) {
            vos = categoryDOS.stream()
                    .map(categoryDO -> FindTagPageListRspVO.builder()
                            .id(categoryDO.getId())
                            .name(categoryDO.getName())
                            .createTime(categoryDO.getCreateTime())
                            .build())
                    .collect(Collectors.toList());
        }
        return PageResponse.success(categoryDOPage, vos);
    }

    /**
     * 删除标签
     *
     * @param deleteTagReqVO
     * @return
     */
    @Override
    public Response deleteTag(DeleteTagReqVO deleteTagReqVO) {
        // 分类 ID
        Long categoryId = deleteTagReqVO.getId();

        // 删除标签
        tagMapper.deleteById(categoryId);

        return Response.success();
    }

    /**
     * 获取标签分类的 Select 列表数据
     *
     * @return
     */
    @Override
    public Response findTagSelectList(FindTagSelectListReqVO findTagSelectListReqVO) {
        String name = findTagSelectListReqVO.getName();

        LambdaQueryWrapper<TagDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), TagDO::getName, name.trim())
                .orderByDesc(TagDO::getCreateTime);

        // 查询所有分类
        List<TagDO> categoryDOS = tagMapper.selectList(wrapper);

        // DO 转 VO
        List<SelectRspVO> selectRspVOS = null;
        // 如果分类数据不为空
        if (!CollectionUtils.isEmpty(categoryDOS)) {
            // 将分类 ID 作为 Value 值，将分类名称作为 label 展示
            selectRspVOS = categoryDOS.stream()
                    .map(categoryDO -> SelectRspVO.builder()
                            .label(categoryDO.getName())
                            .value(categoryDO.getId())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(selectRspVOS);
    }
}
