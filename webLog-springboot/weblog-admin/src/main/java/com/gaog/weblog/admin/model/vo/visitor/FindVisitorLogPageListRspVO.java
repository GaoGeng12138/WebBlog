package com.gaog.weblog.admin.model.vo.visitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "查询访客记录分页响应 VO")
public class FindVisitorLogPageListRspVO {

    @ApiModelProperty(value = "主键 ID")
    private Long id;

    @ApiModelProperty(value = "用户 ID")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "IP 地址")
    private String ipAddress;

    @ApiModelProperty(value = "IP 归属地")
    private String ipLocation;

    @ApiModelProperty(value = "访客类型")
    private String visitorType;

    @ApiModelProperty(value = "访问日期")
    private LocalDate visitDate;

    @ApiModelProperty(value = "记录时间")
    private LocalDateTime createTime;
}
