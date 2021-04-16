# GenMaybatis

## 说明

根据数据库表通过velocity模板，生成 spring boot + mybatis 单表增删改查。

## 配置说明

### 参考配置文件
```
[cfg]
datasource.url=jdbc:oracle:thin:@127.0.0.1:1521:orcl
datasource.driver-class-name=oracle.jdbc.OracleDriver
datasource.userName=username
datasource.password=123456

table_name=NFSYS_ORG
package=com.xxx.admin.system
open_swagger=true
table_prefix=nf

[type_annotation]
Date=@JsonFormat (pattern = "yyyy-MM-dd HH:mm:ss")

[dbtype_to_java]
NUMBER=Integer
VARCHAR2=String
DATE=Date

[dbtype_to_jdbc]
NUMBER=INTEGER
VARCHAR2=VARCHAR
VARCHAR=VARCHAR
DATE=TIMESTAMP
```

#### 配置说明

* [cfg]  
主配置。
datasource.url：数据库地址  
datasource.driver-class-name：驱动名称
datasource.userName：数据库用户名称
datasource.password：数据库密码

table_name：需要生成的表
package：包名
open_swagger：是否生成 swagger 注解
table_prefix：表前前缀

* [type_annotation]  
生成的实体类型属性，需不需要加上的注解。    
如: Date=@JsonFormat (pattern = "yyyy-MM-dd HH:mm:ss")，白色 Date 类型加上注解 “=@JsonFormat (pattern = "yyyy-MM-dd HH:mm:ss")”
```
    @ApiModelProperty(value = "创建时间")
    @JsonFormat (pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
```

* [dbtype_to_java]
数据库表字段类型到 Java 类型的映射。  
如: NUMBER=Integer，表示数据库表字段类型为 NUMBER ，java 为 "Integer" 
```
    private Integer orgId;
```

* [dbtype_to_jdbc]  

数据库表字段类型到 JDBC 类型的映射。     
如: NUMBER=INTEGER，表示数据库表字段类型为 NUMBER ，jdbcType 为 "INTEGER" 
```
...
    <resultMap id="sysOrgMap" type="com.xxx.admin.system.entity.SysOrg">
        <result column="ORG_ID" property="orgId" jdbcType="INTEGER" />
        <result column="ORG_CODE" property="orgCode" jdbcType="VARCHAR" />
...
```

### 模板说明
模板在resources\templates目录下：
* controller.vm 生成 controller 模板
* entity.vm  生成 entity 模板
* mapper.vm 生成 mapper 模板
* mybatis.vm 生成 mybatis xml 模板
* service.vm 生成 service 模板
* serviceImpl.vm 生成 serviceImpl 模板

#### 其它说明
  目前仅支持 oracle