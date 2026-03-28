package com.grouprace.core.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.grouprace.core.model.Post;

@Entity(tableName = "posts")
public class PostEntity {
    @PrimaryKey
    public int postId;
    public Integer recordId;
    public int ownerId;
    public String title;
    public String description;
    public String photoUrl;
    public int likeCount;
    public int commentCount;
    public String viewMode;
    public String createdAt;
    public String username;
    public String displayName;
    public String profilePictureUrl;
    public boolean isLiked;

    public PostEntity() {}

    public PostEntity(int postId, Integer recordId, int ownerId, String title, String description, String photoUrl, int likeCount, int commentCount, String viewMode, String createdAt, String username, String displayName, String profilePictureUrl, boolean isLiked) {
        this.postId = postId;
        this.recordId = recordId;
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.photoUrl = photoUrl;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.viewMode = viewMode;
        this.createdAt = createdAt;
        this.username = username;
        this.displayName = displayName;
        this.profilePictureUrl = profilePictureUrl;
        this.isLiked = isLiked;
    }

    public Post asExternalModel() {
        Post post = new Post(postId, recordId, ownerId, title, description, photoUrl, likeCount, commentCount, viewMode, createdAt, username, displayName, profilePictureUrl);
        post.setLiked(isLiked);
        return post;
    }
}
