package com.sql.convert.pojo.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/6/8 19:23
 * @description : com.sql.convert.pojo.bo
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MatchFieldExcel implements Serializable {

    @ExcelProperty("DataBase-One:表名")
    private String oneTableName;

    @ExcelProperty("DataBase-One:表字段")
    private String oneField;

    @ExcelProperty("DataBase-One:表字段是否为空")
    private String oneIsNullAble;

    @ExcelProperty("DataBase-One:表字段备注")
    private String oneComment;

    @ExcelProperty("DataBase-Two:表名")
    private String twoTableName;

    @ExcelProperty("DataBase-Two:表字段")
    private String twoField;

    @ExcelProperty("DataBase-Two:表字段是否为空")
    private String twoIsNullAble;

    @ExcelProperty("DataBase-Two:表字段备注")
    private String twoComment;

    @ExcelProperty("是否匹配")
    private String isMatch;

}
