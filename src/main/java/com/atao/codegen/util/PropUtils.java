package com.atao.codegen.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.beanutils.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.Assert;

import com.atao.util.StringUtils;

/**
 * Created by zpz on 2018/4/8.
 */
public class PropUtils {

    private static final Logger logger = LoggerFactory.getLogger(PropUtils.class);
    private static final Properties prop = genConfigProperties();

    private static Properties genConfigProperties() {
        try {
            return PropertiesLoaderUtils.loadAllProperties("application-dev.properties");
        } catch (IOException e) {
            logger.error("加载数据库配置文件失败失败", e);
            throw new RuntimeException(e);
        }
    }
    public static <T> T getValue(String key, Class<T> classType, T defaultVal) {
        Assert.notNull(key, "key不能为空");
        String strVal = prop.getProperty(key);
        return null != strVal && !strVal.isEmpty()?(null == classType?(T)strVal:(classType.isAssignableFrom(strVal.getClass())?(T)strVal: (T)ConvertUtils.convert(strVal, classType))):defaultVal;
    }

    public static <T> T getValue(String key, T defaultVal) {
        return getValue(key, (Class<T>)defaultVal.getClass(), defaultVal);
    }

    public static String getValue(String key, String defaultVal) {
        return (String)getValue(key, String.class, defaultVal);
    }

    public static String getValue(String key) {
        return getValue(key, (String)null);
    }


    public static Set<String> keySetStartWith(String prefix) {
        if(StringUtils.isBlank(prefix)) {
            prefix = null;
        }

        LinkedHashSet keys = new LinkedHashSet();
        Iterator var2 = prop.keySet().iterator();

        while(true) {
            Object key;
            do {
                do {
                    if(!var2.hasNext()) {
                        return keys;
                    }

                    key = var2.next();
                } while(!(key instanceof String));
            } while(null != prefix && !((String)key).startsWith(prefix));

            keys.add((String)key);
        }
    }

}
