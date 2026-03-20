package com.gaog.weblog.admin.model.vo.visitor;

import com.gaog.weblog.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "查询访客记录分页请求 VO")
public class FindVisitorLogPageListReqVO extends BasePageQuery {

    @ApiModelProperty(value = "IP 地址")
    private String ipAddress;

    @ApiModelProperty(value = "访客类型：member-登录用户 anonymous-匿名访客")
    private String visitorType;

    @ApiModelProperty(value = "开始日期")
    private LocalDate startDate;

    @ApiModelProperty(value = "结束日期")
    private LocalDate endDate;
}
