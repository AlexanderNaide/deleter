package org.satal.deleter.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class DeleterController implements Initializable {

    private static Long millInDay = 86400000L;
    public AnchorPane HomeWindow;
    public VBox VBoxHomeWindow;
    public Pane leftWindow;
    public Pane rightWindow;
    public ComboBox<Interval> intervalMenu;
    public Label directoryLabel;
    public Label countLeft;
    public Label countTotal;
    public Label countRight;
    public Button addFileButton;
    public Button delFileButton;


    private final TableView<Row> leftTable = new TableView<>();
    private final TableView<Row> rightTable = new TableView<>();
    private final ObservableList<Row> leftData = FXCollections.observableArrayList();
    private final ObservableList<Row> rightData = FXCollections.observableArrayList();
    private final ObservableList<Interval> intervals = FXCollections.observableArrayList();


    private File currentDirectory;
    private File[] currentDirectoryFiles;
    private Date currentDate;
    private DirectoryChooser directoryChooser;
    private DirectoryChooser removeDirectoryChooser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Initialise common
        directoryChooser = new DirectoryChooser();
        removeDirectoryChooser = new DirectoryChooser();

        File temp = new File("\\\\Hp\\dnc");
        if(temp.exists() && temp.isDirectory()) {
            currentDirectory = temp;
            setDirectoryLabel(currentDirectory.getPath());
            directoryChooser.setInitialDirectory(currentDirectory);
            removeDirectoryChooser.setInitialDirectory(currentDirectory);
        }
        currentDate = new Date();

        //Initialise interval
        initialiseIntervals();
        intervalMenu.setItems(intervals);
        intervalMenu.getSelectionModel().select(2);


        //Initialise tables
        initialTable(leftTable, leftData);
        initialTable(rightTable, rightData);
        leftTable.setOnKeyPressed((e) -> {
            if(e.getCode().toString().equals("RIGHT")){
                addButtonEvent();
            }
        });
        rightTable.setOnKeyPressed((e) -> {
            if(e.getCode().toString().equals("LEFT")){
                delButtonEvent();
            }
        });
        leftWindow.getChildren().add(leftTable);
        rightWindow.getChildren().add(rightTable);
        leftTable.setOnMouseClicked(e -> {
            leftTableClick();
            offDelFileButton();
        });
        rightTable.setOnMouseClicked(e -> {
            rightTableClick();
            offAddFileButton();
        });

        // удобный сайт с иконками https://icons8.ru/icon/set/%D0%BF%D0%B5%D1%80%D0%B5%D0%BC%D0%B5%D1%81%D1%82%D0%B8%D1%82%D1%8C-%D0%B2-%D1%81%D0%BF%D0%B8%D1%81%D0%BE%D0%BA/fluency

    }

    public void keyTyped(KeyEvent keyEvent) {

    }

    public void initialiseIntervals(){
        LocalDateTime dt = LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());
        Interval int3months = new Interval(Date.from(dt.minusMonths(3).atZone(ZoneId.systemDefault()).toInstant()), "3 месяца");
        Interval int1month = new Interval(Date.from(dt.minusMonths(1).atZone(ZoneId.systemDefault()).toInstant()), "1 месяц");
        Interval int2weeks = new Interval(Date.from(dt.minusWeeks(2).atZone(ZoneId.systemDefault()).toInstant()), "2 недели");
        Interval int1week = new Interval(Date.from(dt.minusWeeks(1).atZone(ZoneId.systemDefault()).toInstant()), "1 неделя");
        Interval int3days = new Interval(Date.from(dt.minusDays(3).atZone(ZoneId.systemDefault()).toInstant()), "3 дня");
        intervals.addAll(int3months, int1month, int2weeks, int1week, int3days);
    }

    public void initialTable(TableView<Row> table, ObservableList<Row> data){
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(720);
        table.setPrefWidth(450);

        TableColumn<Row, String> nameCol = new TableColumn<>("Имя файла");
        nameCol.setMinWidth(218);
        nameCol.setPrefWidth(218);
        nameCol.setCellValueFactory(new PropertyValueFactory<Row, String>("name"));
//        TableColumn<Row2, String> oCol = new TableColumn<>("Программа (кратко)");
//        oCol.setCellValueFactory(new PropertyValueFactory<Row2, String>("oName"));
//        TableColumn<Row2, String> programCol = new TableColumn<>("Имя программы");
//        programCol.setCellValueFactory(new PropertyValueFactory<Row2, String>("programName"));
        TableColumn<Row, Date> dateCol = new TableColumn<>("Дата");
        dateCol.setMinWidth(115);
        dateCol.setPrefWidth(115);
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
        table.getColumns().add(nameCol);
//        table.getColumns().add(oCol);
//        table.getColumns().add(programCol);
        table.getColumns().add(dateCol);
        table.getColumns().add(oldCol);
    }

    private void setDirectoryLabel(String str){
        directoryLabel.setText(str);
    }

    public void findCatalog(MouseEvent mouseEvent) throws IOException {
        File temp = directoryChooser.showDialog(HomeWindow.getScene().getWindow());
        if (temp != null){
            currentDirectory = temp;
            setDirectoryLabel(currentDirectory.getPath());
            currentDirectoryFiles = currentDirectory.listFiles();
            removeDirectoryChooser.setInitialDirectory(currentDirectory);
            sortFiles();
        }
    }

    public void sortFiles() throws IOException {
        if (currentDirectoryFiles.length != 0){
            leftData.clear();
            rightData.clear();
            BasicFileAttributes atr;
            for (File f : currentDirectoryFiles){
                if(f.isFile()){
                    atr = Files.readAttributes(f.toPath(), BasicFileAttributes.class);

                    Row r = new Row();
                    r.setName(f.getName());
                    long oldMillis = atr.creationTime().toMillis();
                    int oldDay = (int) ((currentDate.getTime() - oldMillis) / millInDay);
                    Date fileDate = new Date(oldMillis);
                    r.setDate(fileDate);
                    r.setOld("" + oldDay + " " + getDay(oldDay));
                    r.setFile(f);
                    if(fileDate.before(intervalMenu.getSelectionModel().getSelectedItem().getDate())){
                        rightData.add(r);
                    } else {
                        leftData.add(r);
                    }
                }
            }
            counter();
        }
    }

    public void counter(){
        countLeft.setText(leftData.size() + " файлов.");
        countTotal.setText("Всего: " + (leftData.size() + rightData.size()) + " файлов.");
        countRight.setText(rightData.size() + " файлов.");
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

    public void intervalMenuAction(ActionEvent actionEvent) throws IOException {
        sortFiles();
    }

    public void readFile(){
        //                StringBuilder program = new StringBuilder();
/*                try (BufferedReader br = new BufferedReader(new FileReader(f))){
                    boolean oNameFlag = false;
                    boolean programFlag = false;
                    while (br.ready()){
                        String s = br.readLine();
                        if (s.length() > 1){
                            if (s.charAt(0) == 'O'){
                                String[] str = s.split(" ");
                                r.setoName(str[0]);
                                oNameFlag = true;
                                if (str.length > 1){
                                    for (String s1 : str) {
                                        String s2 = s1.strip();
                                        if(s2.charAt(0) == '('){
                                            int n = s2.indexOf(')');
                                            if (n > 0){
                                                r.setProgramName(s2.substring(1, n));
                                                programFlag = true;
                                            }
                                        }
                                    }
                                }

                            }
                            if (oNameFlag && s.charAt(0) == '('){
                                String s1 = s.strip();
                                if(s1.charAt(0) == '('){
                                    int n = s1.indexOf(')');
                                    if (n > 0){
                                        r.setProgramName(s1.substring(1, n));
                                        programFlag = true;
                                    }
                                }
                            }
                        }
                        if (oNameFlag && programFlag){
                            break;
                        }
                    }
                }*/


/*                char c;
                boolean flag = false;
                try (FileReader fr = new FileReader(f)){
                    while (fr.ready()){
                        c = (char) fr.read();
                        if (c == '(' && !flag){
                            flag = true;
                        } else if (c == ')' && flag) {
                            break;
                        } else if (flag) {
                            program.append(c);
                        }
                    }
                }*/
//                r.setProgramName(program.toString());
    }

    public void leftTableClick() {
        Row row = leftTable.getSelectionModel().getSelectedItem();
        if(row != null){
            addFileButton.setDisable(false);
        }
    }

    public void rightTableClick() {
        Row row = rightTable.getSelectionModel().getSelectedItem();
        if(row != null){
            delFileButton.setDisable(false);
        }
    }

    public void offAddFileButton(){
        addFileButton.setDisable(true);
    }

    public void offDelFileButton(){
        delFileButton.setDisable(true);
    }

    public void addButtonEvent() {
        Row row = leftTable.getSelectionModel().getSelectedItem();
        leftTable.requestFocus();
        if(row != null){
            rightData.add(row);
            leftData.remove(row);
            counter();
        }
    }

    public void delButtonEvent() {
        Row row = rightTable.getSelectionModel().getSelectedItem();
        rightTable.requestFocus();
        if(row != null){
            leftData.add(row);
            rightData.remove(row);
            counter();
        }
    }

    public void delete(MouseEvent mouseEvent) {
        if (rightData.size() > 0){
            for (Row r : rightData) {
                File f = r.getFile();
                f.delete();
            }
            rightData.clear();
            counter();
        }
    }

    public void moveToFolder(MouseEvent mouseEvent) {
        if (rightData.size() > 0){
            File choseRemoveCatalog = removeDirectoryChooser.showDialog(HomeWindow.getScene().getWindow());
            if (choseRemoveCatalog != null && rightData.size() > 0){
                for (Row r : rightData) {
                    File f = r.getFile();
                    if(f.renameTo(new File(choseRemoveCatalog + "/" + f.getName()))){
                        f.delete();
                    }
                }
                rightData.clear();
                counter();
            }
        }
    }

    public static class Row {
        private String name;
        private File file;
        private String oName;
        private String programName;
        private Date date;
        private String old;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getDate() {

            return date;
        }

        public String getoName() {
            return oName;
        }

        public void setoName(String oName) {
            this.oName = oName;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getProgramName() {
            return programName;
        }

        public void setProgramName(String programName) {
            this.programName = programName;
        }

        public String getOld() {
            return old;
        }

        public void setOld(String old) {
            this.old = old;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }
    }

    public static class Interval{

        private Date date;
        private String interval;

        public Interval(Date date, String interval) {
            this.date = date;
            this.interval = interval;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getInterval() {
            return interval;
        }

        public void setInterval(String interval) {
            this.interval = interval;
        }

        @Override
        public String toString()  {
            return this.interval;
        }
    }
}
