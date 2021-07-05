package com.sql.convert.enums;

import lombok.Getter;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/4/21 14:12
 * @description : com.databases.tabledetails.eums.mysql
 */
@Getter
public enum MysqlDataFieldsEnum {
    /**
     *
     */
    TABLE_NAME("TABLE_NAME", "表名称"),
    COLUMN_NAME("COLUMN_NAME", "表字段"),
    ORDINAL_POSITION("ORDINAL_POSITION", ""),
    COLUMN_DEFAULT("COLUMN_DEFAULT", ""),
    IS_NULLABLE("IS_NULLABLE", ""),
    DATA_TYPE("DATA_TYPE", ""),
    CHARACTER_MAXIMUM_LENGTH("CHARACTER_MAXIMUM_LENGTH", ""),
    CHARACTER_OCTET_LENGTH("CHARACTER_OCTET_LENGTH", ""),
    NUMERIC_PRECISION("NUMERIC_PRECISION", ""),
    NUMERIC_SCALE("NUMERIC_SCALE", ""),
    DATETIME_PRECISION("DATETIME_PRECISION", ""),
    CHARACTER_SET_NAME("CHARACTER_SET_NAME", ""),
    COLLATION_NAME("COLLATION_NAME", ""),
    COLUMN_TYPE("COLUMN_TYPE", ""),
    COLUMN_KEY("COLUMN_KEY", ""),
    EXTRA("EXTRA", ""),
    PRIVILEGES("PRIVILEGES", ""),
    COLUMN_COMMENT("COLUMN_COMMENT", "");

    private final String fieldName;

    private final String description;

    MysqlDataFieldsEnum(String fieldName, String description) {
        this.fieldName = fieldName;
        this.description = description;
    }
}
