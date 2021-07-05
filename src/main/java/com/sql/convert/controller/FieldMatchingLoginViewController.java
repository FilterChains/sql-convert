package com.sql.convert.controller;

import com.sql.convert.enums.DataBasesEnum;
import com.sql.convert.pojo.vo.DataBaseVo;
import com.sql.convert.service.ConnectionService;
import com.sql.convert.springutil.SpringUtil;
import com.sql.convert.util.JavaFxViewUtil;
import com.sql.convert.util.StringTrimUtil;
import com.sql.convert.util.SymbolUtil;
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
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/6/5 18:55
 * @description : com.sql.convert.controller
 */
@Slf4j
@FXMLController
public class FieldMatchingLoginViewController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TextField url_m;
    @FXML
    private TextField port_m;
    @FXML
    private TextField dbName_m;
    @FXML
    private TextField userName_m;
    @FXML
    private PasswordField password_m;
    @FXML
    private ChoiceBox<String> cBox_m;

    @FXML
    private TextField url_s;
    @FXML
    private TextField port_s;
    @FXML
    private TextField dbName_s;
    @FXML
    private TextField userName_s;
    @FXML
    private PasswordField password_s;
    @FXML
    private ChoiceBox<String> cBox_s;
    @FXML
    private Button connection;
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
     * <p>@description : 初始化数据库 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/5 19:07 </p>
     *
     * @param actionEvent 当前画板事件
     **/
    public void connection(ActionEvent actionEvent) {
        try {
            log.info("数据库连接");
            final String valueM = cBox_m.getValue();
            final String dbUrlM = StringTrimUtil.trimCat(url_m.getText());
            final String dbPortM = StringTrimUtil.trimCat(port_m.getText());
            final String dbDbNameM = StringTrimUtil.trimCat(dbName_m.getText());
            final String dbUserNameM = StringTrimUtil.trimCat(userName_m.getText());
            final String dbPasswordM = StringTrimUtil.trimCat(password_m.getText());
            // database-one
            final String dataM = ValidateDate.validateDataBaseData(dbUrlM, dbPortM, dbDbNameM, dbUserNameM, dbPasswordM);
            if (StringUtils.isNotBlank(dataM)) {
                JavaFxViewUtil.alertCauseBy(Alert.AlertType.ERROR, "Database-One资源加载失败", dataM);
                return;
            }
            final String valueS = cBox_s.getValue();
            final String dbUrlS = StringTrimUtil.trimCat(url_s.getText());
            final String dbPortS = StringTrimUtil.trimCat(port_s.getText());
            final String dbDbNameS = StringTrimUtil.trimCat(dbName_s.getText());
            final String dbUserNameS = StringTrimUtil.trimCat(userName_s.getText());
            final String dbPasswordS = StringTrimUtil.trimCat(password_s.getText());
            // database-one
            final String dataS = ValidateDate.validateDataBaseData(dbUrlS, dbPortS, dbDbNameS, dbUserNameS, dbPasswordS);
            if (StringUtils.isNotBlank(dataS)) {
                JavaFxViewUtil.alertCauseBy(Alert.AlertType.ERROR, "Database-Two资源加载失败", dataS);
                return;
            }
            // 数据源匹配是否相同
            if (valueM.concat(dbUrlM).concat(SymbolUtil.TRANSVERSE_LINE).concat(dbDbNameM)
                    .equals(valueS.concat(dbUrlS).concat(SymbolUtil.TRANSVERSE_LINE).concat(dbDbNameS))) {
                JavaFxViewUtil.alertMessage(Alert.AlertType.ERROR, "相同数据库无法进行匹配");
                return;
            }

            // 连接数据库
            ConnectionService connectionService = SpringUtil.getBean(ConnectionService.class);
            DataBaseVo buildM = DataBaseVo.builder()
                    .url(dbUrlM).port(dbPortM).dbName(dbDbNameM)
                    .userName(dbUserNameM).password(dbPasswordM)
                    .build();
            buildM.setType(valueM);
            boolean connectionM = connectionService.connection(buildM);
            DataBaseVo buildS = DataBaseVo.builder()
                    .url(dbUrlS).port(dbPortS).dbName(dbDbNameS)
                    .userName(dbUserNameS).password(dbPasswordS)
                    .build();
            buildS.setType(valueS);
            boolean connectionS = connectionService.connection(buildS);
            if (connectionM && connectionS) {
                JavaFxViewUtil.alertMessage(Alert.AlertType.INFORMATION, "资源加载成功");
                JavaFxViewUtil.windowToSkip(Boolean.TRUE, "/view/FieldMatchingView.fxml", "convert", actionEvent);
            } else {
                JavaFxViewUtil.alertMessage(Alert.AlertType.INFORMATION, "资源加载失败");
            }
        } catch (Exception e) {
            log.error("数据库连接异常", e);
            JavaFxViewUtil.alertCauseBy(Alert.AlertType.ERROR, "资源加载失败", e.getMessage());
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> collect = Arrays.stream(DataBasesEnum.values())
                .map(DataBasesEnum::getName).collect(Collectors.toList())
                .stream().filter(x -> !DataBasesEnum.OTHER.getName()
                        .equals(x)).collect(Collectors.toList());
        // 初始选择框值
        cBox_m.getItems().addAll(collect);
        // 初始选择框值
        cBox_s.getItems().addAll(collect);
        //默认选择第一个
        cBox_m.getSelectionModel().selectFirst();
        //默认选择第一个
        cBox_s.getSelectionModel().selectFirst();
    }
}
