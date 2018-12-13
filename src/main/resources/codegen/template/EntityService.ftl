<#assign daoName = table.className?uncap_first + "Mapper" />
package ${basePackageName}.service;

import ${basePackageName}.model.${table.className};
import ${basePackageName}.mapper.${table.className}Mapper;
import com.learnyeai.learnai.support.BaseMapper;
import com.learnyeai.common.support.WeyeBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 * @author ${author}
 */
@Service
public class ${table.className}WyService extends WeyeBaseService<${table.className}> {

    @Resource
    private ${table.className}Mapper ${daoName};

    @Override
    public BaseMapper<${table.className}> getMapper() {
        return ${daoName};
    }
    @Override
    protected boolean isLogicDelete(){
<#assign del = 0/>
<#list table.fieldList as field>
    <#if field.collumnName = "DEL_FLAG"><#assign del = 1/></#if>
</#list>
<#if del = 1>
        return true;
<#else>
        return false;
</#if>
    }
}
