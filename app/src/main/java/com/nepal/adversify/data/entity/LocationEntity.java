package com.nepal.adversify.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "location")
public class LocationEntity {

    @PrimaryKey
    public int id;

    public double lat;
    public double lon;

}
