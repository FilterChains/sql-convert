package com.sql.convert.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/6/7 22:04
 * @description : com.sql.convert.util
 */
public class test {
    public static void main(String[] args) {
        List<String> qw = Lists.newArrayList("qw", "12", "123");
        List<String> qw1 = Lists.newArrayList("qw", "12");

        System.out.println(qw.containsAll(qw1));
        int i = 0;
        for (String s : qw1) {
            if (qw.contains(s)) {
                i++;
            }
        }
        BigDecimal bigDecimal = BigDecimal.valueOf(i);
        BigDecimal bigDecimal1 = BigDecimal.valueOf(qw1.size());
        BigDecimal divide = bigDecimal.divide(bigDecimal1, 2, BigDecimal.ROUND_DOWN);
        System.out.println("比率：" + divide.toPlainString());
        DecimalFormat df = new DecimalFormat("0.00%");
        System.out.println(df.format(BigDecimal.ZERO));
        System.out.println(df.format(BigDecimal.ONE));
        System.out.println(df.format(BigDecimal.valueOf(0.67)));
        // 写法1
        String fileName = "C:\\Users\\luwei\\Desktop\\" + System.currentTimeMillis() + ".xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        EasyExcel.write(fileName, DemoData.class).sheet("模板")
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()) // 自适应宽度
                .doWrite(data());
        System.out.println("wancheng");

        // 写法2
        // fileName = "C:\\Users\\luwei\\Desktop\\simpleWrite" + System.currentTimeMillis() + ".xlsx";
        // // 这里 需要指定写用哪个class去写
        // ExcelWriter excelWriter = EasyExcel.write(fileName, DemoData.class).build();
        // WriteSheet writeSheet = EasyExcel.writerSheet("模板").build();
        // excelWriter.write(data(), writeSheet);
        // 千万别忘记finish 会帮忙关闭流
        // excelWriter.finish();
    }

    private static List<DemoData> data() {
        List<DemoData> list = new ArrayList<DemoData>();
        for (int i = 0; i < 10; i++) {
            DemoData data = new DemoData();
            data.setString("字符串" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }
}
