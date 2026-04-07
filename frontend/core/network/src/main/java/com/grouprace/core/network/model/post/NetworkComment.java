package com.grouprace.core.network.model.post;

import com.google.gson.annotations.SerializedName;

public class NetworkComment {

    @SerializedName("comment_id")
    private int commentId;

    @SerializedName("post_id")
    private int postId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("content")
    private String content;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("username")
    private String username;

    @SerializedName("fullname")
    private String fullName;

    @SerializedName("avatar_url")
    private String profilePictureUrl;

    @SerializedName("like_count")
    private int likeCount;

    @SerializedName("reply_count")
    private int replyCount;

    @SerializedName("is_liked")
    private int isLiked;

    @SerializedName("parent_id")
    private Integer parentId;

    public int getCommentId() { return commentId; }
    public void setCommentId(int commentId) { this.commentId = commentId; }

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public int getReplyCount() { return replyCount; }
    public void setReplyCount(int replyCount) { this.replyCount = replyCount; }

    public int getIsLiked() { return isLiked; }
    public void setIsLiked(int isLiked) { this.isLiked = isLiked; }

    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }

    public com.grouprace.core.model.Comment asExternalModel() {
        return new com.grouprace.core.model.Comment(
            commentId, postId, userId, content, createdAt, username, fullName, profilePictureUrl,
            likeCount, replyCount, isLiked == 1, parentId
        );
    }
}
