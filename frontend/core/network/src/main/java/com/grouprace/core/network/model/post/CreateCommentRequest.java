package com.grouprace.core.network.model.post;

import com.google.gson.annotations.SerializedName;

public class CreateCommentRequest {

    @SerializedName("content")
    private String content;

    @SerializedName("parentId")
    private Integer parentId;

    public CreateCommentRequest(String content) {
        this.content = content;
    }

    public CreateCommentRequest(String content, Integer parentId) {
        this.content = content;
        this.parentId = parentId;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }
}
