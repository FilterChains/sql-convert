package com.sql.convert.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sql.convert.pojo.bo.MatchData;
import com.sql.convert.pojo.bo.SqlBo;
import com.sql.convert.util.SymbolUtil;
import lombok.SneakyThrows;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/6/8 16:10
 * @description : com.sql.convert.config
 */
public class CacheData {

    /**
     * 设置过期时间，TimeUnit.SECONDS是指的多少秒过期
     * 本次设置1小时过期
     */
    private static final Cache<String, List<MatchData>> SEARCH = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();
    private static final Cache<String, Map<String, Map<String, List<SqlBo>>>> SQL_DATE = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    /**
     * <p>@description : 从缓存获取匹配结果 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/8 16:22 </p>
     *
     * @return {@link List<MatchData>}
     **/
    @SneakyThrows
    public static List<MatchData> getSearchCacheData() {
        return SEARCH.get(SymbolUtil.SEARCH_CACHE_KEY, new Callable<List<MatchData>>() {
            @Override
            public List<MatchData> call() throws Exception {
                return Collections.emptyList();
            }
        });
    }

    /**
     * <p>@description : 从缓存获取读取数据库结果 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/8 16:22 </p>
     *
     * @return {@link Map<String, Map<String, List<SqlBo>>>}
     **/
    @SneakyThrows
    public static Map<String, Map<String, List<SqlBo>>> getCacheData() {
        return SQL_DATE.get(SymbolUtil.CACHE_KEY, new Callable<Map<String, Map<String, List<SqlBo>>>>() {
            @Override
            public Map<String, Map<String, List<SqlBo>>> call() throws Exception {
                return Collections.emptyMap();
            }
        });
    }

    /**
     * <p>@description : 将匹配结果存入缓存中 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/8 16:22 </p>
     *
     * @param list 匹配结果
     **/
    public static void setSearchCacheDate(List<MatchData> list) {
        SEARCH.put(SymbolUtil.SEARCH_CACHE_KEY, list);
    }

    /**
     * <p>@description : 将读取数据库结果存入缓存中 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/8 16:22 </p>
     *
     * @param map 读取数据库结果
     **/
    public static void setCacheDate(Map<String, Map<String, List<SqlBo>>> map) {
        SQL_DATE.put(SymbolUtil.CACHE_KEY, map);
    }

    /**
     * <p>@description : 清除缓存 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/8 16:23 </p>
     **/
    public static void clearCache() {
        // 清除缓存
        SEARCH.invalidateAll();
        SQL_DATE.invalidateAll();
    }
}
