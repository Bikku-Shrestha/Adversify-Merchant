package com.nepal.adversify.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "review")
public class ReviewEntity {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "client_id")
    public String clientId;

    @ColumnInfo(name = "client_name")
    public String clientName;
    public String review;
    public int star;

}
