package com.bbx.mybatisUtils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.ini4j.Profile;
import org.ini4j.Wini;

import java.io.*;
import java.sql.*;
import java.util.*;


public class GenMybatisHandle {

    // 表注释
    final String SQL_TAB_COMMENTS  = "SELECT TABLE_NAME, COMMENTS FROM USER_TAB_COMMENTS WHERE TABLE_NAME = ?";

    // 获得表主键
    final String SQL_CONSTRAINT = "SELECT B.COLUMN_NAME FROM USER_CONSTRAINTS A, USER_CONS_COLUMNS B " +
            "WHERE A.CONSTRAINT_NAME = B.CONSTRAINT_NAME AND A.CONSTRAINT_TYPE = 'P' AND B.TABLE_NAME= ? ";

    // 表包含的列
    final String SQL_TAB_COLUMNS = "SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE FROM USER_TAB_COLUMNS WHERE TABLE_NAME = ?";

    // 列注释
    final String SQL_COL_COMMENTS = "SELECT TABLE_NAME, COLUMN_NAME, COMMENTS FROM USER_COL_COMMENTS WHERE TABLE_NAME = ?";


    /**
     * 路径
     */
    private String basePath;

    /**
     * 配置文件名称
     */
    private String cfg_file;

    /**
     * 数据库url
     */
    private String url;

    /**
     * 数据库驱动名称
     */
    private String driverClassName;

    /**
     * 数据库用户名
     */
    private String userName;

    /**
     * 数据库密码
     */
    private String password;


    /**
     * 表前缀
     */
    private String table_prefix;

    /**
     * 原始表名
     */
    private String db_table_name;

    /**
     * 主键列
     */
    private String db_id_name;

    /**
     * 实体id名称
     */
    private String id_name;

    /**
     * 实体id名称首字母大写
     */
    private String id_u_name;


    /**
     * 实体id 类型
     */
    private String id_type;



    /**
     * 实体id 注释
     */
    private String id_comments;


    /**
     * 实体名
     */
    private String entity_name;

    /**
     * 首字母小写实体名
     */
    private String entity_name_var;

    /**
     * 表注释
     */
    private String table_comments;

    /**
     * 包名
     */
    private String package_name;

    /**
     * 是否开启 swagger
     */
    private Boolean open_swagger;


    /**
     * 是否开启 驼峰命名
     */
    private Boolean open_hump;

    /**
     * 表列信息
     */
    private List<ColumnInfo> columns = new ArrayList<>();

    /**
     * 类型注解
     */
    private Map<String, String> typeAnnotationtype = new LinkedHashMap<String, String>();

    /**
     * 数据库类型映射到 Java K 数据库, V JAVA
     */
    private Map<String, String> typeMapping = new HashMap<String, String>();

    /**
     * 数据库类型映射到 JDBC K 数据库, V JAVA
     */
    private Map<String, String> typeMappingJdbc = new HashMap<String, String>();

    /**
     * 生成代码
     * @throws IOException
     * @throws SQLException
     */
    public void gen(String cfg, String basePath) throws IOException, SQLException, TemplateException {
        this.cfg_file = cfg;
        this.basePath = basePath;

        iniCfg();

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setUsername(userName);
        hikariConfig.setPassword(password);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        HikariDataSource ds = new HikariDataSource(hikariConfig);
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;

        // 创建connection
        conn = ds.getConnection();
        statement = conn.createStatement();

        // 表注释
        PreparedStatement ps = conn.prepareStatement(SQL_TAB_COMMENTS);
        ps.setString(1, StringUtils.upperCase(db_table_name));
        rs = ps.executeQuery();
        if (rs.next()){
            table_comments = rs.getString("COMMENTS");
            db_table_name = rs.getString("TABLE_NAME");
            entity_name = StringTools.subPrefix(table_prefix, db_table_name);
            entity_name_var = StringTools.toHump(entity_name, false);
            entity_name = StringTools.toHump(entity_name, true);
        }

        // 获得表主键
        ps = conn.prepareStatement(SQL_CONSTRAINT);
        ps.setString(1, StringUtils.upperCase(db_table_name));
        rs = ps.executeQuery();
        if (rs.next()){
            db_id_name = rs.getString("COLUMN_NAME");
        }

        //  表包含的列
        ps = conn.prepareStatement(SQL_TAB_COLUMNS);
        ps.setString(1, StringUtils.upperCase(db_table_name));
        rs = ps.executeQuery();
        while (rs.next()) {
            ColumnInfo ci = new ColumnInfo();
            // 列名
            ci.setDb_name(rs.getString("COLUMN_NAME"));
            // 列类型
            ci.setDb_type(rs.getString("DATA_TYPE"));
            // 属性名，使用驼峰
            if (open_hump == null || open_hump == true)
                ci.setName(StringTools.toHump(ci.getDb_name(), false));
            else
                ci.setName(StringUtils.lowerCase(ci.getDb_name()));
            // 属性类型
            ci.setType(mapDbToJava(ci.getDb_type()));
            // 属性首字母大写
            ci.setU_name(StringTools.firstUpper(ci.getName()));
            // 主键
            if (ci.getDb_name().equals(db_id_name)) {
                id_name = ci.getName();
                ci.setDb_key(true);
                id_type = ci.getType();
                id_u_name = StringTools.firstUpper(id_name);

            }

            // jdbc 类型映射
            ci.setJdbc_type(typeMappingJdbc.get(ci.getDb_type()));

            // 添加注解
            if (typeAnnotationtype.containsKey(ci.getType()))
                ci.setAnnotation(typeAnnotationtype.get(ci.getType()));

            columns.add(ci);
        }

        // 列注释
        ps = conn.prepareStatement(SQL_COL_COMMENTS);
        ps.setString(1, StringUtils.upperCase(db_table_name));
        rs = ps.executeQuery();
        while (rs.next()) {
            String  columnName = rs.getString("COLUMN_NAME");
            String  comments = rs.getString("COMMENTS");
            for(int n = 0; n < columns.size(); n++) {
                ColumnInfo ci = columns.get(n);
                if (ci.getDb_name().endsWith(columnName) )
                    ci.setDb_comments(comments);
                if (columnName.equals(db_id_name))
                    id_comments = comments;
            }

        }

        //关闭connection
        conn.close();

        genHandle();

    }


    /**
     * 初始化配置
     * @throws IOException
     */
    private void iniCfg() throws IOException {

        Wini ini = new Wini(new File(cfg_file));
        url = ini.get("cfg", "datasource.url", String.class);
        driverClassName = ini.get("cfg", "datasource.driver-class-name", String.class);
        userName = ini.get("cfg", "datasource.userName", String.class);
        password = ini.get("cfg", "datasource.password", String.class);
        db_table_name = ini.get("cfg", "table_name", String.class);
        package_name = ini.get("cfg", "package", String.class);
        table_prefix = ini.get("cfg", "table_prefix", String.class);
        open_swagger = ini.get("cfg", "open_swagger", Boolean.class);
        open_hump = ini.get("cfg", "open_hump", Boolean.class);

        // 类型注解
        getSectionKeys(ini, "type_annotation", typeAnnotationtype);
        // 数据库类型映射到 java 类型
        getSectionKeys(ini, "dbtype_to_java", typeMapping);
        // 数据库类型映射到 jdbc 类型
        getSectionKeys(ini, "dbtype_to_jdbc", typeMappingJdbc);
    }

    /**
     * 获取配置 section 所有key
     */
    private void getSectionKeys(Wini ini, String sectionName, Map<String, String> map) {
        List<Profile.Section> listTypeMappingJdbc = ini.getAll(sectionName);
        for (int i = 0; i < listTypeMappingJdbc.size(); i++) {
            Profile.Section section = listTypeMappingJdbc.get(i);
            for (String key : section.keySet()) {
                map.put(key, section.get(key));
            }
        }
    }


    /**
     * 数据库类型映射到 JAVA 代码
     * @param dbName
     * @return
     */
    private String mapDbToJava(String dbName) {
        if (typeMapping.containsKey(dbName)) {
            return typeMapping.get(dbName);
        }
        return dbName;
    }

    /**
     * 处理生成
     * @throws IOException
     */
    private void genHandle() throws IOException, TemplateException {
        Map<String, Object> dataModel = new HashMap<>();

        dataModel.put("package", this.package_name);
        dataModel.put("table_comments", this.table_comments);
        dataModel.put("db_table_name", this.db_table_name);
        dataModel.put("entity_name", this.entity_name);
        dataModel.put("columns", this.columns);
        dataModel.put("open_swagger", this.open_swagger);
        dataModel.put("entity_name_var", this.entity_name_var);
        dataModel.put("id_type", this.id_type);
        dataModel.put("id_name", this.id_name);
        dataModel.put("id_u_name", this.id_u_name);
        dataModel.put("id_comments", this.id_comments);
        dataModel.put("db_id_name", this.db_id_name);

        FileUtils.mkdir(basePath + "entity");
        FileUtils.mkdir(basePath + "controller");
        FileUtils.mkdir(basePath + "mapper");
        FileUtils.mkdir(basePath + "service");
        FileUtils.mkdir(basePath + "service/impl");
        FileUtils.mkdir(basePath + "mybatis-mapper");
        FileUtils.mkdir(basePath + "vue");
        FileUtils.mkdir(basePath + "vue/" + entity_name);

        procFreeMakeer(basePath + "templates", "entity.ftl", dataModel
                ,basePath + "entity/" + entity_name + ".java");

        procFreeMakeer(basePath + "templates", "controller.ftl", dataModel
                ,basePath + "controller/" + entity_name + "Controller.java");

        procFreeMakeer(basePath + "templates", "mapper.ftl", dataModel
                ,basePath + "mapper/" + entity_name + "Mapper.java");

        procFreeMakeer(basePath + "templates", "service.ftl", dataModel
                ,basePath + "service/" + entity_name + "Service.java");

        procFreeMakeer(basePath + "templates", "serviceImpl.ftl", dataModel
                ,basePath + "service/impl/" + entity_name + "ServiceImpl.java");

        procFreeMakeer(basePath + "templates", "mybatis.ftl", dataModel
                ,basePath + "mybatis-mapper/" + entity_name + "Mapper.xml");
//
//        procFreeMakeer(basePath + "templates", "vue.ftl", dataModel
//                ,basePath + "mybatis-mapper/" + "vue/view/" + entity_name + "ndex.vue");


    }

    private void procFreeMakeer(String templatePath, String templateName, Map<String, Object> dataModel,
                                String outFilename) throws IOException, TemplateException {
        Configuration cfg = new Configuration();
        cfg.setClassicCompatible(true);
        FileTemplateLoader ftl = new FileTemplateLoader(new File(templatePath));
        cfg.setTemplateLoader(ftl);

        Template template = cfg.getTemplate(templateName);
        template.process(dataModel, new FileWriter(new File(outFilename)));

        // 将数据打印到控制台的
        // template.process(dataModel,new PrintWriter(System.out));
    }




}
