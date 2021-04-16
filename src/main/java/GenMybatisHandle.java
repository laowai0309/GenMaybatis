import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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
     * 表列信息
     */
    public List<ColumnInfo> columns = new ArrayList<>();

    /**
     * 类型注解
     */
    public Map<String, String> typeAnnotationtype = new LinkedHashMap<String, String>();

    /**
     * 数据库类型映射到 Java K 数据库, V JAVA
     */
    public Map<String, String> typeMapping = new HashMap<String, String>();

    /**
     * 数据库类型映射到 JDBC K 数据库, V JAVA
     */
    public Map<String, String> typeMappingJdbc = new HashMap<String, String>();

    /**
     * 生成代码
     * @throws IOException
     * @throws SQLException
     */
    public void gen(String cfg, String basePath) throws IOException, SQLException {
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
        String sql = "SELECT TABLE_NAME, COMMENTS FROM USER_TAB_COMMENTS WHERE TABLE_NAME = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
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
        sql = "SELECT B.COLUMN_NAME FROM USER_CONSTRAINTS A, USER_CONS_COLUMNS B " +
            "WHERE A.CONSTRAINT_NAME = B.CONSTRAINT_NAME AND A.CONSTRAINT_TYPE = 'P' AND B.TABLE_NAME= ? ";
        ps = conn.prepareStatement(sql);
        ps.setString(1, StringUtils.upperCase(db_table_name));
        rs = ps.executeQuery();
        if (rs.next()){
            db_id_name = rs.getString("COLUMN_NAME");
        }

        //  表包含的列
        sql = "SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE FROM USER_TAB_COLUMNS WHERE TABLE_NAME = ?";
        ps = conn.prepareStatement(sql);
        ps.setString(1, StringUtils.upperCase(db_table_name));
        rs = ps.executeQuery();
        while (rs.next()) {
            ColumnInfo ci = new ColumnInfo();
            ci.setDb_name(rs.getString("COLUMN_NAME"));
            ci.setDb_type(rs.getString("DATA_TYPE"));
            // 使用驼峰
            ci.setName(StringTools.toHump(ci.getDb_name(), false));
            // 不使用驼峰
            // ci.setName(StringUtils.lowerCase(ci.getDb_name()));
            ci.setType(mapDbToJava(ci.getDb_type()));
            ci.setU_name(StringTools.firstUpper(ci.getName()));
            if (ci.getDb_name().equals(db_id_name)) {
                id_name = ci.name;
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
        sql = "SELECT TABLE_NAME, COLUMN_NAME, COMMENTS FROM USER_COL_COMMENTS WHERE TABLE_NAME = ?";
        ps = conn.prepareStatement(sql);
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

        List<Profile.Section> list = ini.getAll("type_annotation");
        for (int i = 0; i < list.size(); i++) {
            Profile.Section section = list.get(i);
            for (String key : section.keySet()) {
                typeAnnotationtype.put(key, section.get(key));
            }
        }

        // 数据库类型映射到 java 类型
        List<Profile.Section> listTypeMappingJava = ini.getAll("dbtype_to_java");
        for (int i = 0; i < listTypeMappingJava.size(); i++) {
            Profile.Section section = listTypeMappingJava.get(i);
            for (String key : section.keySet()) {
                typeMapping.put(key, section.get(key));
            }
        }

        // 数据库类型映射到 jdbc 类型
        List<Profile.Section> listTypeMappingJdbc = ini.getAll("dbtype_to_jdbc");
        for (int i = 0; i < listTypeMappingJdbc.size(); i++) {
            Profile.Section section = listTypeMappingJdbc.get(i);
            for (String key : section.keySet()) {
                typeMappingJdbc.put(key, section.get(key));
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
    private void genHandle() throws IOException {
        VelocityContext context = new VelocityContext();

        context.put("package", this.package_name);
        context.put("table_comments", this.table_comments);
        context.put("db_table_name", this.db_table_name);
        context.put("entity_name", this.entity_name);
        context.put("columns", this.columns);
        context.put("open_swagger", this.open_swagger);
        context.put("entity_name_var", this.entity_name_var);
        context.put("id_type", this.id_type);
        context.put("id_name", this.id_name);
        context.put("id_u_name", this.id_u_name);
        context.put("id_comments", this.id_comments);
        context.put("db_id_name", this.db_id_name);

        File file = new File(basePath + "entity");
        if(!file.exists()){
            file.mkdir();
        }

        file = new File(basePath + "controller");
        if(!file.exists()){
            file.mkdir();
        }

        file = new File(basePath + "service");
        if(!file.exists()){
            file.mkdir();
        }

        file = new File(basePath + "service/impl");
        if(!file.exists()){
            file.mkdir();
        }

        file = new File(basePath + "mapper");
        if(!file.exists()){
            file.mkdir();
        }

        file = new File(basePath + "mybatis-mapper");
        if(!file.exists()){
            file.mkdir();
        }

        writeVecocity(context, "templates/entity.vm", basePath + "entity/" + entity_name + ".java");
        writeVecocity(context, "templates/controller.vm", basePath + "controller/" + entity_name + "Controller.java");
        writeVecocity(context, "templates/mapper.vm", basePath + "mapper/" + entity_name + "Mapper.java");
        writeVecocity(context, "templates/service.vm", basePath + "service/" + entity_name + "Service.java");
        writeVecocity(context, "templates/serviceImpl.vm", basePath + "service/impl/" + entity_name + "ServiceImpl.java");
        writeVecocity(context, "templates/mybatis.vm", basePath + "mybatis-mapper/" + entity_name_var + "Mapper.xml");
    }


    public void writeVecocity(VelocityContext context, String templateName, String outFileName) throws IOException {
        Properties properties=new Properties();
        properties.setProperty("resource.loader", "class");
        properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        properties.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
        properties.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        properties.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");

        VelocityEngine velocityEngine = new VelocityEngine(properties);
        StringWriter sw = new StringWriter();
        velocityEngine.mergeTemplate(templateName, "utf-8", context, sw);
        IOUtils.write(sw.toString(), new FileOutputStream(outFileName));

    }



}
