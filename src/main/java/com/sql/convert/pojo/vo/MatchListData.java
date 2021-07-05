package com.sql.convert.pojo.vo;

import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;

/**
 * @author : Wei.Lu
 * @version : 1.0
 * @date : 2021/6/7 21:08
 * @description : com.sql.convert.pojo.bo
 */
public class MatchListData implements Serializable {

    private final SimpleStringProperty dbOneField;

    private final SimpleStringProperty dbTwoField;

    private final SimpleStringProperty match;

    public MatchListData(String dbOneField, String dbTwoField, String match) {
        this.dbOneField = new SimpleStringProperty(dbOneField);
        this.dbTwoField = new SimpleStringProperty(dbTwoField);
        this.match = new SimpleStringProperty(match);
    }

    public String getDbOneField() {
        return dbOneField.get();
    }

    public SimpleStringProperty dbOneFieldProperty() {
        return dbOneField;
    }

    public void setDbOneField(String dbOneField) {
        this.dbOneField.set(dbOneField);
    }

    public String getDbTwoField() {
        return dbTwoField.get();
    }

    public SimpleStringProperty dbTwoFieldProperty() {
        return dbTwoField;
    }

    public void setDbTwoField(String dbTwoField) {
        this.dbTwoField.set(dbTwoField);
    }

    public String getMatch() {
        return match.get();
    }

    public SimpleStringProperty matchProperty() {
        return match;
    }

    public void setMatch(String match) {
        this.match.set(match);
    }
}
