package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.grouprace.core.model.Post;
import com.grouprace.core.network.model.NetworkPost;
import com.grouprace.core.network.source.PostNetworkDataSource;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class PostRepositoryImpl implements PostRepository {

    private final PostNetworkDataSource postNetworkDataSource;

    @Inject
    public PostRepositoryImpl(PostNetworkDataSource postNetworkDataSource) {
        this.postNetworkDataSource = postNetworkDataSource;
    }

    @Override
    public LiveData<List<Post>> getPosts() {
        return Transformations.map(postNetworkDataSource.getPosts(), networkPosts -> {
            if (networkPosts == null) return null;
            return networkPosts.stream()
                    .map(NetworkPost::asExternalModel)
                    .collect(Collectors.toList());
        });
    }
}
