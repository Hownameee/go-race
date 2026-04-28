package com.grouprace.core.network.model.club;

import com.google.gson.annotations.SerializedName;

public class UpdateClubRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("image_base64")
    private String imageBase64;

    @SerializedName("image_content_type")
    private String imageContentType;

    public UpdateClubRequest(String name, String description, String imageBase64, String imageContentType) {
        this.name = name;
        this.description = description;
        this.imageBase64 = imageBase64;
        this.imageContentType = imageContentType;
    }
}
