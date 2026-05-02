package com.grouprace.core.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.grouprace.core.model.Post;
import java.util.List;

@Entity(tableName = "posts")
public class PostEntity {
    @PrimaryKey
    public int postId;
    public Integer recordId;
    public int ownerId;
    public String title;
    public String description;
    public List<String> photoUrls;
    public int likeCount;
    public int commentCount;
    public String viewMode;
    public String createdAt;
    public String username;
    public String fullName;
    public String profilePictureUrl;
    public String activityType;
    public Integer durationSeconds;
    public Double distanceKm;
    public Double speed;
    public String recordImageUrl;
    public boolean isLiked;
    public Integer clubId;
    public boolean pendingSync;
    public boolean selfOwner;

    @Ignore
    public PostEntity(int postId, Integer recordId, int ownerId, String title, String description, java.util.List<String> photoUrls, int likeCount, int commentCount, String viewMode, String createdAt, String username, String fullName, String profilePictureUrl, String activityType, Integer durationSeconds, Double distanceKm, Double speed, String recordImageUrl, boolean isLiked, Integer clubId, boolean pendingSync) {
        this(postId, recordId, ownerId, title, description, photoUrl, likeCount, commentCount,
                viewMode, createdAt, username, fullName, profilePictureUrl, activityType,
                durationSeconds, distanceKm, speed, recordImageUrl, isLiked, clubId, false);
    }

    public PostEntity(int postId, Integer recordId, int ownerId, String title, String description, String photoUrl, int likeCount, int commentCount, String viewMode, String createdAt, String username, String fullName, String profilePictureUrl, String activityType, Integer durationSeconds, Double distanceKm, Double speed, String recordImageUrl, boolean isLiked, Integer clubId, boolean selfOwner) {
        this.postId = postId;
        this.recordId = recordId;
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.photoUrls = photoUrls;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.viewMode = viewMode;
        this.createdAt = createdAt;
        this.username = username;
        this.fullName = fullName;
        this.profilePictureUrl = profilePictureUrl;
        this.activityType = activityType;
        this.durationSeconds = durationSeconds;
        this.distanceKm = distanceKm;
        this.speed = speed;
        this.recordImageUrl = recordImageUrl;
        this.isLiked = isLiked;
        this.clubId = clubId;
        this.selfOwner = selfOwner;
        this.pendingSync = pendingSync;
    }

    public Post asExternalModel() {
        Post post = new Post(postId, recordId, ownerId, title, description, photoUrls, likeCount, commentCount, viewMode, createdAt, username, fullName, profilePictureUrl, activityType, durationSeconds, distanceKm, speed, recordImageUrl, clubId);
        post.setLiked(isLiked);
        return post;
    }
}
