<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<!-- ${table_comments}(${db_table_name}) -->
<mapper namespace="${package}.mapper.${entity_name}Mapper">
    <!-- 字段映射 -->
    <resultMap id="${entity_name_var}Map" type="${package}.entity.${entity_name}">
        <#list columns as ci>
        <#if ci.key>
        <id column="${ci.db_name}" property="${ci.name}" jdbcType="${ci.jdbc_type}" />
        <#else>
        <result column="${ci.db_name}" property="${ci.name}" jdbcType="${ci.jdbc_type}" />
        </#if>
        </#list>
    </resultMap>

    <!-- 表查询字段 -->
    <sql id="allColumns">
        <#list columns as ci>
        <#if ci_has_next>
        t.${ci.db_name},
        <#else>
        t.${ci.db_name}
        </#if>
        </#list>
    </sql>

    <!-- 根据主键查询 -->
    <select id="get" resultMap="${entity_name_var}Map" parameterType="${id_type}">
        SELECT
        <include refid="allColumns" />
        FROM ${db_table_name} t WHERE t.${db_id_name} = ${'#'}{${id_name}}
    </select>

    <!-- 分页查询 -->
    <select id="list" resultMap="${entity_name_var}Map" parameterType="${package}.entity.${entity_name}">
        SELECT
        <include refid="allColumns" />
        FROM ${db_table_name} t WHERE 1 = 1
        <#list columns as ci>
        <if test="${ci.name} != null">
        <#if ci.type  == "String">
            AND ${ci.db_name} LIKE CONCAT(CONCAT('%', ${'#'}{${ci.name}, jdbcType=${ci.jdbc_type}}), '%')
        <#else>
            AND ${ci.db_name} = ${'#'}{${ci.name}, jdbcType=${ci.jdbc_type}}
        </#if>
        </if>
        </#list>
        <!--
        <if test="beginTime != null">
            AND CREATE_TIME <![CDATA[ >= ]]> to_date(${'#'}{beginTime, jdbcType=VARCHAR}, 'YYYY-MM-DD')
        </if>
        <if test="endTime != null">
            AND CREATE_TIME <![CDATA[ <= ]]> to_date(${'#'}{endTime, jdbcType=VARCHAR}, 'YYYY-MM-DD')
        </if>
        -->

    </select>

    <!-- 新增 -->
    <insert id="save">
        <selectKey resultType="int" keyProperty="${id_name}" order="BEFORE">
            SELECT ${db_table_name}_S.NEXTVAL FROM DUAL
        </selectKey>
        INSERT INTO ${db_table_name} (
            <#list columns as ci>
            <#if ci_has_next>
            ${ci.db_name},
            <#else >
            ${ci.db_name}
            </#if>
            </#list>
        ) VALUES (
        <#list columns as ci>
        <#if ci_has_next>
            ${'#'}{${ci.name}, jdbcType=${ci.jdbc_type}},
        <#else>
            ${'#'}{${ci.name}, jdbcType=${ci.jdbc_type}}
        </#if>
        </#list>
        )
    </insert>

    <!-- 修改字典类型表信息 -->
    <update id="update">
        UPDATE  ${db_table_name}
        <set>
        <#list columns as ci>
        <if test="${ci.name} != null">
            ${ci.db_name} = ${'#'}{${ci.name},jdbcType=${ci.jdbc_type}},
        </if>
        </#list>
        </set>
        WHERE ${db_id_name} = ${'#'}{${id_name}}
    </update>

    <!-- 根据主键删除字典类型表 -->
    <delete id="del" parameterType="${id_type}">
        DELETE FROM ${db_table_name} WHERE ${db_id_name} = ${'#'}{${id_name}}
    </delete>

</mapper>