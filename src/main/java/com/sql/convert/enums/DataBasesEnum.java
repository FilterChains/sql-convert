package com.sql.convert.enums;

import lombok.Getter;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/4/23 22:56
 */
@Getter
public enum DataBasesEnum {

    /**
     * Pg数据驱动
     */
    PG_SQL("pg_sql", "org.postgresql.Driver", "select * from pg_class c,pg_attribute a,pg_type t,pg_description d\n" +
            "where a.attnum > 0 and a.attrelid=c.oid and a.atttypid=t.oid and d.objoid=a.attrelid and d.objsubid=a.attnum\n" +
            "and c.relname in (select tablename from pg_tables where schemaname='public' and position('_2' in tablename)=0) order by c.relname,a.attnum\n"),

    /**
     * mysql 驱动
     */
    MYSQL("mysql", "com.mysql.cj.jdbc.Driver", "select * from INFORMATION_SCHEMA.columns where table_schema = '%s'"),

    VERTICA("vertica", "com.vertica.jdbc.Driver", ""),

    ORACLE("oracle", "oracle.jdbc.driver.OracleDriver", ""),

    OTHER("other", "", "");
    private final String name;

    private final String driver;

    private final String sqlField;

    DataBasesEnum(String name, String driver, String sqlField) {
        this.name = name;
        this.driver = driver;
        this.sqlField = sqlField;
    }
}
