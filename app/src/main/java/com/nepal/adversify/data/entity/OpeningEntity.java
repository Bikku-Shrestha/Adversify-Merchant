package com.nepal.adversify.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "opening_info")
public class OpeningEntity {

    @PrimaryKey
    public int id;

    public String sunday;
    public String monday;
    public String tuesday;
    public String wednesday;
    public String thursday;
    public String friday;
    public String saturday;

}
