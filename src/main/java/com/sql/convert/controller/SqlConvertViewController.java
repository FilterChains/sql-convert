package com.sql.convert.controller;

import com.google.common.base.Splitter;
import com.sql.convert.enums.DataBasesEnum;
import com.sql.convert.pojo.bo.DataBase;
import com.sql.convert.pojo.vo.DataBaseVo;
import com.sql.convert.util.BaseData;
import com.sql.convert.util.ConvertSql;
import com.sql.convert.util.JavaFxViewUtil;
import com.sql.convert.util.StringTrimUtil;
import com.sql.convert.util.SymbolUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.StopWatch;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/5/19 21:17
 * @description : com.sql.convert.controller
 */
@Slf4j
@FXMLController
public class SqlConvertViewController implements Initializable {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextArea resource;
    @FXML
    private TextArea target;
    @FXML
    private Button convert;
    @FXML
    private Button exit;
    @FXML
    private Button create;
    @FXML
    private ChoiceBox<String> cBox;
    @FXML
    private Label dbType;
    @FXML
    private Label content;

    /**
     * <p>@description : 登出 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/5/23 17:10 </p>
     **/
    public void exit(ActionEvent actionEvent) {
        BaseData.closeConnection();
        JavaFxViewUtil.windowToSkip(Boolean.TRUE, "/view/SqlConvertLoginView.fxml", "convert", actionEvent);
    }

    /**
     * <p>@description : 执行创建语句 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/5/23 17:29 </p>
     **/
    public void create(ActionEvent actionEvent) {
        String text = target.getText();
        if (StringUtils.isBlank(text.replaceAll(SymbolUtil.CHARACTER_P, ""))) {
            JavaFxViewUtil.alertCauseBy(Alert.AlertType.ERROR, "创建失败", "暂无可执行的创建语句");
            return;
        }
        Connection connection = BaseData.getBaseData().getConnection();
        if (Objects.isNull(connection)) {
            JavaFxViewUtil.alertCauseBy(Alert.AlertType.ERROR, "创建失败", "暂无可用连接");
            return;
        }
        // 获取连接声明对象
        Statement statement = null;
        try {
            StopWatch started = new StopWatch();
            statement = connection.createStatement();
            List<String> list = Splitter.on(";").omitEmptyStrings()
                    .omitEmptyStrings().splitToList(text);
            started.start("开始创建");
            for (String s : list) {
                statement.executeLargeUpdate(s.concat(";"));
            }
            started.stop();
            content.setText("执行成功, 总影响: ".concat(String.valueOf(list.size()))
                    .concat("行 执行时间: ").concat(String.valueOf(started.getLastTaskTimeMillis())).concat("s"));
            JavaFxViewUtil.alertMessage(Alert.AlertType.INFORMATION, "创建成功");
        } catch (SQLException e) {
            e.printStackTrace();
            JavaFxViewUtil.alertCauseBy(Alert.AlertType.ERROR, "创建失败", e.getMessage());
        } finally {
            try {
                if (Objects.nonNull(statement)) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * <p>@description : sql语句转换 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/5/20 9:29 </p>
     **/
    public void convert(ActionEvent actionEvent) {
        log.info("开始sql转换");
        String text = resource.getText();
        if (StringUtils.isBlank(StringTrimUtil.trimCat(text))) {
            JavaFxViewUtil.alertCauseBy(Alert.AlertType.ERROR, "转换失败", "转换数据不能为空");
            return;
        }
        DataBase baseData = BaseData.getBaseData();
        if (Objects.isNull(baseData) || Objects.isNull(baseData.getDataBaseVo())) {
            JavaFxViewUtil.alertCauseBy(Alert.AlertType.ERROR, "转换失败", "数据库连接异常");
            return;
        }
        String value = cBox.getValue();
        DataBasesEnum dbType = baseData.getDataBaseVo().getDbType();
        if (DataBasesEnum.OTHER.equals(dbType)) {
            DataBaseVo build = DataBaseVo.builder().build();
            build.setType(value);
            dbType = build.getDbType();
        }
        target.setText(new ConvertSql(text).convert(dbType));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 自动换行设置
        resource.setWrapText(true);
        target.setWrapText(true);
        DataBasesEnum type = BaseData.getBaseData().getDataBaseVo().getDbType();
        List<String> collect = Arrays.stream(DataBasesEnum.values())
                .map(DataBasesEnum::getName).collect(Collectors.toList())
                .stream().filter(x -> !DataBasesEnum.OTHER.getName()
                        .equals(x)).collect(Collectors.toList());
        if (!DataBasesEnum.OTHER.equals(type)) {
            dbType.setDisable(Boolean.TRUE);
            cBox.setDisable(Boolean.TRUE);
            collect = collect.stream().filter(x -> type.getName().equals(x)).collect(Collectors.toList());
        } else {
            create.setDisable(Boolean.TRUE);
        }
        // 初始选择框值
        cBox.getItems().addAll(collect);
        //默认选择第一个
        cBox.getSelectionModel().selectFirst();
    }
}
