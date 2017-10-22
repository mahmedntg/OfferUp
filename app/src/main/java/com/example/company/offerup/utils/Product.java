package com.example.company.offerup.utils;

/**
 * Created by Mohamed Sayed on 10/17/2017.
 */

public class Product {
    private String name, price, image, desc, endDate, placeName,quantity,total;

    public Product() {
    }

    public Product(String name, String price, String image, String desc, String endDate, String placeName) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.desc = desc;
        this.endDate = endDate;
        this.placeName = placeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
