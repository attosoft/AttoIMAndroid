package cn.id0755.im.data.source.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey
    public int id;
    public String name;
    public String lastName;
}
