package com.sql.convert.config;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/5/19 20:50
 * @description : com.sql.convert.config
 */
public interface CommonValues {

    /**
     * 获取当前系统用户名称
     */
    String SYSTEM_USER_NAME = System.getenv().get("USERNAME");
}
