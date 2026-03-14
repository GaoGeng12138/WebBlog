package com.gaog.weblog.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/23 12:24
 * @Version: 1.0
 * @Description:
 */
@Data
public class PageResponse<T> extends Response<List<T>> implements Serializable {

    private static final long serialVersionUID = -5553298198979158581L;


    /**
     * 总记录数
     */
    private long total = 0L;

    /**
     * 每页显示的记录数，默认每页显示 10 条
     */
    private long size = 10L;

    /**
     * 当前页码
     */
    private long current;

    /**
     * 总页数
     */
    private long pages;

    /**
     * 成功响应
     *
     * @param page Mybatis Plus 提供的分页接口
     * @param data
     * @param <T>
     * @return
     */
    public static <T> PageResponse<T> success(IPage page, List<T> data) {
        PageResponse<T> response = new PageResponse<>();
        response.setSuccess(true);
        response.setCurrent(Objects.isNull(page) ? 1L : page.getCurrent());
        response.setSize(Objects.isNull(page) ? 10L : page.getSize());
        response.setPages(Objects.isNull(page) ? 0L : page.getPages());
        response.setTotal(Objects.isNull(page) ? 0L : page.getTotal());
        response.setData(data);
        return response;
    }

    /**
     * 失败响应
     *
     * @param <T>
     * @return
     */
    public static <T> PageResponse<T> failed() {
        PageResponse<T> response = new PageResponse<>();
        response.setSuccess(false);
        return response;
    }

    /**
     * 失败响应
     *
     * @param errorMessage
     * @param <T>
     * @return
     */
    public static <T> PageResponse<T> failed(String errorMessage) {
        PageResponse<T> response = new PageResponse<>();
        response.setSuccess(false);
        response.setMessage(errorMessage);
        return response;
    }

    /**
     * 失败响应
     *
     * @param errorCode
     * @param errorMessage
     * @param <T>
     * @return
     */
    public static <T> PageResponse<T> failed(String errorCode, String errorMessage) {
        PageResponse<T> response = new PageResponse<>();
        response.setSuccess(false);
        response.setErrorCode(errorCode);
        response.setMessage(errorMessage);
        return response;
    }
}