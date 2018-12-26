package com.kobyakov.nixtesttask.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Product {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String imagePath;
    private boolean isSelected;
    private boolean isBought;
    private String date;

    public Product(String title, String imagePath, String date) {
        this.title = title;
        this.imagePath = imagePath;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isBought() {
        return isBought;
    }

    public void setBought(boolean bought) {
        isBought = bought;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @NonNull
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", isSelected=" + isSelected +
                ", isBought=" + isBought +
                ", date='" + date + '\'' +
                '}';
    }
}