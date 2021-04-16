package com.xxx.core.model;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 分页返回实体类
 */
public class PageData<T> {
    /**
     * 页号
     */
    @ApiModelProperty("页号")
    private Integer pageNum = 1;

    /**
     * 页大小
     */
    @ApiModelProperty("页大小")
    private Integer pageSize=10;

    /**
     * 页数量
     */
    @ApiModelProperty("页数量")
    private Integer pages = 0;

    /**
     * 数据
     */
    @ApiModelProperty("数据")
    private List<T> list;

    /**
     * 总行数
     */
    @ApiModelProperty("总行数")
    private Long total = 0L;

    public Integer getPageNum() {
        return pageNum;
    }


    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
