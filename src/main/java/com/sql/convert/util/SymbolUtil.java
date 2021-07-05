package com.sql.convert.util;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/4/27 2:04
 * @description : 符号工具
 */
public interface SymbolUtil {

    String DOUBLE_SLASH = "//";

    String SPACING = " ";

    String CHARACTER_P = "\\s+";

    String LEFT_BRACKET = "(";

    String SINGLE_SLASH = "/";

    String COLON = ":";

    String MYSQL_URL = "jdbc:mysql://";

    String PG_URL = "jdbc:postgresql://";

    String ORACLE_URL = "jdbc:oracle:thin:@";

    String VERTICA_URL = "jdbc:vertica://";

    String CREATE_TABLE = "create table";

    String UPDATE_TABLE = "update";

    String DELETE_TABLE = "delete from";

    String ALTER_TABLE = "alter table";

    String PG_COMMENT_TABLE = "comment on table \"public\".\"%s\" IS %s;";

    String PG_COMMENT_COLUMN = "comment on column \"public\".\"%s\".\"%s\" IS %s;";

    String PG_COMMENT = "comment on table ";

    String PG_CREATE = "create table \"public\".\"%s\" (";

    String MYSQL_START_CREATE = "create table %s (";

    String MYSQL_END_CREATE = ") engine=innodb default charset=utf8 comment=%s;";

    String FILED = "comment on";

    String COMMENT = "comment";

    String COMMENT_EQ = "comment=";

    String DROP_TABLE = "drop table if exists %s ;";

    String IS = " is ";

    String WITH = "with ";

    int LEN = 2;

    String TRANSVERSE_LINE = "-";

    String SEARCH_CACHE_KEY = "CACHE_DATA_SEARCH";

    String CACHE_KEY = "CACHE_DATA";

    String YES = "YES";

    String NO = "NO";
}
