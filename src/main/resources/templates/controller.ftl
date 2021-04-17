package ${package}.controller;


/**
* ${table_comments} (${db_table_name}) 控制器
*/
<#if open_swagger>
@Api(value = "${entity_name}Controller ${table_comments} 控制器", tags = "${table_comments}")
</#if>
@RestController
public class ${entity_name}Controller {
    @Autowired
    private ${entity_name}Service ${entity_name_var}Service;


    /**
    * 获取${table_comments}
    * @param ${id_name} ${id_comments}
    * @return
    */
    <#if open_swagger>
    @ApiOperation(value = "获取${entity_name}", notes = "获取${entity_name}")
    </#if>
    @GetMapping(value="/${entity_name_var}/{${id_name}}")
    public ReturnT<${entity_name}> get(@PathVariable("${id_name}") ${id_type} ${id_name}) {
        return ${entity_name_var}Service.get(${id_name});
    }

    /**
    * 分页获取${table_comments}
    * @param pageNum 页号
    * @param pageSize 页大小
    * @param ${entity_name_var} 查询的实体
    * @return
    */
    <#if open_swagger>
    @ApiOperation(value = "分页获取${entity_name}", notes = "分页获取${entity_name}")
    </#if>
    @PostMapping(value="/${entity_name_var}/list")
    public ReturnT<PageData<${entity_name}>> list(
        @RequestParam(defaultValue = "0", required = false) int pageNum,
        @RequestParam(defaultValue = "0", required = false) int pageSize,
        @RequestBody(required = false) ${entity_name} ${entity_name_var}){
        return ${entity_name_var}Service.list(pageNum, pageSize, ${entity_name_var});

    }

    /**
    * 新增${table_comments}
    * @param ${entity_name_var} 新增实体
    * @return
    */
    <#if open_swagger>
    @ApiOperation(value = "新增${entity_name}", notes = "新增${entity_name}")
    </#if>
    @PostMapping(value="/${entity_name_var}")
    public ReturnT<String> save(@RequestBody ${entity_name} ${entity_name_var}) {
        return ${entity_name_var}Service.save(${entity_name_var});
    }


    /**
    * 更新${table_comments}
    * @param ${entity_name_var} 更新实体
    * @return
    */
    <#if open_swagger>
    @ApiOperation(value = "更新${entity_name}", notes = "更新${entity_name}")
    </#if>
    @PutMapping(value="/${entity_name_var}")
    public ReturnT<String> update(@RequestBody ${entity_name} ${entity_name_var}) {
        return ${entity_name_var}Service.update(${entity_name_var});
    }

    /**
    * 删除${table_comments}
    * @param ${id_name} ${id_comments}
    * @return
    */
    <#if open_swagger>
    @ApiOperation(value = "删除${entity_name}", notes = "删除${entity_name}")
    </#if>
    @DeleteMapping(value="/${entity_name_var}/{${id_name}}")
    public ReturnT<String> del(@PathVariable("${id_name}") ${id_type} ${id_name}) {
        return ${entity_name_var}Service.del(${id_name});
    }
}
