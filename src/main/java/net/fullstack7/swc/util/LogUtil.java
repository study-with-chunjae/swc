package net.fullstack7.swc.util;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class LogUtil {
    public static void logLine(){
       log.info("==================================");
    }
    public static void logLine(String msg){
        log.info("================={}================",msg);
    }
    public static void log(String name, Object value){
        log.info("{}: {}", name, value.toString());
    }
    public static void log(String name, int value){
        log.info("{}: {}", name, value);
    }
    public static void log(String name, boolean value){
        log.info("{}: {}", name, value);
    }
}
