package com.grouprace.core.model.Profile;

public class MyProfileInfo {
    private String username;
    private String fullname;
    private String email;
    private String birthdate;
    private String avatarUrl;
    private String nationality;
    private String address;
    private Double heightCm;
    private Double weightKg;

    public MyProfileInfo() {}

    public MyProfileInfo(String username, String fullname, String email, String birthdate,
                         String avatarUrl, String nationality, String address,
                         Double heightCm, Double weightKg) {
        this.username = username;
        this.fullname = fullname;
        this.email = email;
        this.birthdate = birthdate;
        this.avatarUrl = avatarUrl;
        this.nationality = nationality;
        this.address = address;
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

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Double getHeightCm() { return heightCm; }
    public void setHeightCm(Double heightCm) { this.heightCm = heightCm; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }
}
