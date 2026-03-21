package com.gaog.weblog.admin.permission;

import com.gaog.weblog.admin.model.vo.role.PermissionTreeNodeRspVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class AdminPermissionCatalog {

    private static final List<PermissionTreeNodeRspVO> PERMISSION_TREE = Arrays.asList(
            menu(100L, "仪表盘", "admin:dashboard:view"),
            menu(200L, "文章管理", "admin:article:list",
                    button(201L, "发布文章", "admin:article:publish"),
                    button(202L, "编辑文章", "admin:article:update"),
                    button(203L, "删除文章", "admin:article:delete"),
                    button(204L, "审核文章", "admin:article:audit")),
            menu(300L, "分类管理", "admin:category:list",
                    button(301L, "新增分类", "admin:category:add"),
                    button(302L, "删除分类", "admin:category:delete"),
                    button(303L, "更新前台展示", "admin:category:update-front")),
            menu(400L, "标签管理", "admin:tag:list",
                    button(401L, "新增标签", "admin:tag:add"),
                    button(402L, "删除标签", "admin:tag:delete")),
            menu(450L, "评论管理", "admin:comment:list",
                    button(451L, "审核评论", "admin:comment:audit"),
                    button(452L, "删除评论", "admin:comment:delete"),
                    button(453L, "置顶评论", "admin:comment:top")),
            menu(500L, "用户管理", "admin:user:list",
                    button(501L, "新增用户", "admin:user:create"),
                    button(502L, "编辑用户", "admin:user:update"),
                    button(503L, "删除用户", "admin:user:delete"),
                    button(504L, "更新用户状态", "admin:user:status"),
                    button(505L, "修改用户密码", "admin:user:password"),
                    button(506L, "分配用户角色", "admin:user:role-assign")),
            menu(600L, "角色管理", "admin:role:list",
                    button(601L, "新增角色", "admin:role:add"),
                    button(602L, "编辑角色", "admin:role:update"),
                    button(603L, "删除角色", "admin:role:delete"),
                    button(604L, "分配角色权限", "admin:role:permission-assign")),
            menu(700L, "访客记录", "admin:visitor:list"),
            menu(800L, "博客设置", "admin:setting:view",
                    button(801L, "更新站点设置", "admin:setting:update"))
    );

    private static final Map<Long, String> ID_TO_KEY = new LinkedHashMap<>();

    static {
        flatten(PERMISSION_TREE).forEach(node -> ID_TO_KEY.put(node.getId(), node.getPermissionKey()));
    }

    private AdminPermissionCatalog() {
    }

    public static List<PermissionTreeNodeRspVO> getPermissionTree() {
        return PERMISSION_TREE;
    }

    public static List<String> getPermissionKeysByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        return ids.stream()
                .map(ID_TO_KEY::get)
                .filter(key -> key != null && !key.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<Long> getPermissionIdsByKeys(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }

        return ID_TO_KEY.entrySet().stream()
                .filter(entry -> keys.contains(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private static PermissionTreeNodeRspVO menu(Long id, String name, String permissionKey, PermissionTreeNodeRspVO... children) {
        return PermissionTreeNodeRspVO.builder()
                .id(id)
                .name(name)
                .type("menu")
                .permissionKey(permissionKey)
                .children(children == null ? Collections.emptyList() : Arrays.asList(children))
                .build();
    }

    private static PermissionTreeNodeRspVO button(Long id, String name, String permissionKey) {
        return PermissionTreeNodeRspVO.builder()
                .id(id)
                .name(name)
                .type("button")
                .permissionKey(permissionKey)
                .children(Collections.emptyList())
                .build();
    }

    private static List<PermissionTreeNodeRspVO> flatten(List<PermissionTreeNodeRspVO> nodes) {
        List<PermissionTreeNodeRspVO> result = new ArrayList<>();
        if (nodes == null) {
            return result;
        }

        for (PermissionTreeNodeRspVO node : nodes) {
            result.add(node);
            result.addAll(flatten(node.getChildren()));
        }

        return result;
    }
}
