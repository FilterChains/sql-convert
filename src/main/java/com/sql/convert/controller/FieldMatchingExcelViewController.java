package com.sql.convert.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sql.convert.config.CacheData;
import com.sql.convert.pojo.bo.DataBase;
import com.sql.convert.pojo.bo.MatchData;
import com.sql.convert.pojo.bo.MatchFieldExcel;
import com.sql.convert.pojo.bo.SqlBo;
import com.sql.convert.util.BaseData;
import com.sql.convert.util.ExcelExport;
import com.sql.convert.util.JavaFxViewUtil;
import com.sql.convert.util.StringTrimUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/6/8 18:35
 * @description : com.sql.convert.controller
 */
@Slf4j
@FXMLController
public class FieldMatchingExcelViewController implements Initializable {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button excel;
    @FXML
    private Button export;
    @FXML
    private TextField filePath;

    /**
     * <p>@description : 选择导出路径 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/5 19:07 </p>
     *
     * @param actionEvent 当前画板事件
     **/
    public void export(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(((Node) actionEvent.getSource()).getScene().getWindow());
        String path = file.getPath();//选择的文件夹路径
        filePath.setText(path);
    }

    /**
     * <p>@description : 开始导出 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/5 19:07 </p>
     *
     * @param actionEvent 当前画板事件
     **/
    public void excel(ActionEvent actionEvent) {
        // 获取文件路径
        String path = StringTrimUtil.trimCat(filePath.getText());
        if (StringUtils.isBlank(path)) {
            JavaFxViewUtil.alertMessage(Alert.AlertType.ERROR, "请为导出文件选择路径");
            return;
        }
        // 从本地缓存获取一般匹配的表名称开始导出未完全匹配的数据
        List<MatchData> cacheData = CacheData.getSearchCacheData();
        if (CollectionUtils.isEmpty(cacheData)) {
            JavaFxViewUtil.alertMessage(Alert.AlertType.ERROR, "暂无可导出数据");
            return;
        }
        List<MatchData> matchData = cacheData.stream().filter(x -> !x.getPercentage().equals(BigDecimal.ONE) &&
                !x.getPercentage().equals(BigDecimal.ZERO)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(matchData)) {
            JavaFxViewUtil.alertMessage(Alert.AlertType.ERROR, "暂无可导出数据");
            return;
        }
        List<String> oneTableName = matchData.stream().filter(x -> StringUtils.isNotBlank(x.getDbOneTableName()) &&
                !"暂无可匹配对象".equals(x.getDbOneTableName())).map(MatchData::getDbOneTableName)
                .collect(Collectors.toList());
        List<String> twoTableName = matchData.stream().filter(x -> StringUtils.isNotBlank(x.getDbTwoTableName()) &&
                !"暂无可匹配对象".equals(x.getDbTwoTableName())).map(MatchData::getDbTwoTableName)
                .collect(Collectors.toList());

        // 从缓存中获取数据库字段
        List<DataBase> collBaseData = BaseData.getCollBaseData();
        Map<String, Map<String, List<SqlBo>>> mapMap = CacheData.getCacheData();
        Map<String, List<SqlBo>> dbOneMap = mapMap.get(StringTrimUtil.getConcatMapKey(collBaseData.get(0).getDataBaseVo()));
        Map<String, List<SqlBo>> dbTwoMap = mapMap.get(StringTrimUtil.getConcatMapKey(collBaseData.get(1).getDataBaseVo()));
        Map<String, List<SqlBo>> hashMapOne = Maps.newLinkedHashMap();
        Map<String, List<SqlBo>> hashMapTwo = Maps.newLinkedHashMap();
        dbOneMap.forEach((k, v) -> {
            if (oneTableName.contains(k)) {
                hashMapOne.put(k, v);
            }
        });
        dbTwoMap.forEach((k, v) -> {
            if (twoTableName.contains(k)) {
                hashMapTwo.put(k, v);
            }
        });
        //组装数据,并导出
        ExcelExport.export(path, assembleData(hashMapOne, hashMapTwo));
        // 关闭当前窗口
        JavaFxViewUtil.closeWindows(actionEvent);
    }

    /**
     * <p>@description : 组装数据 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/8 20:59 </p>
     *
     * @param hashMapOne 数据One
     * @param hashMapTwo 数据Two
     * @return {@link List<MatchFieldExcel>}
     **/
    private List<MatchFieldExcel> assembleData(Map<String, List<SqlBo>> hashMapOne, Map<String, List<SqlBo>> hashMapTwo) {
        List<MatchFieldExcel> list = Lists.newLinkedList();
        Set<Map.Entry<String, List<SqlBo>>> entrySet = hashMapOne.entrySet();
        for (Map.Entry<String, List<SqlBo>> entry : entrySet) {
            String key = entry.getKey();
            List<SqlBo> value = entry.getValue();
            List<SqlBo> sqlBos = hashMapTwo.get(key);
            Map<String, SqlBo> boMap = sqlBos.stream().collect(Collectors.toMap(SqlBo::getTableFieldName, sqlBo -> sqlBo));
            Set<String> keySet = boMap.keySet();
            for (SqlBo sqlBo : value) {
                final String tableFieldName = sqlBo.getTableFieldName();
                if (keySet.contains(tableFieldName)) {
                    list.add(MatchFieldExcel.builder()
                            .oneTableName(key)
                            .oneField(tableFieldName)
                            .oneIsNullAble(sqlBo.getIsNullAble())
                            .oneComment("")
                            .twoTableName(key)
                            .twoField(tableFieldName)
                            .twoIsNullAble(boMap.get(tableFieldName).getIsNullAble())
                            .twoComment("")
                            .isMatch("匹配")
                            .build());
                    continue;
                }
                list.add(MatchFieldExcel.builder()
                        .oneTableName(key)
                        .oneField(tableFieldName)
                        .oneIsNullAble(sqlBo.getIsNullAble())
                        .oneComment("")
                        .twoTableName(key)
                        .twoField("")
                        .twoIsNullAble("")
                        .twoComment("")
                        .isMatch("不匹配(DataBase-Two无此字段)")
                        .build());

            }
        }
        return list;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
