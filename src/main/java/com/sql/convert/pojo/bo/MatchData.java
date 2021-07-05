package com.sql.convert.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/6/7 21:08
 * @description : com.sql.convert.pojo.bo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchData implements Serializable {

    private String dbOneTableName;

    private String dbTwoTableName;

    private BigDecimal percentage;

    private String match;

}
