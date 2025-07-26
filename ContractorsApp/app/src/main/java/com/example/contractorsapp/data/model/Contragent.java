package com.example.contractorsapp.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

// Serializable е нужен, за да можем да подаваме обекта между екрани (Activities)
@Entity(tableName = "contragent_table")
public class Contragent implements Serializable {

    @PrimaryKey
    @SerializedName("Id")
    private int id;

    @SerializedName("Name")
    private String name = "";

    @SerializedName("Phone")
    private String phone = "";

    @SerializedName("Address")
    private String address = "";

    @SerializedName("Bulstat")
    private String bulstat = "";

    @SerializedName("Email")
    private String email = "";

    // Това поле не идва от API-то, ще го ползваме локално
    private boolean isFavorite = false;

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getBulstat() { return bulstat; }
    public String getEmail() { return email; }
    public boolean isFavorite() { return isFavorite; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setBulstat(String bulstat) { this.bulstat = bulstat; }
    public void setEmail(String email) { this.email = email; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}