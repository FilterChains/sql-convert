package com.sql.convert.util;

import com.sql.convert.config.CacheData;
import com.sql.convert.pojo.bo.DataBase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/4/24 17:09
 * @description : org.su.common
 */
@Slf4j
public class BaseData {

    private static final ThreadLocal<List<DataBase>> BASE_DATA = new ThreadLocal<>();

    public static void setBaseData(List<DataBase> dataBases) {
        BASE_DATA.set(dataBases);
    }

    /**
     * <p>@description : 单数据源 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/5 18:49 </p>
     *
     * @return {@link DataBase}
     **/
    public static DataBase getBaseData() {
        List<DataBase> dataBases = BASE_DATA.get();
        return CollectionUtils.isEmpty(dataBases) ? DataBase.builder().build()
                : dataBases.get(0);
    }

    /**
     * <p>@description : 多数据源 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/5 18:49 </p>
     *
     * @return {@link List<DataBase>}
     **/
    public static List<DataBase> getCollBaseData() {
        return BASE_DATA.get();
    }

    /**
     * <p>@description : 关闭连接,并注销信息  </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/5/20 10:46 </p>
     **/
    public static void closeConnection() {
        List<DataBase> dataBase = BASE_DATA.get();
        if (CollectionUtils.isEmpty(dataBase)) {
            return;
        }
        try {
            for (DataBase base : dataBase) {
                Connection connection = base.getConnection();
                if (Objects.nonNull(connection)) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            log.error("数据库连接关闭失败,原因,", e);
        }
        BASE_DATA.remove();
        log.info("注销当前连接信息");
        // 清除当前缓存
        CacheData.clearCache();
    }
}
