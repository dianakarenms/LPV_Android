package com.clickaboom.letrasparavolar.Models;

import java.util.UUID;

/**
 * Created by Karencita on 13/05/2017.
 */

public class Book {
    private String title;
    private String subtitle;
    private int image;
    private UUID mId;

    public Book() {
        // Generate unique identifier
        mId = UUID.randomUUID();
    }

    public Book(String title, String subtitle, int image) {
        // Generate unique identifier
        mId = UUID.randomUUID();
        this.title = title;
        this.subtitle = subtitle;
        this.image = image;
    }
    public UUID getmId() {
        return mId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
