package com.grouprace.core.network.model.user;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class MyProfileInfoPayload {
    @SerializedName("username")
    private String username;

    @SerializedName("fullname")
    private String fullname;

    @SerializedName("email")
    private String email;

    @SerializedName("birthdate")
    private String birthdate;

    @SerializedName("avatar_url")
    private String avatarUrl;

    @SerializedName("bio")
    private String bio;

    @SerializedName("province_city")
    private String provinceCity;

    @SerializedName("country")
    private String country;

    @SerializedName("height_cm")
    private Double heightCm;

    @SerializedName("weight_kg")
    private Double weightKg;

    public MyProfileInfoPayload() {}

    public MyProfileInfoPayload(String username, String fullname, String email, String birthdate,
                                String avatarUrl, String bio, String provinceCity, String country,
                                Double heightCm, Double weightKg) {
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

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBirthdate() { return birthdate; }
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getProvinceCity() { return provinceCity; }
    public void setProvinceCity(String provinceCity) { this.provinceCity = provinceCity; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Double getHeightCm() { return heightCm; }
    public void setHeightCm(Double heightCm) { this.heightCm = heightCm; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    @NonNull
    @Override
    public String toString() {
        return "MyProfileInfoPayload{" +
                "username='" + username + '\'' +
                ", fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", bio='" + bio + '\'' +
                ", provinceCity='" + provinceCity + '\'' +
                ", country='" + country + '\'' +
                ", heightCm=" + heightCm +
                ", weightKg=" + weightKg +
                '}';
    }
}
