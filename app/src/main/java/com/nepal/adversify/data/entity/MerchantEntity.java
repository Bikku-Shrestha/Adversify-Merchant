package com.nepal.adversify.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "merchant")
public class MerchantEntity {

    @PrimaryKey
    public int id;

    public String title;
    public String address;
    public String contact;
    public String website;
    public String image;
    public String description;

    @ColumnInfo(name = "opening_hour")
    public int opening;

    @ColumnInfo(name = "discount")
    public int discount;
    @ColumnInfo(name = "offer")
    public int offer;
    @ColumnInfo(name = "location")
    public int location;


}
