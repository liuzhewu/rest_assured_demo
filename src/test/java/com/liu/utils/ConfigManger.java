package com.liu.utils;

import com.liu.constant.Constants;

import java.io.FileInputStream;
import java.util.Properties;

import static com.liu.constant.Constants.RESOURCES;
import static com.liu.constant.Constants.SP;

/**
 * 读取配置文件，读取的文件：config.properties
 *
 * @author Administrator
 */
public class ConfigManger {
    //系统可变的配置，对应config.properties
    private static Properties config = null;

    static {
        config = new Properties();
        try {
            config.load(new FileInputStream(RESOURCES + SP + "env" + SP + "config.properties"));
        } catch (Exception e) {
            LogUtils.error(e.getMessage());
        }

    }

    public static Properties getConfig() {
        return config;
    }

    public static void main(String[] args) {
        System.out.println("URL:" + getConfig().getProperty(Constants.URL));
    }

}
