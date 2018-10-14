package com.nepal.adversify.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "discount")
public class DiscountEntity {

    @PrimaryKey
    public int id;

    public String title;
    public String description;

}
