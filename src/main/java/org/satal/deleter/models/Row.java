package org.satal.deleter.models;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.chart.PieChart;

import java.io.File;
import java.nio.file.attribute.FileTime;

public class Row {
    private File file;
    private SimpleStringProperty name;
    private SimpleLongProperty date;

    public Row() {
    }

    public File getFile(File f) {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public SimpleStringProperty getName() {
        return name;
    }

    public void setName(String name) {
        this.name = new SimpleStringProperty(name);
    }

    public SimpleLongProperty getDate() {
        return date;
    }

    public void setDate(FileTime date) {
        this.date = new SimpleLongProperty(date.toMillis());
    }
}
