package ${package}.service.impl;


/**
* ${table_comments} (${db_table_name})
*/
@Service
public class ${entity_name}ServiceImpl implements ${entity_name}Service {
    @Resource
    private ${entity_name}Mapper ${entity_name_var}Mapper;

    /**
    * 获取${table_comments}
    * @param ${id_name} ${id_comments}
    * @return
    */
    @Override
    public ReturnT<${entity_name}> get(${id_type} ${id_name}){
        ${entity_name} ${entity_name_var} = ${entity_name_var}Mapper.get(${id_name});
        return new ReturnT<>(${entity_name_var});
    }

    /**
    * 分页获取${table_comments}
    * @param pageNum 页号
    * @param pageSize 页大小
    * @param ${entity_name_var} 查询实体
    * @return
    */
    @Override
    public ReturnT<PageData<${entity_name}>> list(int pageNum, int pageSize, ${entity_name} ${entity_name_var}){
        PageData<${entity_name}> pageData = new PageData<>();

        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
            List<${entity_name}> list = ${entity_name_var}Mapper.list(${entity_name_var});
            PageInfo<${entity_name}> pageInfo = new PageInfo<>(list);

            pageData.setPages(pageInfo.getPages());
            pageData.setPageSize(pageInfo.getPageSize());
            pageData.setPageNum(pageInfo.getPageNum());
            pageData.setTotal(pageInfo.getTotal());
            pageData.setList(list);
         } else {
            List<${entity_name}> list = ${entity_name_var}Mapper.list(${entity_name_var});
            pageData.setList(list);
            pageData.setTotal((long)list.size());
            pageData.setPages(list.size());
            pageData.setPages(0);
         }

        return new ReturnT<>(pageData);
    }

    /**
    * 新增${table_comments}
    * @param ${entity_name_var} 新增实体
    * @return
    */
    @Override
    public ReturnT<String> save(${entity_name} ${entity_name_var}){
        int res = ${entity_name_var}Mapper.save(${entity_name_var});
        if (res > 0 )
            return ReturnT.SUCCESS;
        else
            return ReturnT.FAIL;
    }

    /**
    * 更新${table_comments}
    * @param ${entity_name_var} 更新实体
    * @return
    */
    @Override
    public ReturnT<String> update(${entity_name} ${entity_name_var}){
        int res = ${entity_name_var}Mapper.update(${entity_name_var});
        if (res > 0 )
            return ReturnT.SUCCESS;
        else
            return ReturnT.FAIL;
    }

    /**
    * 删除${table_comments}
    * @param ${id_name} ${id_comments}
    * @return
    */
    @Override
    public ReturnT<String> del(${id_type} ${id_name}) {
        int res = ${entity_name_var}Mapper.del(${id_name});
        if (res > 0 )
            return ReturnT.SUCCESS;
        else
            return ReturnT.FAIL;
    }

}
