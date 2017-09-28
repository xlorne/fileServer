package com.lorne.file.server.model;

/**
 * create by lorne on 2017/9/28
 */
public class ImageWH {


    public ImageWH(int w, int h) {
        this.w = w;
        this.h = h;
    }

    private int w;

    private int h;

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }
}
