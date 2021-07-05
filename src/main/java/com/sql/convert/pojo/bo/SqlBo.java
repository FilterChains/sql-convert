package com.sql.convert.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/4/21 14:37
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SqlBo implements Serializable {

    private String tableName;

    private String tableFieldName;

    private Integer sort;

    private String isNullAble;
}
