package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import com.grouprace.core.model.Post;
import java.util.List;

public interface PostRepository {
    LiveData<List<Post>> getPosts();
}
