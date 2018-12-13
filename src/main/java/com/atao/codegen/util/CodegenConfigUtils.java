package com.atao.codegen.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import com.atao.util.DateUtils;

/**
 * 代码生成配置管理工具类
 * 
 * @author 李超（lc3@yitong.com.cn）
 */
public class CodegenConfigUtils {

	private static Logger logger = LoggerFactory.getLogger(CodegenConfigUtils.class);

	private static final String[] NOT_NULL_PROPS = new String[] { "author", "basePackageName", "tablePattern" };

	private static Properties props = genConfigProperties();

	private CodegenConfigUtils() {
	}

	/**
	 * 读取并编译配置文件
	 * 
	 * @return
	 */
	private static Properties genConfigProperties() {
		try {
			Properties sysProps = new Properties(System.getProperties());
			PropertiesLoaderUtils.fillProperties(sysProps,
					new EncodedResource(new ClassPathResource("codegen/codegen_config.properties"), "UTF-8"));
			checkConfig(sysProps);
			compileConfig(sysProps);
			return sysProps;
		} catch (IOException e) {
			logger.error("加载数据库配置文件失败失败", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 校验参数配置
	 * 
	 * @param props
	 */
	private static void checkConfig(Properties props) {
		for (String notNullProp : NOT_NULL_PROPS) {
			if (!StringUtils.hasText(props.getProperty(notNullProp))) {
				logger.error("缺少关键系数配置：[{}]", notNullProp);
			}
		}
	}

	/**
	 * 编译配置文件，解决路径，增加自动变量
	 * 
	 * @param props
	 */
	private static void compileConfig(Properties props) {
		props.setProperty("dateStr", DateUtils.formatDate(new Date(), "yyyy-mm-dd"));
		String moduleName = props.getProperty("moduleName");
		if (StringUtils.hasText(moduleName)) {
			props.setProperty("basePackageName", props.getProperty("basePackageName") + "." + moduleName);
			// props.setProperty("sqlmapperPath", props.getProperty("sqlmapperPath") + "." +
			// moduleName);
			// props.setProperty("sqlmapperDB2Path", props.getProperty("sqlmapperDB2Path") +
			// "." + moduleName);
			props.setProperty("sqlmapperPath", props.getProperty("sqlmapperPath"));
			props.setProperty("sqlmapperDB2Path", props.getProperty("sqlmapperDB2Path"));
		}
	}

	/**
	 * 得到工程目录
	 * 
	 * @return
	 */
	public static File getProjectPath() {
		String projectPathFile = "projectPathFile";
		File file = (File) props.get(projectPathFile);
		if (null != file) {
			return file;
		}
		String path = props.getProperty("projectPath");
		try {
			if (!StringUtils.hasText(path)) {
				file = new DefaultResourceLoader().getResource("").getFile();
				// 从Class目录查找源码目录
				while (null != file) {
					String name = file.getName();
					file = file.getParentFile();
					if ("target".equals(name) || "src".equals(name)) {
						break;
					}
				}
			} else {
				file = ResourceUtils.getFile(path);
			}

			file = new File(file, "src/main/java");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Assert.notNull(file, "不能自动定位到源码目录，请手工设置工程源码目录位置：projectPath");
		props.put(projectPathFile, file);
		return file;
	}

	/**
	 * class名转文件路径格式
	 * 
	 * @param path
	 * @return
	 */
	private static String replacePath(String path) {
		return StringUtils.replace(path, ".", "/");
	}

	/**
	 * 得到模板位置
	 * 
	 * @return
	 */
	public static File getTplPath() {
		File file = new File(getProjectPath() + "/../resources", replacePath("codegen.template"));
		return file;
	}

	/**
	 * 获得Java基本路径
	 * 
	 * @return
	 */
	public static File getJavaBasePath() {
		String tplPath = props.getProperty("basePackageName");
		return new File(getProjectPath(), replacePath(tplPath));
	}

	/**
	 * 获得Sql Mapper基本路径
	 * 
	 * @return
	 */
	public static File getMapperBasePath() {
		String tplPath = props.getProperty("sqlmapperPath");
		return new File(getProjectPath() + "/../resources", replacePath(tplPath));
	}

	/**
	 * 获得Sql Mapper基本路径
	 * 
	 * @return
	 */
	public static File getMapperDB2Path() {
		String tplPath = props.getProperty("sqlmapperDB2Path");
		return new File(getProjectPath() + "/../resources", replacePath(tplPath));
	}

	/**
	 * 获得页面基本路径
	 * 
	 * @return
	 */
	public static File getViewBasePath() {
		String tplPath = props.getProperty("moduleName");
		return new File(getProjectPath() + "/../webapp/WEB-INF/views/modules/", replacePath(tplPath));
	}

	public static File getReportBasePath() {
		return new File(getProjectPath() + "/../resources/WEB-INF/disgram");
	}

	/**
	 * 获得数据库名
	 * 
	 * @return
	 */
	public static String getTableSchema() {
		return props.getProperty("tableSchema");
	}

	/**
	 * 获得表匹配模式
	 * 
	 * @return
	 */
	public static String getTablePattern() {
		return props.getProperty("tablePattern");
	}

	/**
	 * 得到代码生成配置信息
	 * 
	 * @return
	 */
	public static Properties getConfigs() {
		return props;
	}

	public static void main(String[] args) {
		System.out.println(genConfigProperties());
	}

}
