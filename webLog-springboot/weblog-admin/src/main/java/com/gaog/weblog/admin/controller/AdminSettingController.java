package com.gaog.weblog.admin.controller;

import com.gaog.weblog.admin.model.vo.setting.FindSiteSettingRspVO;
import com.gaog.weblog.admin.model.vo.setting.UpdateSiteSettingReqVO;
import com.gaog.weblog.admin.service.AdminSettingService;
import com.gaog.weblog.common.aspect.ApiOperationLog;
import com.gaog.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/blog")
@Api(tags = "Admin 站点设置模块")
public class AdminSettingController {

    @Autowired
    private AdminSettingService adminSettingService;

    @PostMapping("/settings/get")
    @ApiOperation(value = "获取站点设置")
    @ApiOperationLog(description = "获取站点设置")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:setting:view')")
    public Response<FindSiteSettingRspVO> findSiteSetting() {
        return adminSettingService.findSiteSetting();
    }

    @PostMapping("/settings/update")
    @ApiOperation(value = "更新站点设置")
    @ApiOperationLog(description = "更新站点设置")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:setting:update')")
    public Response<Void> updateSiteSetting(@RequestBody @Validated UpdateSiteSettingReqVO reqVO) {
        return adminSettingService.updateSiteSetting(reqVO);
    }
}
