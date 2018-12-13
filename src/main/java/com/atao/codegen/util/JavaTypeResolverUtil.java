package com.atao.codegen.util;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.type.JdbcType;

import com.atao.codegen.vo.TableField;

/**
 * Sql类型解析工具类
 * @author 李超（lc3）
 */
public class JavaTypeResolverUtil {

    private static final Map<JdbcType, Class> typeMap = new HashMap<JdbcType, Class>();

    static {
        typeMap.put(JdbcType.ARRAY, Object.class);
        typeMap.put(JdbcType.BIGINT, Long.class);
        typeMap.put(JdbcType.BIT, Boolean.class);
        typeMap.put(JdbcType.BOOLEAN, Boolean.class);
        typeMap.put(JdbcType.CHAR, String.class);
        typeMap.put(JdbcType.CLOB, String.class);
        typeMap.put(JdbcType.DATE, Date.class);
        typeMap.put(JdbcType.DOUBLE, Double.class);
        typeMap.put(JdbcType.FLOAT, Double.class);
        typeMap.put(JdbcType.INTEGER, Integer.class);
        typeMap.put(JdbcType.LONGVARCHAR, String.class);
        typeMap.put(JdbcType.NULL, Object.class);
        typeMap.put(JdbcType.OTHER, Object.class);
        typeMap.put(JdbcType.REAL, Float.class);
        typeMap.put(JdbcType.SMALLINT, Short.class);
        typeMap.put(JdbcType.STRUCT, Object.class);
        typeMap.put(JdbcType.TIME, Date.class);
        typeMap.put(JdbcType.TIMESTAMP, Date.class);
        typeMap.put(JdbcType.TINYINT, Byte.class);
        typeMap.put(JdbcType.VARCHAR, String.class);
    }

    /**
     * 计算JDBC对应的Java类型
     */
    public static Class calculateJavaType(TableField field) {
        Integer type = field.getJdbcType();
        Class javaType = typeMap.get(JdbcType.forCode(type));
        if (null == javaType) {
            switch (type) {
                case Types.DECIMAL:
                case Types.NUMERIC:
                    if (field.getScale() > 0 || field.getLength() > 9) {
                        javaType = BigDecimal.class;
                    } else if (field.getLength() > 4) {
                        javaType = Long.class;
                    } else if (field.getLength() > 2) {
                        javaType = Integer.class;
                    } else {
                        javaType = Short.class;
                    }
                    break;
                default:
                    javaType = null;
                    break;
            }
        }
        return null == javaType ? Object.class : javaType;
    }
}
