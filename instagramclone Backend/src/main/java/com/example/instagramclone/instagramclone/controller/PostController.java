package com.example.instagramclone.instagramclone.controller;

import java.util.List;

import org.hibernate.mapping.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.example.instagramclone.instagramclone.model.Post;
import com.example.instagramclone.instagramclone.model.User;
import com.example.instagramclone.instagramclone.model.Comment;
import com.example.instagramclone.instagramclone.service.PostService;
import com.example.instagramclone.instagramclone.service.UserService;
import com.example.instagramclone.instagramclone.session.SessionManager;
import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    public UserService userService;

    @Autowired
    private SessionManager sessionManager;

    @PostMapping("/post/{userId}")
    public ResponseEntity<?> createPost(
            @RequestBody @Valid Post post,
            @PathVariable("userId") String userId,
            @RequestHeader("Authorization") String token) {

        // Check if the user is signed in using the token
        if (!sessionManager.isUserSignedInByToken(token)) {
            return ResponseEntity.status(401).body("User not signed in");
        }
        post.setUserId(userId);

        Post createdPost = postService.createPost(post);

        User user = userService.getUser(userId);
        List<String> posts = user.getPosts();
        posts.add(createdPost.getId());
        user.setPosts(posts);

        userService.updateUserById(userId, user);

        return ResponseEntity.ok(createdPost);
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<?> getPostById(@PathVariable("id") String id, @RequestHeader("Authorization") String token) {
        if (!sessionManager.isUserSignedInByToken(token))
            return ResponseEntity.status(401).body("User not signed in");

        Post post = postService.getPost(id);
        if (post == null)
            return ResponseEntity.status(404).body("Post not found");

        return ResponseEntity.ok(post);
    }

    @PutMapping("/post/{id}")
    public ResponseEntity<?> updatePostById(@PathVariable("id") String id, @RequestBody @Valid Post post,
            @RequestHeader("Authorization") String token) {

        if (!sessionManager.isUserSignedInByToken(token))
            return ResponseEntity.status(401).body("User not signed in");

        if (!postService.isPostExists(id))
            return ResponseEntity.status(404).body("Post not found");

        return ResponseEntity.ok(postService.updatePostById(id, post));
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<?> deletePostById(@PathVariable("id") String id,
            @RequestHeader("Authorization") String token) {
        if (!sessionManager.isUserSignedInByToken(token)) {
            return ResponseEntity.status(401).body("User not signed in");
        }

        if (!postService.isPostExists(id)) {
            return ResponseEntity.status(404).body("Post not found");
        }

        if (!postService.getPost(id).getUserId().equals(sessionManager.getUserByCode(token).getId())) {
            return ResponseEntity.status(403).body("User not authorized to delete this post");
        }

        return ResponseEntity.ok(postService.deletePostById(id));
    }

    @PostMapping("/post/{post_id}/comment")
    public ResponseEntity<?> commentPost(@PathVariable("post_id") String postId, @RequestBody @Valid Comment comment,
            @RequestHeader("Authorization") String token) {
        if (!sessionManager.isUserSignedInByToken(token)) {
            return ResponseEntity.status(401).body("User not signed in");
        }

        if (!postService.isPostExists(postId)) {
            return ResponseEntity.status(404).body("Post not found");
        }

        return ResponseEntity.ok(postService.commentPost(postId, comment.getUserId(), comment.getMessage()));
    }

    @GetMapping("/post/{post_id}/unComment/{index}")
    public ResponseEntity<?> uncommentPost(@PathVariable("post_id") String postId, @PathVariable("index") int index,
            @RequestHeader("Authorization") String token) {
        if (!sessionManager.isUserSignedInByToken(token)) {
            return ResponseEntity.status(401).body("User not signed in");
        }

        if (!postService.isPostExists(postId)) {
            return ResponseEntity.status(404).body("Post not found");
        }

        return ResponseEntity.ok(postService.uncommentPost(postId, index));
    }

    @GetMapping("/post/{post_id}/like/{user_id}")
    public ResponseEntity<?> likePost(@PathVariable("post_id") String postId, @PathVariable("user_id") String userId,
            @RequestHeader("Authorization") String token) {
        if (!sessionManager.isUserSignedInByToken(token)) {
            return ResponseEntity.status(401).body("User not signed in");
        }

        if (!postService.isPostExists(postId)) {
            return ResponseEntity.status(404).body("Post not found");
        }

        return ResponseEntity.ok(postService.likePost(postId, userId));
    }

    @GetMapping("/post/{post_id}/unlike/{user_id}")
    public ResponseEntity<?> unlikePost(@PathVariable("post_id") String postId, @PathVariable("user_id") String userId,
            @RequestHeader("Authorization") String token) {
        if (!sessionManager.isUserSignedInByToken(token)) {
            return ResponseEntity.status(401).body("User not signed in");
        }

        if (!postService.isPostExists(postId)) {
            return ResponseEntity.status(404).body("Post not found");
        }

        return ResponseEntity.ok(postService.unlikePost(postId, userId));
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getAllPosts(@RequestHeader("Authorization") String token) {
        if (!sessionManager.isUserSignedInByToken(token)) {
            return ResponseEntity.status(401).body("User not signed in");
        }

        List<Post> posts = postService.getAllPosts();
        if (posts.isEmpty()) {
            return ResponseEntity.status(404).body("No posts found");
        }

        for (Post post : posts)
            System.out.println(post.getUserId());

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/following")
    public ResponseEntity<?> getFollowingPosts(@RequestHeader("Authorization") String token,
            @RequestParam("userId") String userId) {
        // Check if the user is signed in
        if (!sessionManager.isUserSignedInByToken(token)) {
            return ResponseEntity.status(401).body("User not signed in");
        }

        // Get user details
        User user = userService.getUser(userId);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        // Get list of user IDs whom the user follows
        List<String> followingUserIds = user.getFollowing();

        List<String> postIds = user.getPosts();

        for (String followingUserId : followingUserIds) {
            postIds.addAll(userService.getUser(followingUserId).getPosts());
        }

        List<Post> followingPosts = postService.getPostsByIds(postIds);
        if (followingPosts.isEmpty()) {
            return ResponseEntity.status(404).body("No posts found from followed users");
        }

        return ResponseEntity.ok(followingPosts);
    }
}
