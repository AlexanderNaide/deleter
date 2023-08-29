package org.satal.deleter.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.satal.deleter.controllers.DeleterController;
import org.satal.deleter.model.Interval;
import org.satal.deleter.model.Row;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DeleterView {

    private static Long millInDay = 86400000L;
    public Pane leftWindow;
    public Pane rightWindow;
    public Label directoryLabel;
    public Label countLeft;
    public Label countTotal;
    public Label countRight;
    public ComboBox<Interval> intervalMenu;
    public ImageView cacheIcon;
    public ImageView loadIcon;
    private final ObservableList<Interval> intervals = FXCollections.observableArrayList();

    public Image loading;
    public Image loadingDone;
    public Image stopDns;

    protected final TableView<Row> leftTable = new TableView<>();
    protected final TableView<Row> rightTable = new TableView<>();
    protected final ObservableList<Row> leftData = FXCollections.observableArrayList();
    protected final ObservableList<Row> rightData = FXCollections.observableArrayList();
    private Date currentDate;
    private DeleterController deleterController;
    protected void initialize(DeleterController deleterController){

        this.deleterController = deleterController;

        loading = new Image("org/satal/deleter/loading.gif", 28, 28, false, false);
        loadingDone = new Image("org/satal/deleter/icons8-проверено-48.png", 48, 48, false, false);
        stopDns = new Image("org/satal/deleter/icons8-отмена-48.png", 48, 48, false, false);

        initialTable(leftTable, leftData);
        initialTable(rightTable, rightData);
        currentDate = new Date();
        leftTable.setOnKeyPressed((e) -> {
            if(e.getCode().toString().equals("RIGHT")){
                deleterController.addButtonEvent();
            }
            if(e.getCode().toString().equals("ENTER")){
                deleterController.leftTableEnter();
            }
        });
        rightTable.setOnKeyPressed((e) -> {
            if(e.getCode().toString().equals("LEFT")){
                deleterController.delButtonEvent();
            }
            if(e.getCode().toString().equals("ENTER")){
                deleterController.rightTableEnter();
            }
        });
        leftWindow.getChildren().add(leftTable);
        rightWindow.getChildren().add(rightTable);
        leftTable.setOnMouseClicked(e -> {
            deleterController.leftTableClick(e);
            deleterController.offDelFileButton();
        });
        rightTable.setOnMouseClicked(e -> {
            deleterController.rightTableClick(e);
            deleterController.offAddFileButton();
        });

        initialiseIntervals();
        intervalMenu.setItems(intervals);
        intervalMenu.getSelectionModel().select(3);

    }

    public void initialTable(TableView<Row> table, ObservableList<Row> data){
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(720);
        table.setPrefWidth(450);

        TableColumn<Row, String> dirColumn = new TableColumn<>("Папка");
        dirColumn.setMinWidth(60);
        dirColumn.setPrefWidth(60);
        dirColumn.setCellValueFactory(new PropertyValueFactory<Row, String>("dir"));
        dirColumn.setVisible(false);

        TableColumn<Row, String> nameCol = new TableColumn<>("Имя файла");
        nameCol.setMinWidth(155);
        nameCol.setPrefWidth(195);
        nameCol.setCellValueFactory(new PropertyValueFactory<Row, String>("name"));
//        TableColumn<Row2, String> oCol = new TableColumn<>("Программа (кратко)");
//        oCol.setCellValueFactory(new PropertyValueFactory<Row2, String>("oName"));
//        TableColumn<Row2, String> programCol = new TableColumn<>("Имя программы");
//        programCol.setCellValueFactory(new PropertyValueFactory<Row2, String>("programName"));
        TableColumn<Row, Date> dateCol = new TableColumn<>("Дата");
        dateCol.setMinWidth(120);
        dateCol.setPrefWidth(140);
        dateCol.setCellValueFactory(new PropertyValueFactory<Row, Date>("date"));
        dateCol.setCellFactory(column -> new TableCell<Row, Date>() {
                    private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                    @Override
                    protected void updateItem(Date item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty) {
                            setText(null);
                        }
                        else {
                            this.setText(format.format(item));
                        }
                    }
                }
        );

        TableColumn<Row, String> oldCol = new TableColumn<>("Возраст файла");
        oldCol.setMinWidth(115);
        oldCol.setPrefWidth(115);
        oldCol.setCellValueFactory(new PropertyValueFactory<Row, String>("old"));

        table.setItems(data);
        table.getColumns().add(dirColumn);
        table.getColumns().add(nameCol);
//        table.getColumns().add(oCol);
//        table.getColumns().add(programCol);
        table.getColumns().add(dateCol);
        table.getColumns().add(oldCol);
    }

    public void sortFiles(File[] list) throws IOException {
        leftData.clear();
        rightData.clear();
        List<File> fileList = Arrays.stream(list).filter(File::isFile).collect(Collectors.toList());
        if (fileList.size() > 0){
            BasicFileAttributes atr;
            for (File file : fileList) {
                atr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                Row row = new Row();
                row.setName(file.getName());
                long oldMillis = atr.creationTime().toMillis();
                int oldDay = (int) ((currentDate.getTime() - oldMillis) / millInDay);
                Date fileDate = new Date(oldMillis);
                row.setDate(fileDate);
                row.setOld("" + oldDay + " " + getDay(oldDay));
                row.setFile(file);
                if (leftTable.getColumns().get(0).isVisible()){
                    row.setDir(file.getParentFile().getName());
                }
                if(row.getDate().before(intervalMenu.getSelectionModel().getSelectedItem().getDate())){
                    rightData.add(row);
                } else {
                    leftData.add(row);
                }
            }
        }
        counter();
        loadIcon.setImage(null);
    }

    public void counter(){
        countLeft.setText(leftData.size() + " " + getFile(leftData.size()));
        countTotal.setText("Всего: " + (leftData.size() + rightData.size()) + " " + getFile(leftData.size() + rightData.size()));
        countRight.setText(rightData.size() + " " + getFile(rightData.size()));
    }

    public void initialiseIntervals(){
        LocalDateTime dt = LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());
        Interval int3months = new Interval(Date.from(dt.minusMonths(3).atZone(ZoneId.systemDefault()).toInstant()), "3 месяца");
        Interval int1month = new Interval(Date.from(dt.minusMonths(1).atZone(ZoneId.systemDefault()).toInstant()), "1 месяц");
        Interval int3weeks = new Interval(Date.from(dt.minusWeeks(3).atZone(ZoneId.systemDefault()).toInstant()), "3 недели");
        Interval int2weeks = new Interval(Date.from(dt.minusWeeks(2).atZone(ZoneId.systemDefault()).toInstant()), "2 недели");
        Interval int1week = new Interval(Date.from(dt.minusWeeks(1).atZone(ZoneId.systemDefault()).toInstant()), "1 неделя");
        Interval int3days = new Interval(Date.from(dt.minusDays(3).atZone(ZoneId.systemDefault()).toInstant()), "3 дня");
        intervals.addAll(int3months, int1month, int3weeks, int2weeks, int1week, int3days);
    }

    public void addColumnTable(){
        leftTable.getColumns().get(0).setVisible(true);
        rightTable.getColumns().get(0).setVisible(true);
    }

    public void delColumnTable(){
        leftTable.getColumns().get(0).setVisible(false);
        rightTable.getColumns().get(0).setVisible(false);
    }

    public String getDay(int d){
        if(d > 100000){
            d = d % 100000;
        }
        if(d > 10000){
            d = d % 10000;
        }
        if (d > 1000){
            d = d % 1000;
        }
        if (d > 100){
            d = d % 100;
        }
        if (d > 10 && d < 20){
            return "дней";
        } else {
            if (d > 10) {
                d = d % 10;
            }
            if (d == 1){
                return "день";
            } else if (d > 1 && d < 5) {
                return "дня";
            } else {
                return "дней";
            }
        }
    }

    public String getFile(int f){
        if(f > 100000){
            f = f % 100000;
        }
        if(f > 10000){
            f = f % 10000;
        }
        if (f > 1000){
            f = f % 1000;
        }
        if (f > 100){
            f = f % 100;
        }
        if (f > 10 && f < 20){
            return "файлов";
        } else {
            if (f > 10) {
                f = f % 10;
            }
            if (f == 1){
                return "файл";
            } else if (f > 1 && f < 5) {
                return "файла";
            } else {
                return "файлов";
            }
        }
    }
}
