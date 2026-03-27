package com.gaog.weblog.common.utils;

import com.gaog.weblog.common.domain.dos.CategoryDO;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 分类树工具类。
 */
public final class CategoryTreeUtil {

    private CategoryTreeUtil() {
    }

    /**
     * 构建分类完整路径名称。
     *
     * @param categoryId 分类 ID
     * @param categoryMap 分类映射
     * @return 完整路径名称
     */
    public static String buildCategoryPathName(Long categoryId, Map<Long, CategoryDO> categoryMap) {
        if (categoryId == null || categoryMap == null || categoryMap.isEmpty()) {
            return "";
        }

        List<String> names = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        Long currentId = categoryId;

        while (currentId != null && !visited.contains(currentId)) {
            visited.add(currentId);
            CategoryDO categoryDO = categoryMap.get(currentId);
            if (categoryDO == null) {
                break;
            }

            if (isNotBlank(categoryDO.getName())) {
                names.add(categoryDO.getName());
            }
            currentId = categoryDO.getParentId();
        }

        if (names.isEmpty()) {
            return "";
        }

        Collections.reverse(names);
        return String.join(" / ", names);
    }

    /**
     * 收集分类及其所有子分类 ID。
     *
     * @param categoryId 分类 ID
     * @param categories 分类列表
     * @return 分类及所有子分类 ID
     */
    public static List<Long> collectDescendantIds(Long categoryId, List<CategoryDO> categories) {
        if (categoryId == null || categories == null || categories.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, List<CategoryDO>> childrenMap = categories.stream()
                .filter(categoryDO -> categoryDO.getParentId() != null)
                .collect(Collectors.groupingBy(CategoryDO::getParentId, LinkedHashMap::new, Collectors.toList()));

        List<Long> result = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        Deque<Long> stack = new ArrayDeque<>();
        stack.push(categoryId);

        while (!stack.isEmpty()) {
            Long currentId = stack.pop();
            if (currentId == null || visited.contains(currentId)) {
                continue;
            }

            visited.add(currentId);
            result.add(currentId);

            List<CategoryDO> children = childrenMap.get(currentId);
            if (children == null || children.isEmpty()) {
                continue;
            }

            for (int i = children.size() - 1; i >= 0; i--) {
                stack.push(children.get(i).getId());
            }
        }

        return result;
    }

    /**
     * 判断字符串是否非空白。
     */
    private static boolean isNotBlank(String text) {
        return text != null && !text.trim().isEmpty();
    }
}
