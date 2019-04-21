package com.sss.wearable.Classes;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "interests")
public class Interest {
    @PrimaryKey
    private int id;
    private String name;
    private int color;

    public Interest(int id, String name, int color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
