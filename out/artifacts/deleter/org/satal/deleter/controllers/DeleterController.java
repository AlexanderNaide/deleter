package org.satal.deleter.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import org.satal.deleter.model.CacheCatalog;
import org.satal.deleter.model.Row;
import org.satal.deleter.threads.CacheThread;
import org.satal.deleter.threads.DNSThread;
import org.satal.deleter.view.DeleterView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class DeleterController extends DeleterView implements Initializable {

    public AnchorPane HomeWindow;
    public VBox VBoxHomeWindow;
    public Button addFileButton;
    public Button delFileButton;
    public TextField searchField;
    public File currentDirectory;
    public DirectoryChooser directoryChooser;
    public DirectoryChooser removeDirectoryChooser;
    public ArrayList<CacheCatalog> cache;
    private final Object connectDns = new Object();
    private CacheThread cacheThread;
    private DNSThread dnsThread;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        super.initialize(this);

        //Initialise common
        directoryChooser = new DirectoryChooser();
        removeDirectoryChooser = new DirectoryChooser();
        cacheThread = new CacheThread(this, connectDns);
        dnsThread = new DNSThread(connectDns);

        File temp = new File("\\\\Hp\\dnc");
        if(temp.exists() && temp.isDirectory()) {
            directoryChooser.setInitialDirectory(temp);
        }

        cachingPrograms();
        checkingDNS();

        // удобный сайт с иконками https://icons8.ru/icon/set/%D0%BF%D0%B5%D1%80%D0%B5%D0%BC%D0%B5%D1%81%D1%82%D0%B8%D1%82%D1%8C-%D0%B2-%D1%81%D0%BF%D0%B8%D1%81%D0%BE%D0%BA/fluency

    }

    public void cachingPrograms(){
        cacheThread.start();
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
        dnsThread.start();
    }

    public void showFile(File file){
        try {
            new ProcessBuilder("explorer", String.format("/open,%s", file.getPath())).start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
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
                if (cache != null && temp.getParentFile().getPath().equals("\\\\Hp\\dnc")){
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

    public void resortFiles(){
        try {
            if (currentDirectory != null){
                if (cache != null && currentDirectory.getName().equals("dnc")){
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

    public void intervalMenuAction(ActionEvent actionEvent){
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

    public void rightTableClick(MouseEvent event) {
        Row row = rightTable.getSelectionModel().getSelectedItem();
        if(row != null){
            if(event.getClickCount() == 2){
                showFile(row.getFile());
            }
            delFileButton.setDisable(false);
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
                    if(!f.renameTo(new File(choseRemoveCatalog + "/" + f.getName()))){
                        stopDns();
                    }
                }
                rightData.clear();
                counter();
                cachingPrograms();
            }
        }
    }

    public void onActionSearch() {
        try {
            if (searchField.getText().length() <= 2){
                delColumnTable();
                if(currentDirectory == null){
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
                if (currentDirectory == null && cache != null){
                    for (CacheCatalog catalog : cache) {
                        Arrays.stream(catalog.getList()).filter(f -> f.getName().contains(t)).forEach(temp::add);
                    }
                } else if (currentDirectory != null && currentDirectory.getName().equals("dnc") && cache != null) {
                    for (CacheCatalog catalog : cache) {
                        Arrays.stream(catalog.getList()).filter(f -> f.getName().contains(t)).forEach(temp::add);
                    }
                } else if (currentDirectory != null && !currentDirectory.getName().equals("dnc")){
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

    public void shutdown() {
        System.out.println("Exit");
        dnsThread.shutdown();
        cacheThread.shutdown();
    }
}
