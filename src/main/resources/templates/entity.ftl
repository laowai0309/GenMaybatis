package ${package}.entity;

/**
* ${table_comments} (${db_table_name})
*
*/
public class ${entity_name} implements java.io.Serializable {
    <#list columns as ci>
    /**
    * ${ci.db_comments}
    */
    <#if open_swagger>
    @ApiModelProperty(value = "${ci.db_comments}")
    </#if>
    <#if ci.annotation>
    ${ci.annotation}
    </#if>
    private ${ci.type} ${ci.name};

    </#list>

    <#list columns as ci>
    /**
    * ${ci.db_comments}
    */
    public ${ci.type} get${ci.u_name} () {
        return this.${ci.name};
    }

    /**
    * ${ci.db_comments}
    */
    public void set${ci.u_name} (${ci.type} ${ci.name}) {
        this.${ci.name} = ${ci.name};
    }
    </#list>

}
