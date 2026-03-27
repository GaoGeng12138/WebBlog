package com.gaog.weblog.admin.controller;

import com.gaog.weblog.admin.service.AdminFileService;
import com.gaog.weblog.common.aspect.ApiOperationLog;
import com.gaog.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ZSJ
 * @date 2025/11/25 17:32
 * @description 文件上传控制器
 */
@RestController
@RequestMapping("/admin/file")
@Api(tags = "Admin 文件模块")
public class AdminFileController {

    @Autowired
    private AdminFileService fileService;

    @PostMapping("/upload")
    @ApiOperation(value = "文件上传")
    @ApiOperationLog(description = "文件上传")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:article:list','admin:user:list','admin:setting:view')")
    public Response uploadFile(@RequestParam("file") MultipartFile file) {
        return fileService.uploadFile(file);
    }

    /**
     * 解析上传的 Word 文档并返回 Markdown。
     *
     * @param file Word 文件
     * @return Markdown 内容
     */
    @PostMapping("/word/parse")
    @ApiOperation(value = "Word 文档解析")
    @ApiOperationLog(description = "Word 文档解析")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:article:list','admin:article:publish')")
    public Response parseWordFile(@RequestParam("file") MultipartFile file) {
        return fileService.parseWordFile(file);
    }
}
