package com.sql.convert.controller;

import com.google.common.collect.Lists;
import com.sql.convert.enums.DataBasesEnum;
import com.sql.convert.pojo.bo.DataBase;
import com.sql.convert.pojo.vo.DataBaseVo;
import com.sql.convert.service.ConnectionService;
import com.sql.convert.springutil.SpringUtil;
import com.sql.convert.util.BaseData;
import com.sql.convert.util.JavaFxViewUtil;
import com.sql.convert.util.StringTrimUtil;
import com.sql.convert.util.ValidateDate;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/6/6 11:35
 * @description : com.sql.convert.controller
 */
@Slf4j
@FXMLController
public class DataBaseExcelViewController implements Initializable {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button connection;
    @FXML
    private TextField url;
    @FXML
    private TextField port;
    @FXML
    private TextField dbName;
    @FXML
    private TextField userName;
    @FXML
    private PasswordField password;
    @FXML
    private ChoiceBox<String> cBox;
    @FXML
    private Button logOut;


    /**
     * <p>@description : 退出 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/5 19:07 </p>
     *
     * @param actionEvent 当前画板事件
     **/
    public void logOut(ActionEvent actionEvent) {
        JavaFxViewUtil.windowToSkip(Boolean.TRUE, "/view/HomePageView.fxml", "convert", actionEvent);
    }

    /**
     * <p>@description : 数据库连接 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/5/19 21:07 </p>
     **/
    public void connection(ActionEvent actionEvent) {
        try {
            log.info("数据库连接");
            final String value = cBox.getValue();
            final String dbUrl = StringTrimUtil.trimCat(url.getText());
            final String dbPort = StringTrimUtil.trimCat(port.getText());
            final String dbDbName = StringTrimUtil.trimCat(dbName.getText());
            final String dbUserName = StringTrimUtil.trimCat(userName.getText());
            final String dbPassword = StringTrimUtil.trimCat(password.getText());
            if (DataBasesEnum.OTHER.getName().equals(value)) {
                List<DataBase> list = Lists.newLinkedList();
                list.add(DataBase.builder()
                        .dataBaseVo(DataBaseVo.builder()
                                .dbType(DataBasesEnum.OTHER).build())
                        .connection(null)
                        .build());
                BaseData.setBaseData(list);
                JavaFxViewUtil.windowToSkip(Boolean.TRUE, "/view/SqlConvertView.fxml", "convert", actionEvent);
                return;
            }
            final String data = ValidateDate.validateDataBaseData(dbUrl, dbPort, dbDbName, dbUserName, dbPassword);
            if (StringUtils.isNotBlank(data)) {
                JavaFxViewUtil.alertCauseBy(Alert.AlertType.ERROR, "连接失败", data);
                return;
            }
            //获取用户账号||判断是否已连接数据库
            DataBase baseData = BaseData.getBaseData();
            if (Objects.nonNull(baseData) && Objects.nonNull(baseData.getConnection())) {
                JavaFxViewUtil.alertCauseBy(Alert.AlertType.CONFIRMATION, "已连接数据库请勿重复连接", "连接成功");
                return;
            }
            // 连接数据库
            ConnectionService connectionService = SpringUtil.getBean(ConnectionService.class);
            DataBaseVo build = DataBaseVo.builder()
                    .url(dbUrl).port(dbPort).dbName(dbDbName)
                    .userName(dbUserName).password(dbPassword)
                    .build();
            build.setType(value);
            connectionService.connection(build);
            JavaFxViewUtil.alertMessage(Alert.AlertType.INFORMATION, "连接成功");
            JavaFxViewUtil.windowToSkip(Boolean.TRUE, "/view/SqlConvertView.fxml", "convert", actionEvent);
        } catch (Exception e) {
            log.error("数据库连接异常", e);
            JavaFxViewUtil.alertCauseBy(Alert.AlertType.ERROR, "数据库连接失败", e.getMessage());
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 初始选择框值
        cBox.getItems().addAll(Arrays.stream(DataBasesEnum.values())
                .map(DataBasesEnum::getName).collect(Collectors.toList())
                .stream().filter(x -> !DataBasesEnum.OTHER.getName()
                        .equals(x)).collect(Collectors.toList()));
        //默认选择第一个
        cBox.getSelectionModel().selectFirst();
    }
}
