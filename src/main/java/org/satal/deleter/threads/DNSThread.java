package org.satal.deleter.threads;

import java.io.File;

public class DNSThread {

    private final Thread thread;
    private boolean go;

    public DNSThread(Object connectDns) {
        go = true;
        this.thread = new Thread(() -> {
            while (go){
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
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    public void start(){
        thread.start();
    }

    public void shutdown(){
        go = false;
        thread.interrupt();
    }
}
