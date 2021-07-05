package com.sql.convert.pojo.bo;

import com.sql.convert.pojo.vo.DataBaseVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Connection;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/5/20 11:38
 * @description : com.sql.convert.pojo.bo
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataBase implements Serializable {

    private DataBaseVo dataBaseVo;

    private Connection connection;

}
