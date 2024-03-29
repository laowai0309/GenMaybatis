package ${package}.mapper;


/**
* ${table_comments} (${db_table_name})
*/
@Mapper
public interface ${entity_name}Mapper {
    ${entity_name} get(${id_type} ${id_name});

    List<${entity_name}> list(${entity_name} ${entity_name_var});

    int save(${entity_name} ${entity_name_var});


    int update(${entity_name} ${entity_name_var});

    int del(${id_type} ${id_name});
}
