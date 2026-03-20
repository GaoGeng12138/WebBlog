package com.gaog.weblog.web.model.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gaoge
 * @description 当前访问位置响应 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("当前访问位置响应 VO")
public class UserCurrentLocationRspVO {

    @ApiModelProperty("当前访问 IP")
    private String ipAddress;

    @ApiModelProperty("省份")
    private String province;

    @ApiModelProperty("城市")
    private String city;

    @ApiModelProperty("完整位置描述")
    private String location;
}
