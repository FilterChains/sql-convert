package com.sql.convert.util;

import com.sql.convert.pojo.vo.DataBaseVo;
import org.apache.commons.lang.StringUtils;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/4/26 22:11
 * @description : org.su.util
 */
public class StringTrimUtil {

    /**
     * <p>@description : 字符串去除空格 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/4/26 22:14 </p>
     *
     * @param resource 数据源
     * @return {@link String}
     **/
    public static String trimCat(String resource) {
        return StringUtils.isBlank(resource) ? resource :
                resource.replaceAll("\\s+", "");
    }


    public static String getConcatMapKey(DataBaseVo dataBaseVo) {
        return dataBaseVo.getUrl().concat("-").concat(dataBaseVo.getDbName());
    }
}
