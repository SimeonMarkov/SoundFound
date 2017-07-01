package com.soundfound.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

/**
 *
 * Created by Simeon Markov on 6/28/2017.
 */

@DatabaseTable(tableName = "users")
public class User {

    @DatabaseField(id = true)
    private String id;
    @DatabaseField(canBeNull = false, unique = true)
    private String email;
    @DatabaseField
    private String password;

    public User() {

    }

    public User(String email, String password) {
        this.id = UUID.randomUUID().toString();
        this.email = email;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
