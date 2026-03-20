package com.gaog.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gaog.weblog.admin.model.vo.visitor.FindVisitorLogPageListReqVO;
import com.gaog.weblog.admin.model.vo.visitor.FindVisitorLogPageListRspVO;
import com.gaog.weblog.admin.service.AdminVisitorService;
import com.gaog.weblog.common.domain.dos.UserDO;
import com.gaog.weblog.common.domain.dos.VisitorLogDO;
import com.gaog.weblog.common.domain.mapper.UserMapper;
import com.gaog.weblog.common.domain.mapper.VisitorLogMapper;
import com.gaog.weblog.common.utils.IpLocationUtil;
import com.gaog.weblog.common.utils.PageResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AdminVisitorServiceImpl implements AdminVisitorService {

    @Autowired
    private VisitorLogMapper visitorLogMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public PageResponse findVisitorLogPageList(FindVisitorLogPageListReqVO reqVO) {
        Page<VisitorLogDO> page = new Page<>(reqVO.getCurrent(), reqVO.getSize());

        LambdaQueryWrapper<VisitorLogDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(reqVO.getIpAddress()), VisitorLogDO::getIpAddress, reqVO.getIpAddress())
                .ge(Objects.nonNull(reqVO.getStartDate()), VisitorLogDO::getVisitDate, reqVO.getStartDate())
                .le(Objects.nonNull(reqVO.getEndDate()), VisitorLogDO::getVisitDate, reqVO.getEndDate())
                .orderByDesc(VisitorLogDO::getCreateTime);

        if ("member".equalsIgnoreCase(reqVO.getVisitorType())) {
            wrapper.isNotNull(VisitorLogDO::getUserId);
        } else if ("anonymous".equalsIgnoreCase(reqVO.getVisitorType())) {
            wrapper.isNull(VisitorLogDO::getUserId);
        }

        Page<VisitorLogDO> visitorPage = visitorLogMapper.selectPage(page, wrapper);
        List<VisitorLogDO> records = visitorPage.getRecords();

        Set<Long> userIds = records.stream()
                .map(VisitorLogDO::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, UserDO> userMap = userIds.isEmpty()
                ? Collections.emptyMap()
                : userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(UserDO::getId, Function.identity(), (left, right) -> left));

        List<FindVisitorLogPageListRspVO> vos = records.stream()
                .map(item -> {
                    UserDO userDO = item.getUserId() == null ? null : userMap.get(item.getUserId());
                    return FindVisitorLogPageListRspVO.builder()
                            .id(item.getId())
                            .userId(item.getUserId())
                            .username(userDO == null ? null : userDO.getUsername())
                            .ipAddress(item.getIpAddress())
                            .ipLocation(IpLocationUtil.resolveLocation(item.getIpAddress()))
                            .visitorType(item.getUserId() == null ? "anonymous" : "member")
                            .visitDate(item.getVisitDate())
                            .createTime(item.getCreateTime())
                            .build();
                })
                .collect(Collectors.toList());

        return PageResponse.success(visitorPage, vos);
    }
}
