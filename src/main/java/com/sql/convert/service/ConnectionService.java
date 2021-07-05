package com.sql.convert.service;


import com.sql.convert.pojo.vo.DataBaseVo;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/4/24 17:00
 */
public interface ConnectionService {

    /**
     * <p>@description : 连接数据库 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/4/26 15:55 </p>
     *
     * @param dataBaseVo ->连接数据库参数实体类
     * @return {@link Boolean}true ->成功;false->失败
     **/
    boolean connection(DataBaseVo dataBaseVo) throws Exception;

}
