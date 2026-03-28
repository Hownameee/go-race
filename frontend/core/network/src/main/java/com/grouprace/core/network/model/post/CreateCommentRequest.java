package com.grouprace.core.network.model.post;

import com.google.gson.annotations.SerializedName;

public class CreateCommentRequest {

    @SerializedName("content")
    private String content;

    public CreateCommentRequest(String content) {
        this.content = content;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
