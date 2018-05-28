package com.example.bumb.miser;

import java.util.Random;

public class Item {
    private String itemName;
    private String itemImage;
    private double itemPrice;
    private double itemPriceUpdated;
    private String itemURL;
    private boolean alarmOn;
    private int alarmPercentage;
    private boolean favorite;

    public Item() {}

    public Item(String itemName, String itemImage, double itemPrice, String itemURL, boolean alarmOn, int alarmPercentage) {
        this.itemName = itemName;
        this.itemImage = itemImage;

        this.itemPrice = Math.round(itemPrice * 100.0) / 100.0;
        this.itemPriceUpdated = itemPrice;

        this.itemURL = itemURL;

        this.favorite = false;

        if (alarmOn) {
            this.alarmOn = true;
            this.alarmPercentage = alarmPercentage;
        } else {
            this.alarmOn = false;
            this.alarmPercentage = 5;
        }
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = Math.round(itemPrice * 100.0) / 100.0;
    }

    public double getItemPriceUpdated() {
        return itemPriceUpdated;
    }

    public void setItemPriceUpdated(double itemPriceUpdated) {
        this.itemPriceUpdated = Math.round(itemPriceUpdated * 100.0) / 100.0;
    }

    public String getItemURL() {
        return itemURL;
    }

    public void setItemURL(String itemURL) {
        this.itemURL = itemURL;
    }

    public boolean isAlarmOn() {
        return alarmOn;
    }

    public void setAlarmOn(boolean alarmOn) {
        this.alarmOn = alarmOn;
    }

    public int getAlarmPercentage() {
        return alarmPercentage;
    }

    public void setAlarmPercentage(int alarmPercentage) {
        this.alarmPercentage = alarmPercentage;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public double getPriceChangeRatio()
    {
        double ratio = (itemPriceUpdated - itemPrice) / itemPrice * 100;
        return Math.round(ratio * 100.0) / 100.0; //To round only 2 digits
    }
}
