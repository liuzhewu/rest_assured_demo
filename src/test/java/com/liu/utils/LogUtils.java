package com.liu.utils;

import com.liu.enums.LogLevel;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.liu.constant.Constants.RESOURCES;
import static com.liu.constant.Constants.SP;

public class LogUtils {
    static Map<String, Logger> logMap = new HashMap<String, Logger>();

    static {
        PropertyConfigurator.configure(RESOURCES + SP + "log4j.properties");
    }

    public static void info(String msg) {
        log(msg, LogLevel.INFO);
    }

    public static void warn(String msg) {
        log(msg, LogLevel.WARN);
    }

    public static void error(String msg) {
        log(msg, LogLevel.ERROR);
    }

    public static void debug(String msg) {
        log(msg, LogLevel.DEBUG);
    }

    private static void log(String msg, LogLevel level) {
        StackTraceElement statck = new Throwable().getStackTrace()[2];
        String className = new Throwable().getStackTrace()[2].getClassName();
        Logger log = logMap.get(className);
        if (null == log) {
            log = LoggerFactory.getLogger(className);
            logMap.put(className, log);
        }
        switch (level) {
            case DEBUG:
                log.debug(statck + StringUtils.SPACE + msg);
                break;
            case WARN:
                log.warn(statck + StringUtils.SPACE + msg);
                break;
            case ERROR:
                log.error(statck + StringUtils.SPACE + msg);
                break;

            default:
                log.info(statck + StringUtils.SPACE + msg);
                break;
        }

    }

    public static void main(String[] args) {
        info("test log");
    }

}
