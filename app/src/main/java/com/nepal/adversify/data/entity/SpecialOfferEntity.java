package com.nepal.adversify.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "offer")
public class SpecialOfferEntity {

    @PrimaryKey
    public int id;

    public String title;
    public String description;

}
