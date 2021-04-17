package ${package}.service;


/**
* ${table_comments} (${db_table_name})
*/
public interface ${entity_name}Service {


    ReturnT<${entity_name}> get(${id_type} ${id_name});

    ReturnT<PageData<${entity_name}>> list(int pageNum, int pageSize, ${entity_name} ${entity_name_var});

    ReturnT<String> save(${entity_name} ${entity_name_var});

    ReturnT<String> update(${entity_name} ${entity_name_var});

    ReturnT<String> del(${id_type} ${id_name});

}
