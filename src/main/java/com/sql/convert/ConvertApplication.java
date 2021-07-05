package com.sql.convert;

import com.sql.convert.config.StartCartoon;
import com.sql.convert.springutil.EnableSpringUtil;
import com.sql.convert.util.BaseData;
import com.sql.convert.view.HomePageView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@Slf4j
@EnableSpringUtil
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ConvertApplication extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launch(ConvertApplication.class, HomePageView.class, new StartCartoon(), args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Convert");
        // 设置是否固定大小，true->不固定；false->固定
        primaryStage.setResizable(false);
        // 主舞台窗口关闭监听
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                log.info("监听到窗口关闭");
                BaseData.closeConnection();
            }
        });
        super.start(primaryStage);
    }
}
