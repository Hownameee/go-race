package com.grouprace.core.model;

public class Comment {
    private int commentId;
    private int postId;
    private int userId;
    private String content;
    private String createdAt;
    private String username;
    private String fullName;
    private String avatarUrl;
    private int likeCount;
    private int replyCount;
    private boolean isLiked;
    private Integer parentId;

    public Comment() {}

    public Comment(int commentId, int postId, int userId, String content, String createdAt, String username, String fullName, String avatarUrl, int likeCount, int replyCount, boolean isLiked, Integer parentId) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
        this.username = username;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.likeCount = likeCount;
        this.replyCount = replyCount;
        this.isLiked = isLiked;
        this.parentId = parentId;
    }

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

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public int getReplyCount() { return replyCount; }
    public void setReplyCount(int replyCount) { this.replyCount = replyCount; }

    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }

    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }
}
