package com.sql.convert.pojo.vo;

import com.sql.convert.enums.DataBasesEnum;
import com.sql.convert.util.SymbolUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/4/24 17:02
 * @description : 数据库连接实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class DataBaseVo implements Serializable {

    /**
     * 数据库连接信息
     */
    private String url;
    private String port;
    private String dbName;
    private String userName;
    private String password;
    private String type;
    private DataBasesEnum dbType;

    public void setType(String type) {
        if (DataBasesEnum.MYSQL.getName().equals(type)) {
            this.dbType = DataBasesEnum.MYSQL;
        } else if (DataBasesEnum.PG_SQL.getName().equals(type)) {
            this.dbType = DataBasesEnum.PG_SQL;
        } else if (DataBasesEnum.ORACLE.getName().equals(type)) {
            this.dbType = DataBasesEnum.ORACLE;
        } else if (DataBasesEnum.VERTICA.getName().equals(type)) {
            this.dbType = DataBasesEnum.VERTICA;
        } else {
            this.dbType = DataBasesEnum.OTHER;
        }
        this.type = type;
    }

    public String getUrlFull() {
        String connectionUrl = "";
        switch (dbType) {
            case MYSQL:
                connectionUrl = SymbolUtil.MYSQL_URL.concat(suffix());
                break;
            case PG_SQL:
                connectionUrl = SymbolUtil.PG_URL.concat(suffix());
                break;
            case ORACLE:
                connectionUrl = SymbolUtil.ORACLE_URL.concat(suffix());
                break;
            case VERTICA:
                connectionUrl = SymbolUtil.VERTICA_URL.concat(suffix());
                break;
            default:
                break;
        }
        return connectionUrl;
    }

    private String suffix() {
        return url.concat(SymbolUtil.COLON).concat(port)
                .concat(DataBasesEnum.ORACLE.equals(dbType) ? SymbolUtil.COLON :
                        SymbolUtil.SINGLE_SLASH).concat(dbName);
    }
}
