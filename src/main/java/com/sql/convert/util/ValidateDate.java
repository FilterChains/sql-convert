package com.sql.convert.util;

import org.apache.commons.lang.StringUtils;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/4/27 0:30
 * @description : 数据验证
 */
public class ValidateDate {

    /**
     * <p>@description : 登录信息验证 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/4/26 22:59 </p>
     *
     * @param name 用户名
     * @param pw   密码
     * @return {@link String}
     **/
    public static String validateLoginData(String name, String pw) {
        if (StringUtils.isBlank(name)) {
            return "用户名不能为空";
        }
        if (StringUtils.isBlank(pw)) {
            return "密码不能为空";
        }
        return null;
    }

    /**
     * <p>@description : 验证数据库连接参数的有效性 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/4/27 0:29 </p>
     *
     * @param dbUrl      url
     * @param dbPort     端口
     * @param dbDbName   数据库名称
     * @param dbUserName 登录用户名
     * @param dbPassword 登录密码
     * @return {@link String}
     **/
    public static String validateDataBaseData(String dbUrl, String dbPort, String dbDbName,
                                              String dbUserName, String dbPassword) {
        if (StringUtils.isBlank(dbUrl)) {
            return "url不能为空";
        }
        if (StringUtils.isBlank(dbPort)) {
            return "数据库端口不能为空";
        }
        if (StringUtils.isBlank(dbDbName)) {
            return "数据库名称不能为空";
        }
        if (StringUtils.isBlank(dbUserName)) {
            return "登录用户不能为空";
        }
        if (StringUtils.isBlank(dbPassword)) {
            return "登录密码不能为空";
        }
        return null;
    }

}
