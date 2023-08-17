package org.satal.deleter.models;

import javafx.beans.property.SimpleStringProperty;

import java.io.File;

public class FileWrapper {
    private File file;//holds the file object
    private SimpleStringProperty name;//a property for the name of the file

    public FileWrapper(File file){
        name = new SimpleStringProperty();
        name.set(file.getName());

        this.file = file;
    }

    //this method returns the property of the name and is used by the table to chose the value of
    //the column using it.
    public SimpleStringProperty nameProperty(){
        return name;
    }
    public String getPath(){
        return file.getPath();
    }


}
