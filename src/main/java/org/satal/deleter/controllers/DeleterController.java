package org.satal.deleter.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import org.satal.deleter.model.CacheCatalog;
import org.satal.deleter.model.Interval;
import org.satal.deleter.model.Row;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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
    public TextField searchField;
    public ImageView cacheIcon;
    public ImageView loadIcon;


    private File currentDirectory;
    private Date currentDate;
    private DirectoryChooser directoryChooser;
    private DirectoryChooser removeDirectoryChooser;
    private ArrayList<CacheCatalog> cache;
    private Image loading;
    private Image loadingDone;
    private Image stopDns;
    private final Object connectDns = new Object();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Initialise common
        directoryChooser = new DirectoryChooser();
        removeDirectoryChooser = new DirectoryChooser();
        loading = new Image("org/satal/deleter/loading.gif", 28, 28, false, false);
        loadingDone = new Image("org/satal/deleter/icons8-проверено-48.png", 48, 48, false, false);
        stopDns = new Image("org/satal/deleter/icons8-отмена-48.png", 48, 48, false, false);

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
        intervalMenu.getSelectionModel().select(3);


        //Initialise tables
        initialTable(leftTable, leftData);
        initialTable(rightTable, rightData);
        leftTable.setOnKeyPressed((e) -> {
            if(e.getCode().toString().equals("RIGHT")){
                addButtonEvent();
            }
            if(e.getCode().toString().equals("ENTER")){
                leftTableEnter();
            }
        });
        rightTable.setOnKeyPressed((e) -> {
            if(e.getCode().toString().equals("LEFT")){
                delButtonEvent();
            }
            if(e.getCode().toString().equals("ENTER")){
                rightTableEnter();
            }
        });
        leftWindow.getChildren().add(leftTable);
        rightWindow.getChildren().add(rightTable);
        leftTable.setOnMouseClicked(e -> {
            leftTableClick(e);
            offDelFileButton();
        });
        rightTable.setOnMouseClicked(e -> {
            rightTableClick(e);
            offAddFileButton();
        });
        cachingPrograms();
        checkingDNS();

        // удобный сайт с иконками https://icons8.ru/icon/set/%D0%BF%D0%B5%D1%80%D0%B5%D0%BC%D0%B5%D1%81%D1%82%D0%B8%D1%82%D1%8C-%D0%B2-%D1%81%D0%BF%D0%B8%D1%81%D0%BE%D0%BA/fluency

    }

    public void cachingPrograms(){
        new Thread(() -> {
            if (cache == null){
                cacheIcon.setImage(stopDns);
            }
            try {
                synchronized (connectDns){
                    connectDns.wait();
                    cacheIcon.setImage(loading);
                    File temp = new File("\\\\Hp\\dnc");
                    if(temp.exists() && temp.isDirectory()) {
                        currentDirectory = temp;
                        setDirectoryLabel(currentDirectory.getPath());
                        directoryChooser.setInitialDirectory(currentDirectory);
                        ArrayList<CacheCatalog> tempCache = new ArrayList<>();
                        File[] pages = temp.listFiles();
                        if (pages != null && pages.length > 0){
                            for (File page : pages) {
                                if (page.isDirectory()){
                                    tempCache.add(new CacheCatalog(page.getName(), page.listFiles()));
                                }
                            }
                        }
                        cache = new ArrayList<>(tempCache);
                        cacheIcon.setImage(loadingDone);
                    } else {
                        cacheIcon.setImage(stopDns);
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void keyTyped(KeyEvent keyEvent) {

    }

    public void stopDns(){
        cache = null;
        currentDirectory = null;
        directoryChooser.setInitialDirectory(null);
        cachingPrograms();
    }

    public void checkingDNS(){
        new Thread(() -> {
            while (true){
                File temp = new File("\\\\Hp\\dnc");
                if (temp.exists() && temp.isDirectory()){
                    synchronized (connectDns){
                        connectDns.notify();
                    }
                }
//                connectDns = temp.exists() && temp.isDirectory();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void showFile(File file){
        try {
            new ProcessBuilder("explorer", String.format("/open,%s", file.getPath())).start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
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
        table.getColumns().add(dirColumn);
        table.getColumns().add(nameCol);
//        table.getColumns().add(oCol);
//        table.getColumns().add(programCol);
        table.getColumns().add(dateCol);
        table.getColumns().add(oldCol);
    }

    private void setDirectoryLabel(String str){
        directoryLabel.setText(str);
    }

    public void findCatalog(MouseEvent mouseEvent) {
        try{
            File temp = directoryChooser.showDialog(HomeWindow.getScene().getWindow());
            if (temp != null){
                loadIcon.setImage(loading);
                searchField.setText("");
                currentDirectory = temp;
                setDirectoryLabel(temp.getPath());
                removeDirectoryChooser.setInitialDirectory(temp);
                if (cache != null){
                    sortFiles(cache.stream().filter(c -> c.getDir().equals(temp.getName())).map(CacheCatalog::getList).findFirst().orElse(Objects.requireNonNull(temp.listFiles())));
                } else {
                    sortFiles(Objects.requireNonNull(temp.listFiles()));
                }
            }
        } catch (RuntimeException | IOException e){
            stopDns();
            findCatalog(mouseEvent);
        }

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

    public void resortFiles(){
        try {
            if (currentDirectory != null){
                if (cache != null){
                    sortFiles(cache.stream().filter(c -> c.getDir().equals(currentDirectory.getName())).map(CacheCatalog::getList).findFirst().orElse(Objects.requireNonNull(currentDirectory.listFiles())));
                } else {
                    sortFiles(Objects.requireNonNull(currentDirectory.listFiles()));
                }
            } else {
                leftData.clear();
                rightData.clear();
            }
        } catch (IOException | RuntimeException e){
            stopDns();
        }
    }

    public void intervalMenuAction(ActionEvent actionEvent) throws IOException {
        resortFiles();
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

    public void leftTableClick(MouseEvent event) {
        Row row = leftTable.getSelectionModel().getSelectedItem();
        if(row != null){
            if(event.getClickCount() == 2){
                showFile(row.getFile());
            }
            addFileButton.setDisable(false);
        }
    }
    public void leftTableEnter() {
        Row row = leftTable.getSelectionModel().getSelectedItem();
        if(row != null){
            showFile(row.getFile());
        }
    }

    public void rightTableEnter() {
        Row row = rightTable.getSelectionModel().getSelectedItem();
        if(row != null){
            showFile(row.getFile());
        }
    }

    public void rightTableClick(MouseEvent event) {
        Row row = rightTable.getSelectionModel().getSelectedItem();
        if(row != null){
            if(event.getClickCount() == 2){
                showFile(row.getFile());
            }
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
                if(!f.delete()){
                    stopDns();
                }
            }
            rightData.clear();
            counter();
            cachingPrograms();
        }
    }

    public void moveToFolder(MouseEvent mouseEvent) {
        if (rightData.size() > 0){
            File choseRemoveCatalog = removeDirectoryChooser.showDialog(HomeWindow.getScene().getWindow());
            if (choseRemoveCatalog != null && rightData.size() > 0){
                for (Row r : rightData) {
                    File f = r.getFile();
                    if(f.renameTo(new File(choseRemoveCatalog + "/" + f.getName()))){
                        if(!f.delete()){
                            stopDns();
                        }
                    }
                }
                rightData.clear();
                counter();
                cachingPrograms();
            }
        }
    }

    private void addColumnTable(){
        leftTable.getColumns().get(0).setVisible(true);
        rightTable.getColumns().get(0).setVisible(true);
    }

    private void delColumnTable(){
        leftTable.getColumns().get(0).setVisible(false);
        rightTable.getColumns().get(0).setVisible(false);
    }

    public void onActionSearch() {
        try {
            if (searchField.getText().length() <= 2){
                delColumnTable();
                if(currentDirectory != null && currentDirectory.getName().equals("dnc")){
                    leftData.clear();
                    rightData.clear();
                } else {
                    resortFiles();
                }
            } else if (searchField.getText().length() > 2){
                loadIcon.setImage(loading);
                addColumnTable();
                ArrayList<File> temp = new ArrayList<>();
                String t = searchField.getText();
                if (cache != null){
                    for (CacheCatalog catalog : cache) {
                        Arrays.stream(catalog.getList()).filter(f -> f.getName().contains(t)).forEach(temp::add);
                    }
                } else if (currentDirectory != null){
                    Arrays.stream(Objects.requireNonNull(currentDirectory.listFiles())).filter(f -> f.getName().contains(t)).forEach(temp::add);
                }

                File[] f = new File[temp.size()];
                temp.toArray(f);
                sortFiles(f);
            }
        } catch (IOException e){
            stopDns();
        }
    }

    public void startCaching(MouseEvent event) {
        cachingPrograms();
    }
}
