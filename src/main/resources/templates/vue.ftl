<!-- ${table_comments} -->

<template>
    <div class="app-container">
        <el-row :gutter="20">
            <!-- 搜索区域 -->
            <el-col :span="20" :xs="24">
                <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
                    <#list columns as ci>
                    <#if ci.type == "Date">
                    <el-form-item label="${ci.db_comments}">
                        <el-date-picker
                                v-model="dateRange"
                                size="small"
                                style="width: 240px"
                                value-format="yyyy-MM-dd"
                                type="daterange"
                                range-separator="-"
                                start-placeholder="开始日期"
                                end-placeholder="结束日期"
                        ></el-date-picker>
                    </el-form-item>
                    <#else>
                    <el-form-item label="${ci.db_comments}" prop="${ci.name}">
                        <el-input
                                v-model="queryParams.${ci.name}"
                                placeholder="请输入${ci.db_comments}"
                                clearable
                                size="small"
                                style="width: 240px"
                                @keyup.enter.native="handleQuery"
                        />
                    </el-form-item>
                    </#if>
                    </#list>
                    <el-form-item>
                        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
                        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
                    </el-form-item>
                </el-form>

                <!-- 增删改查按钮区域 -->
                <el-row :gutter="10" class="mb8">
                    <el-col :span="1.5">
                        <el-button
                                type="primary"
                                plain
                                icon="el-icon-plus"
                                size="mini"
                                @click="handleAdd"
                                v-hasPermi="['${permi_prefix}:add']"
                        >新增</el-button>
                    </el-col>
                    <el-col :span="1.5">
                        <el-button
                                type="success"
                                plain
                                icon="el-icon-edit"
                                size="mini"
                                :disabled="single"
                                @click="handleUpdate"
                                v-hasPermi="['${permi_prefix}:edit']"
                        >修改</el-button>
                    </el-col>
                    <el-col :span="1.5">
                        <el-button
                                type="danger"
                                plain
                                icon="el-icon-delete"
                                size="mini"
                                :disabled="multiple"
                                @click="handleDelete"
                                v-hasPermi="['${permi_prefix}:remove']"
                        >删除</el-button>
                    </el-col>
                    <el-col :span="1.5">
                        <el-button
                                type="info"
                                plain
                                icon="el-icon-upload2"
                                size="mini"
                                @click="handleImport"
                                v-hasPermi="['${permi_prefix}:import']"
                        >导入</el-button>
                    </el-col>
                    <el-col :span="1.5">
                        <el-button
                                type="warning"
                                plain
                                icon="el-icon-download"
                                size="mini"
                                @click="handleExport"
                                v-hasPermi="['${permi_prefix}:export']"
                        >导出</el-button>
                    </el-col>
                    <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
                </el-row>

                <el-table v-loading="loading" :data="${ci.name}List" @selection-change="handleSelectionChange">
                    <el-table-column type="selection" width="50" align="center" />
                    <#list columns as ci>
                    <el-table-column label="${ci.db_comments}" align="center" key="${ci.name}" prop="${ci.name}" />
                    </#list>

                    <el-table-column
                            label="操作"
                            align="center"
                            width="160"
                            class-name="small-padding fixed-width"
                    >
                        <template slot-scope="scope">
                            <el-button
                                    size="mini"
                                    type="text"
                                    icon="el-icon-edit"
                                    @click="handleUpdate(scope.row)"
                                    v-hasPermi="['${permi_prefix}:edit']"
                         >修改</el-button>
                             <el-button
                                     v-if="scope.row.${id_name} !== 1"
                                     size="mini"
                                     type="text"
                                     icon="el-icon-delete"
                                     @click="handleDelete(scope.row)"
                                     v-hasPermi="['${permi_prefix}:remove']"
                             >删除</el-button>
                         </template>
                    </el-table-column>
                </el-table>

                 <pagination
                         v-show="total>0"
                         :total="total"
                         :page.sync="queryParams.pageNum"
                         :limit.sync="queryParams.pageSize"
                         @pagination="getList"
                         />
             </el-col>
         </el-row>

 <!-- 添加或修改参数配置对话框 -->
        <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
            <el-form ref="form" :model="form" :rules="rules" label-width="80px">
                <#list columns as ci>
                    <#if ci_index % 2 == 0>
                    <el-row>
                    </#if>
                        <el-col :span="12">
                            <el-form-item label="${ci.db_comments}" prop="nickName">
                                <el-input v-model="form.${ci.name}" placeholder="请输入${ci.db_comments}" />
                            </el-form-item>
                        </el-col>
                    <#if ci_index % 2 == 1 || !ci_has_next>
                    </el-row>
                    </#if>

                </#list>

            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="submitForm">确 定</el-button>
                <el-button @click="cancel">取 消</el-button>
            </div>
        </el-dialog>

    </div>
</template>


