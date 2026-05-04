package com.grouprace.core.network.model.club;

import com.google.gson.annotations.SerializedName;

public class CreateClubRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("privacy_type")
    private String privacyType;

    public CreateClubRequest(String name, String description, String privacyType) {
        this.name = name;
        this.description = description;
        this.privacyType = privacyType;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPrivacyType() { return privacyType; }
}
