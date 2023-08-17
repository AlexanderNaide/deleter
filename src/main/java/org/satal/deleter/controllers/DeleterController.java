package org.satal.deleter.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.satal.deleter.models.FileWrapper;
import org.satal.deleter.models.Row;
import org.satal.deleter.views.WindowViewer;
import org.satal.deleter.views.ico.icoCatalog.Large;
import org.satal.deleter.views.ico.icoCatalog.TileElement;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

//public class DeleterController extends WindowViewer implements Initializable {
public class DeleterController implements Initializable {
    public AnchorPane HomeWindow;
    public VBox VBoxHomeWindow;
    public HBox quickMenu;
    public VBox leftWindow;
    public VBox rightWindow;

    private final TableView<Row> table = new TableView<>();
    private final ObservableList<Row> data = FXCollections.observableArrayList();

    private DirectoryChooser directoryChooser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        directoryChooser = new DirectoryChooser();


        TableColumn<Row, String> firstNameCol = new TableColumn<>("Name");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Row, String> lastNameCol = new TableColumn<>("Directory");
        lastNameCol.setMinWidth(300);
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Row, String> date = new TableColumn<>("Дата");
        date.setMinWidth(100);
        date.setCellValueFactory(new PropertyValueFactory<>("date"));

        table.setItems(data);
        table.getColumns().addAll(firstNameCol, lastNameCol, date);

        leftWindow.getChildren().add(table);


    }

    public void keyTyped(KeyEvent keyEvent) {

    }

//    @FXML
//    private Label welcomeText;
//
//    @FXML
//    protected void onHelloButtonClick() {
//        welcomeText.setText("Welcome to JavaFX Application!");
//    }

    public void findCatalog(MouseEvent mouseEvent) throws IOException {
//        File dir = directoryChooser.showDialog(HomeWindow.getScene().getWindow());
        File dir = new File("C:\\Users\\GVoichuk\\Desktop\\WT150");




        ArrayList<Row> left = new ArrayList<>();
        ArrayList<Row> right = new ArrayList<>();
        ArrayList<FileWrapper> wrappers = new ArrayList<>();






        BasicFileAttributes atr;
        File[] ch = dir.listFiles();
        assert ch != null;
        for (File f : ch){
//            Path of = Path.of(dir.toURI());
            if(f.isFile()){
//                of.relativize(f.toPath());
//
//                right.add((of.relativize(f.toPath())).toFile());
//                Row row = new Row();
//                row.setName(f.getName());
//                row.setFile(f);
                atr = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
//                row.setDate(atr.creationTime());
//                left.add(row);

//                wrappers.add(new FileWrapper(f));
                data.add(new Row(f.getName(), f.getPath(), atr.creationTime().toMillis()));


//                System.out.println(f.getName());
//                System.out.println(f.getPath());


            }
        }
//        right.forEach(f -> System.out.println(f.getName()));
//        right.forEach(f -> leftWindow.getItems().add(f));

//        Path currentPath = dir.toPath();
//        List<Path> dd = Files.list(currentPath).toList();
//        Path tt = dd.get(0);

//        BasicFileAttributes atr = Files.readAttributes(tt, BasicFileAttributes.class);
//        System.out.println(atr.creationTime());
//        System.out.println(atr.lastAccessTime());
//        System.out.println(atr.lastModifiedTime());

//        TableView<Row> table = new TableView<>();

//        TableColumn<Row, String> column1 = new TableColumn<>("Имя");
//        TableColumn<Row, Long> column2 = new TableColumn<>("Дата добавления");


//        column1.setCellValueFactory(new PropertyValueFactory<Row, String>("name"));
//        column2.setCellValueFactory(new PropertyValueFactory<Row, Long>("date"));


//        column2.setSortType(TableColumn.SortType.DESCENDING);
//        column2.setSortable(false);

//        ObservableList<Row> list = FXCollections.observableList(left);
//        ObservableList<FileWrapper> list = FXCollections.observableList(wrappers);
//        fileView.setItems(list);


//        table.getColumns().addAll(List.of(column1, column2));
//        table.setItems(list);
//        leftWindow.getChildren().add(table);




//        https://stackoverflow.com/questions/47484280/format-of-date-in-the-javafx-tableview







    }

    public static class Row {

        private final SimpleStringProperty firstName;
        private final SimpleStringProperty lastName;
        private final SimpleLongProperty date;

        private Row(String fName, String lName, Long date) {
            this.firstName = new SimpleStringProperty(fName);
            this.lastName = new SimpleStringProperty(lName);
            this.date = new SimpleLongProperty(date);
        }

        public String getFirstName() {
            return firstName.get();
        }

        public SimpleStringProperty firstNameProperty() {
            return firstName;
        }

        public String getLastName() {
            return lastName.get();
        }

        public SimpleStringProperty lastNameProperty() {
            return lastName;
        }

        public long getDate() {
            return date.get();
        }

        public SimpleLongProperty dateProperty() {
            return date;

        }
    }
}
