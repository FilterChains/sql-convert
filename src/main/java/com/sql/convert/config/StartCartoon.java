package com.sql.convert.config;

import de.felixroske.jfxsupport.SplashScreen;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/4/23 23:14
 * @description : 工具启动动画设置
 */
public class StartCartoon extends SplashScreen {

    @Override
    public boolean visible() {
        // 是否开启启动动画
        return false;
    }

    @Override
    public String getImagePath() {
        // 启动图片路径
        return "";
    }

}
