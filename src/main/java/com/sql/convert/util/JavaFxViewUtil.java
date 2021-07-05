package com.sql.convert.util;

import com.sql.convert.ConvertApplication;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

@Slf4j
public class JavaFxViewUtil {

    /**
     * <p>@description : 消息提示语 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/4/24 13:52 </p>
     *
     * @param alertType 提示类型
     * @param message   提示标题
     **/
    public static void alertMessage(Alert.AlertType alertType,
                                    final String message) {
        if (Objects.isNull(alertType) || StringUtils.isBlank(message)) {
            log.error("请输入要提示的内容");
            return;
        }
        Alert alert = new Alert(alertType);
        alert.setHeight(60L);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    /**
     * <p>@description : 消息提示语 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/4/24 13:52 </p>
     *
     * @param alertType 提示类型
     * @param message   提示标题
     * @param causeBy   提示类容
     **/
    public static void alertCauseBy(Alert.AlertType alertType,
                                    final String message,
                                    final String causeBy) {
        if (Objects.isNull(alertType) || StringUtils.isBlank(message)) {
            log.error("请输入要提示的内容");
            return;
        }
        Alert alert = new Alert(alertType);
        alert.setHeight(60L);
        alert.setHeaderText(message);
        if (StringUtils.isNotBlank(causeBy)) {
            alert.setContentText(causeBy);
        }
        alert.showAndWait();
    }

    /**
     * <p>@description : 跳转窗口 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/4/24 10:48 </p>
     *
     * @param flag      true->关闭之前视图;false->不关闭
     * @param fxml      视图路径
     * @param titleName 窗口名称
     * @param event     当前视图对象
     **/
    public static Initializable windowToSkip(final boolean flag,
                                             final String fxml,
                                             final String titleName,
                                             ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        InputStream in = ConvertApplication.class.getResourceAsStream(fxml);
        loader.setLocation(ConvertApplication.class.getResource(fxml));
        try {
            Stage stage = new Stage();
            if (flag) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                // stage.hide(); // 隐藏
                stage.close(); // 关闭
            }
            stage.setScene(new Scene(loader.load(in)));
            stage.sizeToScene();
            stage.setTitle(titleName);
            // 页面不可被拉伸
            stage.setResizable(false);
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    log.info("子窗口关闭");
                }
            });
            stage.show();
        } catch (Exception e) {
            log.error(Level.SEVERE.getName(), "页面加载异常！");
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return (Initializable) loader.getController();
    }

    /**
     * <p>@description : 跳转并传参 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/5/4 22:43 </p>
     *
     * @param xmlUrl view 地址
     * @param title  view 名称
     * @param chang  窗口是否可变
     * @param map    参数Map   目标试图通过AnchorPane对象获取
     **/
    public static void windowToSkipAndPassingParameters(final String xmlUrl,
                                                        final String title,
                                                        final boolean chang,
                                                        Map<String, String> map) {
        try {
            // 传递当前kettle类型
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(ConvertApplication.class.getResource(xmlUrl));
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            scene.setUserData(map);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setResizable(chang);
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    log.info("子窗口关闭");
                    // 清除kettleId
                }
            });
            stage.show();
        } catch (IOException e) {
            log.error(Level.SEVERE.getName(), "页面加载异常！");
        }
    }

    /**
     * <p>@description : 关闭当前窗口 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/4/27 2:39 </p>
     *
     * @param event 当前页面对象
     **/
    public static void closeWindows(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
