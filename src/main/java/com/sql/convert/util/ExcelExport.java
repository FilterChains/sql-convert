package com.sql.convert.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.sql.convert.pojo.bo.MatchFieldExcel;

import java.util.List;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/6/8 19:30
 * @description : com.sql.convert.util
 */
public class ExcelExport {

    /**
     * <p>@description : 数据库字段一般匹配导出 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/8 19:31 </p>
     *
     * @param filePath 导出文件路径
     * @param list     导出数据
     **/
    public static void export(String filePath, List<MatchFieldExcel> list) {
        EasyExcel.write(filePath.concat("\\").concat(String.valueOf(System.currentTimeMillis()))
                .concat(".xlsx"), MatchFieldExcel.class).sheet("一般匹配数据")
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()) // 自适应宽度
                .doWrite(list);
    }
}
