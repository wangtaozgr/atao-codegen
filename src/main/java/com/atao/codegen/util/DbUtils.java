package com.atao.codegen.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atao.codegen.vo.TableEntity;
import com.atao.codegen.vo.TableField;
import com.atao.util.StringUtils;

/**
 * 代码生成方面的数据库工具类.
 * 
 * @author lc3@yitong.com.cn
 */
public class DbUtils {

	private static final Logger logger = LoggerFactory.getLogger(DbUtils.class);

	public static Connection getConnect() {
		try {
			Class.forName(PropUtils.getValue("spring.datasource.driver-class-name"));
			Properties tmpProp = System.getProperties();
			tmpProp.setProperty("user", PropUtils.getValue("spring.datasource.username"));
			tmpProp.setProperty("password", PropUtils.getValue("spring.datasource.password", ""));
			tmpProp.setProperty("remarksReporting", "true");
			tmpProp.setProperty("useInformationSchema", "true");
			return DriverManager.getConnection(PropUtils.getValue("spring.datasource.url"), tmpProp);
		} catch (Exception e) {
			logger.error("获取和数据库的连接失败", e);
			throw new RuntimeException("获取和数据库的连接失败", e);
		}
	}

	public static Collection<TableEntity> getTables(String schema, String tablePattern) {
		Set<TableEntity> tableCollection = new HashSet<TableEntity>();
		if (StringUtils.isBlank(tablePattern)) {
			return tableCollection;
		}
		String[] tablePatterns = tablePattern.split(",");
		ResultSet rs = null;
		try {
			DatabaseMetaData metaData = getConnect().getMetaData();
			for (String tablePatternStr : tablePatterns) {
				rs = metaData.getTables(null, schema, tablePatternStr, new String[] { "TABLE" });
				tableCollection.addAll(parseTableEntity4ResultSet(rs));
				closeResultSet(rs);
			}
			for (TableEntity entity : tableCollection) {
				rs = metaData.getColumns(null, entity.getSchema(), entity.getName(), null);
				entity.setFieldList(parseTableField4ResultSet(rs));
				closeResultSet(rs);
				rs = metaData.getPrimaryKeys(null, entity.getSchema(), entity.getName());
				while (rs.next()) {
					String columnName = rs.getString("COLUMN_NAME"); //$NON-NLS-1$
					entity.addPrimaryKeyColumn(columnName);
				}
				closeResultSet(rs);
			}
		} catch (SQLException e) {
			logger.error("获取数据库表信息失败", e);
			throw new RuntimeException("获取数据库信息失败", e);
		} finally {
			closeResultSet(rs);
		}
		return tableCollection;
	}

	private static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// ignore
			}
		}
	}

	private static List<TableField> parseTableField4ResultSet(ResultSet rs) throws SQLException {
		List<TableField> list = new ArrayList<TableField>();
		if (null == rs) {
			return null;
		}
		while (rs.next()) {
			TableField field = new TableField();
			field.setCollumnName(rs.getString("COLUMN_NAME"));
			field.setJdbcType(rs.getInt("DATA_TYPE"));
			field.setLength(rs.getInt("COLUMN_SIZE"));
			field.setNullable(rs.getInt("NULLABLE") > 0);
			field.setRemarks(rs.getString("REMARKS"));
			field.setScale(rs.getInt("DECIMAL_DIGITS"));
			list.add(field);
		}
		return list;
	}

	private static List<TableEntity> parseTableEntity4ResultSet(ResultSet rs) throws SQLException {
		List<TableEntity> list = new ArrayList<TableEntity>();
		if (null == rs) {
			return null;
		}
		while (rs.next()) {
			TableEntity table = new TableEntity();
			table.setSchema(rs.getString("TABLE_SCHEM"));
			if (null == table.getSchema()) {
				table.setSchema(rs.getString("TABLE_CAT"));
			}
			table.setName(rs.getString("TABLE_NAME").toUpperCase());
			table.setRemarks(rs.getString("REMARKS"));
			list.add(table);
		}
		return list;
	}

	public static void main(String[] args) {
		System.out.printf(getTables("easyweb", "P_MNG_USER").toString());
		System.out.printf(getTables("MMC", "P_MNG_USER").toString());
	}
}
