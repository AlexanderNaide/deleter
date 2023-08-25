package org.satal.deleter.threads;

import org.satal.deleter.controllers.DeleterController;
import org.satal.deleter.model.CacheCatalog;

import java.io.File;
import java.util.ArrayList;

public class CacheThread {

    private Thread thread;
    DeleterController deleterController;
    final Object connectDns;

    public CacheThread(DeleterController deleterController, Object connectDns) {
        this.deleterController = deleterController;
        this.connectDns = connectDns;
        init();
    }

    private void init(){
        this.thread = new Thread(() -> {
            if (deleterController.cache == null){
                deleterController.cacheIcon.setImage(deleterController.stopDns);
            }
            try {
                synchronized (connectDns){
                    connectDns.wait();
                    deleterController.cacheIcon.setImage(deleterController.loading);
                    File temp = new File("\\\\Hp\\dnc");
                    if(temp.exists() && temp.isDirectory()) {
                        ArrayList<CacheCatalog> tempCache = new ArrayList<>();
                        File[] pages = temp.listFiles();
                        if (pages != null && pages.length > 0){
                            for (File page : pages) {
                                if (page.isDirectory()){
                                    tempCache.add(new CacheCatalog(page.getName(), page.listFiles()));
                                }
                            }
                        }
                        deleterController.cache = new ArrayList<>(tempCache);
                        deleterController.cacheIcon.setImage(deleterController.loadingDone);
                    } else {
                        deleterController.cacheIcon.setImage(deleterController.stopDns);
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void start() {
        thread.start();
        init();

    }

    public void shutdown(){
        thread.interrupt();
    }
}
