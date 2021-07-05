package com.sql.convert.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/5/21 21:58
 * @description : com.sql.convert.pojo.bo
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SqlDate {

    /**
     * 创建语句
     */
    private List<List<String>> createList;

    /**
     * 字段备注:key1->表名,key2->字段名,value->备注
     */
    private Map<String, Map<String, String>> filedMap;

}
