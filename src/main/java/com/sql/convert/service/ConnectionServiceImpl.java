package com.sql.convert.service;

import com.google.common.collect.Lists;
import com.sql.convert.pojo.bo.DataBase;
import com.sql.convert.pojo.vo.DataBaseVo;
import com.sql.convert.util.BaseData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/4/24 17:00
 */
@Slf4j
@Service
@Order(-1)
public class ConnectionServiceImpl implements ConnectionService {

    @Override
    public boolean connection(DataBaseVo dataBaseVo) throws Exception {
        Connection connection = null;
        try {
            // 加载数据库驱动
            Class.forName(dataBaseVo.getDbType().getDriver());
            connection = DriverManager.getConnection(dataBaseVo.getUrlFull(),
                    dataBaseVo.getUserName(), dataBaseVo.getPassword());
            // 保存连接
            saveConnection(dataBaseVo, connection);
        } catch (SQLException | ClassNotFoundException e) {
            log.error("数据库连接异常:", e);
            throw e;
        }
        return Objects.nonNull(connection);
    }

    /**
     * <p>@description : 保存数据库连接 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/5 19:16 </p>
     *
     * @param dataBaseVo 登录连接信息
     * @param connection jdbc连接信息
     **/
    private void saveConnection(DataBaseVo dataBaseVo, Connection connection) {
        List<DataBase> collBaseData = BaseData.getCollBaseData();
        if (CollectionUtils.isEmpty(collBaseData)) {
            collBaseData = Lists.newLinkedList();
        }
        collBaseData.add(DataBase.builder()
                .dataBaseVo(dataBaseVo)
                .connection(connection)
                .build());
        BaseData.setBaseData(collBaseData);
    }
}
