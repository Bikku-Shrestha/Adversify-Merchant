package com.nepal.adversify.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "rate")
public class RateEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "client_id")
    public String clientId;

    @ColumnInfo(name = "client_name")
    public String clientName;
    public int star;

}
