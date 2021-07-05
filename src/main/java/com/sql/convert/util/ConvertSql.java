package com.sql.convert.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sql.convert.enums.DataBasesEnum;
import com.sql.convert.pojo.bo.SqlDate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/5/20 12:50
 * @description : com.sql.convert.util
 */
public class ConvertSql {

    private final String sqlContent;

    private final SqlDate BUILD = SqlDate.builder().build();

    private final List<String> list;

    private final boolean flag;

    public ConvertSql(String sqlContent) {
        String toLowerCase = sqlContent.toLowerCase();
        this.flag = toLowerCase.contains(SymbolUtil.CREATE_TABLE);
        this.list = spiltStr(";", toLowerCase.replaceAll("；", ";")
                .replaceAll("（", "(")
                .replaceAll("）", ")")
                .replaceAll("，", ",")
                .replaceAll("`", ""));
        this.sqlContent = sqlContent;
    }

    /**
     * <p>@description : 根据不同数据库类型转换不同的数据库sql语句 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/5/20 12:56 </p>
     *
     * @param dataBasesEnum 数据库类型
     * @return String
     **/
    public String convert(DataBasesEnum dataBasesEnum) {
        if (flag) {
            String s = filtrationContent();
            return StringUtils.isNotBlank(s) ? s : convertCreateSql(dataBasesEnum);
        } else {
            return "";
        }
    }

    /**
     * <p>@description : 统一获取sql创建信息 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/5/21 23:07 </p>
     *
     * @return String
     **/
    private String filtrationContent() {
        String result = null;
        try {
            // 过滤出创建语句
            List<String> createList = list.stream().filter(x ->
                    x.contains(SymbolUtil.CREATE_TABLE) &&
                            x.contains(SymbolUtil.LEFT_BRACKET))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(createList)) {
                return "暂无创建语句可以转换";
            }
            // 过滤字段备注信息
            List<String> filedList = list.stream().filter(x -> x.contains(SymbolUtil.FILED)).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(filedList)) {
                Map<String, Map<String, String>> linkedHashMap = new LinkedHashMap<>();
                List<List<String>> linkedList = Lists.newLinkedList();
                for (String cm : createList) {
                    String tableName = null;
                    List<String> sqlList = Lists.newLinkedList();
                    Map<String, String> map = Maps.newLinkedHashMap();
                    List<String> list = spiltStr("\n", cm);
                    for (String com : list) {
                        result = com;
                        com = com.replace("\"", "");
                        List<String> spiltStr = spiltStr(SymbolUtil.SPACING, com);
                        int size = spiltStr.size();
                        String s = spiltStr.get(size - 1)
                                .replaceAll(",", "")
                                .replaceAll("\\)", "");
                        if (com.startsWith("create") && com.endsWith(SymbolUtil.LEFT_BRACKET)) {
                            if (1 != s.length()) {
                                tableName = s.replaceAll("\\(", "");
                                if (tableName.contains(".")) {
                                    List<String> sp = spiltStr(".", tableName);
                                    tableName = sp.get(sp.size() - 1);
                                }
                            } else {
                                tableName = spiltStr.get(size - 2);
                            }
                            sqlList.add(tableName);
                            continue;
                        }
                        if (null == tableName) {
                            continue;
                        }
                        if (com.startsWith(SymbolUtil.COMMENT) &&
                                SymbolUtil.COMMENT.equals(spiltStr.get(0))) {
                            map.put(tableName, s);
                            linkedHashMap.put(tableName, map);
                            break;
                        }
                        if (com.contains(SymbolUtil.COMMENT_EQ)) {
                            map.put(tableName, s.replaceAll(SymbolUtil.COMMENT_EQ, ""));
                            linkedHashMap.put(tableName, map);
                            break;
                        }
                        if (com.startsWith(")")) {
                            break;
                        }
                        if (com.contains(SymbolUtil.COMMENT)) {
                            map.put(spiltStr.get(0), s);
                            sqlList.add(spiltStr.get(0).concat(SymbolUtil.SPACING)
                                    .concat(SymbolUtil.SPACING).concat(spiltStr.get(1)));
                        }
                    }
                    linkedList.add(sqlList);
                    linkedHashMap.put(tableName, map);
                }
                BUILD.setCreateList(linkedList);
                BUILD.setFiledMap(linkedHashMap);
                result = null;
            } else {
                List<List<String>> linkedList = Lists.newLinkedList();
                for (String s : createList) {
                    List<String> spiltStr = spiltStr("\n", s);
                    List<String> list = Lists.newLinkedList();
                    for (String s1 : spiltStr) {
                        s1 = s1.replace("\"", "");
                        List<String> str = spiltStr(SymbolUtil.SPACING, s1
                                .replaceAll("\\);", ""));
                        if (CollectionUtils.isEmpty(str)) {
                            break;
                        }
                        if (s1.startsWith(")") && 1 == s1.length()) {
                            break;
                        }
                        int size = str.size();
                        if (s1.endsWith(SymbolUtil.LEFT_BRACKET)) {
                            List<String> str1 = spiltStr(SymbolUtil.SPACING, s1);
                            String s2 = str1.get(str1.size() - 2);
                            list.add(spiltStr(".", s2).get(1));
                            continue;
                        }
                        if (2 <= size) {
                            list.add(str.get(0).concat(SymbolUtil.SPACING)
                                    .concat(SymbolUtil.SPACING).concat(str.get(1)));
                        }
                    }
                    linkedList.add(list);
                }
                Map<String, Map<String, String>> linkedHashMap = new LinkedHashMap<>();
                // 过滤表名称语句
                List<String> tableName = filedList.stream().filter(x -> x.contains(SymbolUtil.PG_COMMENT)).collect(Collectors.toList());
                for (String tn : tableName) {
                    result = tn;
                    tn = tn.replace("\"", "");
                    Map<String, String> map = Maps.newLinkedHashMap();
                    List<String> spiltStr = spiltStr(SymbolUtil.IS, tn);
                    String remark = spiltStr.get(1);
                    List<String> str = spiltStr(".", spiltStr.get(0));
                    String tName = str.get(1);
                    map.put(tName, remark);
                    linkedHashMap.put(tName, map);
                }
                // 过滤所有备注字段
                filedList = filedList.stream().filter(x -> !x.contains(SymbolUtil.PG_COMMENT)).collect(Collectors.toList());
                for (String fd : filedList) {
                    result = fd;
                    fd = fd.replace("\"", "");
                    List<String> spiltStr = spiltStr(SymbolUtil.IS, fd);
                    String remark = spiltStr.get(1);
                    List<String> str = spiltStr(".", spiltStr.get(0));
                    String tName = str.get(1);
                    String filed = str.get(2);
                    if (!linkedHashMap.containsKey(tName)) {
                        Map<String, String> map = Maps.newLinkedHashMap();
                        map.put(filed, remark);
                        linkedHashMap.put(tName, map);
                        continue;
                    }
                    Map<String, String> filedMap = linkedHashMap.get(tName);
                    filedMap.put(filed, remark);
                    linkedHashMap.put(tName, filedMap);
                }
                BUILD.setCreateList(linkedList);
                BUILD.setFiledMap(linkedHashMap);
                result = null;
            }
        } catch (Exception e) {
            result = String.format("转换异常-[请检查对应格式]: [%s],%s", result, e.getMessage());
        }
        return result;
    }

    /**
     * <p>@description : 字符串分割 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/5/21 22:09 </p>
     *
     * @param separator 分隔符
     * @param text      分割字符串
     * @return List<String>
     **/
    private List<String> spiltStr(String separator, String text) {
        return Splitter.on(separator).trimResults().omitEmptyStrings()
                .splitToList(text);
    }


    /**
     * <p>@description : 创建语句转换 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/5/20 13:22 </p>
     *
     * @param dataBasesEnum 数据库类型
     * @return String
     **/
    private String convertCreateSql(DataBasesEnum dataBasesEnum) {
        List<List<String>> createList = BUILD.getCreateList();
        if (CollectionUtils.isEmpty(createList)) {
            return "暂无创建语句";
        }
        switch (dataBasesEnum) {
            case MYSQL:
                return convertToCreateMysqlSql(createList);
            case PG_SQL:
                return convertToCreatePgSql(createList);
            case ORACLE:
                return convertToCreateOracleSql(createList);
            case VERTICA:
                return convertToCreateVerticaSql(createList);
            default:
                return "";
        }
    }


    private String convertToCreatePgSql(List<List<String>> createList) {
        List<String> linkedList = Lists.newLinkedList();
        // 获取字段属性Map
        Map<String, Map<String, String>> filedMap = BUILD.getFiledMap();
        for (List<String> sqlList : createList) {
            int i = 1;
            int size = sqlList.size();
            String tableName = null;
            Map<String, String> map = null;
            List<String> filedList = Lists.newLinkedList();
            for (String sql : sqlList) {
                // 开始组装sql语句
                if (!sql.contains(SymbolUtil.SPACING)) {
                    tableName = sql;
                    map = filedMap.get(tableName);
                    linkedList.add("");
                    linkedList.add(String.format(SymbolUtil.DROP_TABLE, sql));
                    linkedList.add(String.format(SymbolUtil.PG_CREATE, sql));
                    filedList.add("");
                    filedList.add(String.format(SymbolUtil.PG_COMMENT_TABLE, sql, map.get(sql)));
                    i++;
                    continue;
                }
                if (MapUtils.isEmpty(map)) {
                    break;
                }
                if (sql.endsWith(",")) {
                    sql = sql.substring(0, sql.length() - 1);
                }
                List<String> spiltStr = spiltStr(SymbolUtil.SPACING, sql);
                String s = spiltStr.get(0);
                String s1 = map.get(s);
                if (StringUtils.isNotBlank(s1)) {
                    filedList.add(String.format(SymbolUtil.PG_COMMENT_COLUMN, tableName, s, s1));
                }
                String concat = sql.replaceAll("decimal", "numeric")
                        .replaceAll("datetime", "timestamp")
                        .replaceAll("string", "varchar(255)");
                if (sql.contains("int") || sql.contains("integer")) {
                    concat = s.concat(SymbolUtil.SPACING).concat(SymbolUtil.SPACING).concat("integer");
                }
                concat = SymbolUtil.SPACING.concat(SymbolUtil.SPACING).concat(concat);
                if (i == size) {
                    linkedList.add(concat);
                    linkedList.add(");");
                    break;
                }
                linkedList.add(concat.concat(","));
                i++;
            }
            linkedList.addAll(filedList);
        }
        return String.join("\n", linkedList);
    }

    private String convertToCreateMysqlSql(List<List<String>> createList) {
        // 获取字段属性Map
        Map<String, Map<String, String>> filedMap = BUILD.getFiledMap();
        List<String> linkedList = Lists.newLinkedList();
        for (List<String> sqlList : createList) {
            int i = 1;
            int size = sqlList.size();
            String tableName = null;
            Map<String, String> map = null;
            for (String sql : sqlList) {
                // 开始组装sql语句
                if (!sql.contains(SymbolUtil.SPACING)) {
                    tableName = sql;
                    map = filedMap.get(tableName);
                    linkedList.add("");
                    linkedList.add(String.format(SymbolUtil.DROP_TABLE, sql));
                    linkedList.add(String.format(SymbolUtil.MYSQL_START_CREATE, sql));
                    i++;
                    continue;
                }
                if (MapUtils.isEmpty(map)) {
                    break;
                }
                if (sql.endsWith(",")) {
                    sql = sql.substring(0, sql.length() - 1);
                }
                List<String> spiltStr = spiltStr(SymbolUtil.SPACING, sql);
                String s = map.get(spiltStr.get(0));
                String concat = SymbolUtil.SPACING
                        .concat(SymbolUtil.SPACING).concat(sql)
                        .concat(SymbolUtil.SPACING)
                        .concat(SymbolUtil.SPACING)
                        .concat(StringUtils.isBlank(s) ? "" :
                                SymbolUtil.COMMENT.concat(SymbolUtil.SPACING)
                                        .concat(SymbolUtil.SPACING).concat(s))
                        .replaceAll("string", "varchar(255)")
                        .replaceAll("numeric", "decimal")
                        .replaceAll("timestamp", "datetime")
                        .replaceAll("integer", "int")
                        .replaceAll("money", "decimal");
                if (i == size) {
                    linkedList.add(concat);
                    linkedList.add(String.format(SymbolUtil.MYSQL_END_CREATE, map.get(tableName)));
                    break;
                }
                linkedList.add(concat.concat(","));
                i++;
            }
        }
        return String.join("\n", linkedList);
    }

    private String convertToCreateOracleSql(List<List<String>> createList) {
        List<String> linkedList = Lists.newLinkedList();
        return String.join("\n", linkedList);
    }

    private String convertToCreateVerticaSql(List<List<String>> createList) {
        List<String> linkedList = Lists.newLinkedList();


        return String.join("\n", linkedList);
    }
}
