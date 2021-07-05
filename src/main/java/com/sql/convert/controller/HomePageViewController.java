package com.sql.convert.controller;

import com.sql.convert.util.JavaFxViewUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/6/5 18:52
 * @description : com.sql.convert.controller
 */
@Slf4j
@FXMLController
public class HomePageViewController implements Initializable {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button fieldMatching;
    @FXML
    private Button sqlConvert;
    @FXML
    private Button databaseCount;
    @FXML
    private Button databaseExcel;

    /**
     * <p>@description : 两库表字段匹配工具 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/5 23:48 </p>
     **/
    public void fieldMatching(ActionEvent actionEvent) {
        JavaFxViewUtil.windowToSkip(Boolean.TRUE, "/view/FieldMatchingLoginView.fxml", "convert", actionEvent);
    }

    /**
     * <p>@description : SQL转换相关工具 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/5 23:48 </p>
     **/
    public void sqlConvert(ActionEvent actionEvent) {
        JavaFxViewUtil.windowToSkip(Boolean.TRUE, "/view/SqlConvertLoginView.fxml", "convert", actionEvent);
    }

    /**
     * <p>@description : 库表数据条数查询工具 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/5 23:48 </p>
     **/
    public void databaseCount(ActionEvent actionEvent) {
        JavaFxViewUtil.windowToSkip(Boolean.TRUE, "/view/DatabaseCountLoginView.fxml", "convert", actionEvent);
    }

    /**
     * <p>@description : 数据库DDL导出工具 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/5 23:48 </p>
     **/
    public void databaseExcel(ActionEvent actionEvent) {
        JavaFxViewUtil.windowToSkip(Boolean.TRUE, "/view/DataBaseExcelLoginView.fxml", "convert", actionEvent);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
