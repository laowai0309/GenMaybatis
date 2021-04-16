package com.bbx.mybatisUtils;

public class ColumnInfo {
    /**
     * 字段对应的属性名称 - 首字母小写
     */
    private String name;

    /**
     * 字段对应的属性名称 - 首字母大写
     */
    private String u_name;

    /**
     * 属性类型
     */
    private String type;

    /**
     * 对应的JDBC类型
     */
    private String jdbc_type;

    /**
     * 属性需要添加的注解
     */
    private String annotation;

    /**
     * 数据库字段名称
     */
    private String db_name;

    /**
     *  数据库字段类型
     */
    private String db_type;

    /**
     * 数据库字段注释
     */
    private String db_comments;

    /**
     * 是否为数据库主键
     */
    private Boolean db_key;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getU_name() {
        return u_name;
    }

    public void setU_name(String u_name) {
        this.u_name = u_name;
    }

    public String getJdbc_type() {
        return jdbc_type;
    }

    public void setJdbc_type(String jdbc_type) {
        this.jdbc_type = jdbc_type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getDb_name() {
        return db_name;
    }

    public void setDb_name(String db_name) {
        this.db_name = db_name;
    }

    public String getDb_type() {
        return db_type;
    }

    public void setDb_type(String db_type) {
        this.db_type = db_type;
    }

    public String getDb_comments() {
        return db_comments;
    }

    public void setDb_comments(String db_comments) {
        this.db_comments = db_comments;
    }

    public Boolean getDb_key() {
        return db_key;
    }

    public void setDb_key(Boolean db_key) {
        this.db_key = db_key;
    }
}
