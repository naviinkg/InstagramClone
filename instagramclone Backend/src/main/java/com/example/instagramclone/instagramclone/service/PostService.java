package com.example.instagramclone.instagramclone.service;

import com.example.instagramclone.instagramclone.model.Post;
import com.example.instagramclone.instagramclone.repository.PostRepository;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Post createPost(Post post) {
        return postRepository.createPost(post);
    }

    public Post getPost(String postId) {
        return postRepository.getPost(postId);
    }

    public String updatePostById(String postId, Post post) {
        return postRepository.updatePostById(postId, post);
    }

    public Post deletePostById(String postId) {
        return postRepository.deletePostById(postId);
    }

    public String likePost(String postId, String userId) {
        return postRepository.likePost(postId, userId);
    }

    public String unlikePost(String postId, String userId) {
        return postRepository.unlikePost(postId, userId);
    }

    public String commentPost(String postId, String userId, String message) {
        return postRepository.commentPost(postId, userId, message);
    }

    public String uncommentPost(String postId, int index) {
        return postRepository.uncommentPost(postId, index);
    }

    public boolean isPostExists(String postId) {
        return postRepository.getPost(postId) != null;
    }

    public List<Post> getAllPosts() {
        return postRepository.getAllPosts();
    }

    public List<Post> getPostsByIds(List<String> postIds) {
        List<Post> posts = new ArrayList<>();

        for (String postId : postIds)
            posts.add(getPost(postId));

        return posts;
    }
}
