package org.satal.deleter.model;

import java.io.File;

public class CacheCatalog {
    private String dir;
    private File[] list;

    public CacheCatalog(String dir, File[] list) {
        this.dir = dir;
        this.list = list;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
    public File[] getList() {
        return list;
    }

    public void setList(File[] list) {
        this.list = list;
    }
}
