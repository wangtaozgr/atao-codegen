package com.atao.codegen.vo;

import org.apache.ibatis.type.JdbcType;

import com.atao.codegen.util.JavaTypeResolverUtil;
import com.atao.util.StringUtils;

/**
 * 表字段对应的代码生成辅助类.
 * 
 * @author lc3@yitong.com.cn
 */
public class TableField {

	private String name;
	private String methodName;
	private String collumnName;
	private Integer jdbcType;
	private int length;
	private int scale;
	private boolean nullable;
	private String remarks;

	public String getName() {
		return name;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getCollumnName() {
		return collumnName;
	}

	public void setCollumnName(String collumnName) {
		this.collumnName = collumnName;
		this.methodName = StringUtils.capitalizeAll(collumnName);
		this.name = StringUtils.uncapitalize(this.methodName);
	}

	public Integer getJdbcType() {
		return jdbcType;
	}

	public void setJdbcType(Integer jdbcType) {
		this.jdbcType = jdbcType;
	}

	public String getJdbcTypeName() {
		if (null == jdbcType) {
			return null;
		} else {
			JdbcType type = JdbcType.forCode(jdbcType);
			if (null == type) {
				type = JdbcType.UNDEFINED;
			}
			return type.name();
		}
	}

	public Class getJavaType() {
		return JavaTypeResolverUtil.calculateJavaType(this);
	}

	public String getJavaTypeName() {
		Class jdbcType = getJavaType();
		return null == jdbcType ? null : jdbcType.getSimpleName();
	}

	public String getJdbc2JavaName() {
		Class jdbcType = getJavaType();

		return null == jdbcType ? null : jdbcType.getName();
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public String getRemarks() {
		return StringUtils.toString(remarks, "");
	}

	public void setRemarks(String remarks) {
		if (null != remarks) {
			remarks = remarks.replaceAll("\\n|\\r", "  ");
		}
		this.remarks = remarks;
	}

	@Override
	public String toString() {
		return "TableField{" + "name='" + name + '\'' + ", methodName='" + methodName + '\'' + ", collumnName='"
				+ collumnName + '\'' + ", jdbcType=" + jdbcType + ", javaType=" + getJavaTypeName() + ", length="
				+ length + ", scale=" + scale + ", nullable=" + nullable + ", remarks='" + remarks + '\'' + '}';
	}
}
