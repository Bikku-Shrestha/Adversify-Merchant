package com.nepal.adversify.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CompositeMerchantEntity {

    @PrimaryKey
    public int id;

    public String title;
    public String address;
    public String contact;
    public String website;
    public String category;
    public String image;
    public String description;

    @ColumnInfo(name = "discount_id")
    public int discountId = 1;
    @ColumnInfo(name = "discount_title")
    public String discountTitle;
    @ColumnInfo(name = "discount_description")
    public String discountDescription;

    @ColumnInfo(name = "offer_id")
    public int offerId = 1;
    @ColumnInfo(name = "offer_title")
    public String offerTitle;
    @ColumnInfo(name = "offer_description")
    public String offerDescription;

    @ColumnInfo(name = "opening_id")
    public int openingId = 1;
    public String sunday;
    public String monday;
    public String tuesday;
    public String wednesday;
    public String thursday;
    public String friday;
    public String saturday;

    @ColumnInfo(name = "location_id")
    public int locationId = 1;
    public double lat;
    public double lon;


}
