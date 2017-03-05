package com.androideverde.deviceinfo;

/**
 * Created by jordi.bernabeu on 18/02/2017.
 */

public class RecyclerItem {

    private String mTitle;
    private String mContent;

    public RecyclerItem(String title, String content) {
        mTitle = title;
        mContent = content;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getContent() {
        return mContent;
    }

    public RecyclerItem setTitle(String title) {
        mTitle = title;
        return this;
    }

    public RecyclerItem setContent(String content) {
        mContent = content;
        return this;
    }
}
