package com.grouprace.core.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.grouprace.core.model.Profile.MyProfileInfo;

@Entity(tableName = "my_profile_info")
public class MyProfileInfoEntity {
    @PrimaryKey
    public int id;
    public boolean pendingSync;
    public String username;
    public String fullname;
    public String email;
    public String birthdate;
    public String avatarUrl;
    public String bio;
    public String provinceCity;
    public String country;
    public Double heightCm;
    public Double weightKg;

    public MyProfileInfoEntity(int id, boolean pendingSync, String username, String fullname, String email,
                               String birthdate, String avatarUrl, String bio,
                               String provinceCity, String country, Double heightCm,
                               Double weightKg) {
        this.id = id;
        this.pendingSync = pendingSync;
        this.username = username;
        this.fullname = fullname;
        this.email = email;
        this.birthdate = birthdate;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.provinceCity = provinceCity;
        this.country = country;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
    }

    public MyProfileInfo asExternalModel() {
        return new MyProfileInfo(
                username,
                fullname,
                email,
                birthdate,
                avatarUrl,
                bio,
                provinceCity,
                country,
                heightCm,
                weightKg
        );
    }
}
