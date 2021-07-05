package com.sql.convert.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sql.convert.config.CacheData;
import com.sql.convert.enums.DataBasesEnum;
import com.sql.convert.enums.MysqlDataFieldsEnum;
import com.sql.convert.pojo.bo.DataBase;
import com.sql.convert.pojo.bo.MatchData;
import com.sql.convert.pojo.bo.SqlBo;
import com.sql.convert.pojo.vo.DataBaseVo;
import com.sql.convert.pojo.vo.MatchListData;
import com.sql.convert.util.BaseData;
import com.sql.convert.util.JavaFxViewUtil;
import com.sql.convert.util.StringTrimUtil;
import com.sql.convert.util.SymbolUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/6/5 18:55
 * @description : com.sql.convert.controller
 */
@Slf4j
@FXMLController
public class FieldMatchingViewController implements Initializable {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button search;
    @FXML
    private Button export;
    @FXML
    private Button logOut;
    @FXML
    private PieChart pieChart;
    @FXML
    private Label databaseOne;
    @FXML
    private Label databaseTwo;
    @FXML
    private TableView<MatchListData> matchList;
    @FXML
    private TableColumn<MatchListData, String> dbOne;
    @FXML
    private TableColumn<MatchListData, String> dbTwo;
    @FXML
    private TableColumn<MatchListData, String> dbMatch;
    @FXML
    private TextField searchText;

    private final DecimalFormat DF = new DecimalFormat("#.##%");


    /**
     * <p>@description : 退出 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/5 19:07 </p>
     *
     * @param actionEvent 当前画板事件
     **/
    public void logOut(ActionEvent actionEvent) {
        JavaFxViewUtil.windowToSkip(Boolean.TRUE, "/view/FieldMatchingLoginView.fxml", "convert", actionEvent);
    }

    /**
     * <p>@description : 导出 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/5 19:07 </p>
     *
     * @param actionEvent 当前画板事件
     **/
    public void export(ActionEvent actionEvent) {
        JavaFxViewUtil.windowToSkip(Boolean.FALSE, "/view/FieldMatchingExcelView.fxml", "convert", actionEvent);
    }

    /**
     * <p>@description : 搜索 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/5 19:07 </p>
     *
     * @param actionEvent 当前画板事件
     **/
    public void search(ActionEvent actionEvent) {
        final String text = StringTrimUtil.trimCat(searchText.getText());
        // 从缓存中获取匹配结果
        List<MatchData> cacheData = CacheData.getSearchCacheData();
        if (StringUtils.isBlank(text) || CollectionUtils.isEmpty(cacheData)) {
            matchList(cacheData);
            matchPieChart(cacheData);
            return;
        }
        // 筛选匹配结果
        List<MatchData> matchDataList = cacheData.stream()
                .peek(x -> {
                    x.setMatch(DF.format(x.getPercentage()));
                })
                .filter(x -> x.getDbOneTableName().contains(text) ||
                        x.getDbTwoTableName().contains(text) ||
                        x.getMatch().contains(text))
                .collect(Collectors.toList());
        // 匹配列表
        matchList(matchDataList);
        // 匹配率饼图
        matchPieChart(matchDataList);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 初始化信息
        List<DataBase> collBaseData = BaseData.getCollBaseData();
        if (SymbolUtil.LEN > collBaseData.size()) {
            JavaFxViewUtil.alertMessage(Alert.AlertType.INFORMATION, "资源加载失败,请重新登录");
            return;
        }
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1), Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
        try {
            CompletableFuture<Map<String, Map<String, List<SqlBo>>>> futureOne = CompletableFuture.supplyAsync(() -> {
                DataBase dataOne = collBaseData.get(0);
                DataBaseVo dataBaseVo = dataOne.getDataBaseVo();
                String text = "Database-One: ".concat(dataBaseVo.getUrl())
                        .concat(" [")
                        .concat(dataBaseVo.getDbName())
                        .concat("]");
                databaseOne.setText(text);
                return getDatabaseMsg(dataOne);
            }, executor);
            CompletableFuture<Map<String, Map<String, List<SqlBo>>>> futureTwo = CompletableFuture.supplyAsync(() -> {
                DataBase dataTwo = collBaseData.get(1);
                DataBaseVo dataBaseVo = dataTwo.getDataBaseVo();
                String text = "Database-Two: ".concat(dataBaseVo.getUrl())
                        .concat(" [")
                        .concat(dataBaseVo.getDbName())
                        .concat("]");
                databaseTwo.setText(text);
                return getDatabaseMsg(dataTwo);
            }, executor);
            CompletableFuture<Void> allOf = CompletableFuture.allOf(futureOne, futureTwo);
            allOf.get();
            Map<String, Map<String, List<SqlBo>>> oneMap = futureOne.get();
            Map<String, Map<String, List<SqlBo>>> twoMap = futureTwo.get();
            // 将读取结果存入缓存中
            saveDate(oneMap, twoMap);
            // 数据库字段匹配
            Map<String, List<SqlBo>> dbOneMap = oneMap.get(StringTrimUtil.getConcatMapKey(collBaseData.get(0).getDataBaseVo()));
            Map<String, List<SqlBo>> dbTwoMap = twoMap.get(StringTrimUtil.getConcatMapKey(collBaseData.get(1).getDataBaseVo()));
            // 计算匹配度
            List<MatchData> list = getMatchData(dbOneMap, dbTwoMap);
            // 匹配率饼图
            matchPieChart(list);
            // 匹配列表
            matchList(list);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            executor.shutdownNow();
        } finally {
            executor.shutdown();
        }
    }

    /**
     * <p>@description : 将读取结果存入缓存中 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/8 20:13 </p>
     *
     * @param oneMap 数据库One
     * @param twoMap 数据库Two
     **/
    private void saveDate(Map<String, Map<String, List<SqlBo>>> oneMap, Map<String, Map<String, List<SqlBo>>> twoMap) {
        Map<String, Map<String, List<SqlBo>>> hashMap = new LinkedHashMap<>(2);
        hashMap.putAll(oneMap);
        hashMap.putAll(twoMap);
        CacheData.setCacheDate(hashMap);
    }

    /**
     * <p>@description : 匹配率饼图 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/7 22:57 </p>
     *
     * @param list 设置匹配列表
     **/
    private void matchPieChart(List<MatchData> list) {
        long notMatch = list.stream().filter(x -> x.getPercentage().equals(BigDecimal.ZERO)).count();
        long match = list.stream().filter(x -> x.getPercentage().equals(BigDecimal.ONE)).count();
        long mh = list.stream().filter(x -> !x.getPercentage().equals(BigDecimal.ONE) && !x.getPercentage().equals(BigDecimal.ZERO)).count();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data(String.format("完全匹配 (%s)", match), match),
                new PieChart.Data(String.format("一般匹配 (%s)", mh), mh),
                new PieChart.Data(String.format("不匹配 (%s)", notMatch), notMatch));
        pieChart.setData(pieChartData);
    }

    /**
     * <p>@description : 设置匹配列表 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/7 22:50 </p>
     *
     * @param list 匹配集合
     **/
    private void matchList(List<MatchData> list) {
        ObservableList<MatchListData> mhList = FXCollections.observableArrayList();
        for (MatchData matchData : list) {
            mhList.add(new MatchListData(matchData.getDbOneTableName(),
                    matchData.getDbTwoTableName(),
                    DF.format(matchData.getPercentage())));
        }
        dbOne.setCellValueFactory(new PropertyValueFactory<>("dbOneField"));
        dbTwo.setCellValueFactory(new PropertyValueFactory<>("dbTwoField"));
        dbMatch.setCellValueFactory(new PropertyValueFactory<>("match"));
        matchList.setItems(mhList);
    }

    /**
     * <p>@description : 计算匹配率 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/7 22:34 </p>
     *
     * @param dbOneMap 匹配对象
     * @param dbTwoMap 匹配源
     * @return {@link List<MatchData>}
     **/
    private List<MatchData> getMatchData(Map<String, List<SqlBo>> dbOneMap, Map<String, List<SqlBo>> dbTwoMap) {
        List<MatchData> list = Lists.newLinkedList();
        Set<Map.Entry<String, List<SqlBo>>> entrySet = dbOneMap.entrySet();
        for (Map.Entry<String, List<SqlBo>> entry : entrySet) {
            String key = entry.getKey();
            List<SqlBo> value = entry.getValue();
            // 表匹配不成功
            if (!dbTwoMap.containsKey(key)) {
                list.add(MatchData.builder()
                        .dbOneTableName(key)
                        .dbTwoTableName("暂无可匹配对象")
                        .percentage(BigDecimal.ZERO)
                        .build());
                continue;
            }
            List<SqlBo> val = dbTwoMap.get(key);
            List<String> fieldOneList = value.stream().map(SqlBo::getTableFieldName)
                    .collect(Collectors.toList());
            List<String> fieldTwoList = val.stream().map(SqlBo::getTableFieldName)
                    .collect(Collectors.toList());
            // 表完全匹配
            if (fieldTwoList.containsAll(fieldOneList)) {
                List<SqlBo> boList = val.stream().filter(x -> !fieldOneList.contains(x.getTableFieldName()) &&
                        SymbolUtil.NO.contains(x.getIsNullAble())).collect(Collectors.toList());
                list.add(MatchData.builder()
                        .dbOneTableName(key)
                        .dbTwoTableName(key)
                        .percentage(CollectionUtils.isNotEmpty(boList) ? BigDecimal.valueOf(0.99) : BigDecimal.ONE)
                        .build());
                continue;
            }
            // 表一般匹配
            int size = value.size();
            int i = 0;
            for (SqlBo sqlBo : value) {
                final String tableFieldName = sqlBo.getTableFieldName();
                if (fieldTwoList.contains(tableFieldName)) {
                    i++;
                }
            }
            BigDecimal bigDecimal = BigDecimal.valueOf(i);
            BigDecimal bigDecimal1 = BigDecimal.valueOf(size);
            // 去除小数点后面多余的零
            BigDecimal divide = bigDecimal.divide(bigDecimal1, 2,
                    BigDecimal.ROUND_DOWN).stripTrailingZeros();
            list.add(MatchData.builder()
                    .dbOneTableName(key)
                    .dbTwoTableName(key)
                    .percentage(divide)
                    .build());
        }
        if (MapUtils.isNotEmpty(dbTwoMap)) {
            Set<String> keySet = dbTwoMap.keySet();
            Set<String> set = dbOneMap.keySet();
            for (String s : keySet) {
                if (!set.contains(s)) {
                    list.add(MatchData.builder()
                            .dbOneTableName("暂无可匹配对象")
                            .dbTwoTableName(s)
                            .percentage(BigDecimal.ZERO)
                            .build());
                }
            }
        }
        // 将匹配结果存入本地缓存中(1小时过期)
        CacheData.setSearchCacheDate(list);
        return list;
    }

    /**
     * <p>@description : 获取数据库相关信息 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/7 20:07 </p>
     *
     * @param data 登录数据库相关信息
     * @return Map<String, Map < String, List < SqlBo>>> key：数据库URL-数据库名称; value:[key:数据库表名称；value:对应数据库表字段]
     **/
    private Map<String, Map<String, List<SqlBo>>> getDatabaseMsg(DataBase data) {
        Map<String, Map<String, List<SqlBo>>> hashMap = Maps.newLinkedHashMap();
        DataBaseVo dataBaseVo = data.getDataBaseVo();
        final String key = StringTrimUtil.getConcatMapKey(dataBaseVo);
        //获取数据库字段
        Connection connection = data.getConnection();
        Statement statement = null;
        DataBasesEnum dbType = dataBaseVo.getDbType();
        try {
            statement = connection.createStatement();
            final String sql = String.format(dbType.getSqlField(), dataBaseVo.getDbName());
            hashMap.put(key, getAssembleData(statement.executeQuery(sql), dbType));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(statement)) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return hashMap;
    }

    /**
     * <p>@description : 组装数据库数据 </p>
     * <p>@author : Wei.Lu</p>
     * <p>@date : 2021/6/7 20:06 </p>
     *
     * @param resultSet 数据库查询结果集
     * @param dbType    数据库类型
     * @return Map<String, List < SqlBo>> key:数据库表名称；value:对应数据库表字段
     **/
    private static Map<String, List<SqlBo>> getAssembleData(ResultSet resultSet, DataBasesEnum dbType) {
        try {
            if (Objects.isNull(resultSet)) {
                return Collections.emptyMap();
            }
            // 查询数据结果集筛选
            List<SqlBo> list = new ArrayList<>();
            while (resultSet.next()) {
                //取出列值
                final String tableName = resultSet.getString(MysqlDataFieldsEnum.TABLE_NAME.getFieldName());
                final String tableFieldName = resultSet.getString(MysqlDataFieldsEnum.COLUMN_NAME.getFieldName());
                final String sort = resultSet.getString(MysqlDataFieldsEnum.ORDINAL_POSITION.getFieldName());
                final String isNull = resultSet.getString(MysqlDataFieldsEnum.IS_NULLABLE.getFieldName());
                list.add(SqlBo.builder()
                        .tableName(tableName)
                        .tableFieldName(tableFieldName)
                        .sort(Integer.valueOf(sort))
                        .isNullAble(isNull)
                        .build());
            }
            resultSet.close();
            return list.stream().sorted(Comparator.comparing(SqlBo::getSort))
                    .collect(Collectors.groupingBy(SqlBo::getTableName, LinkedHashMap::new,
                            Collectors.toList()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }
}
