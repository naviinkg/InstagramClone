package com.example.instagramclone.instagramclone.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.example.instagramclone.instagramclone.model.Post;
import com.example.instagramclone.instagramclone.model.Comment;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Repository
public class PostRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public Post createPost(Post post) {
        String uniquePart = UUID.randomUUID().toString();

        int totalPostsCount = getTotalPostsCount();

        String captionPart = getCaptionPart(post.getCaption());

        String postId = uniquePart + "-" + totalPostsCount + "-" + captionPart;

        post.setId(postId);

        dynamoDBMapper.save(post);
        return post;
    }

    private int getTotalPostsCount() {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        PaginatedScanList<Post> scanResult = dynamoDBMapper.scan(Post.class, scanExpression);
        return scanResult.size();
    }

    private String getCaptionPart(String caption) {
        if (caption == null || caption.isEmpty())
            return "nocaption";

        return caption.length() <= 10 ? caption : caption.substring(0, 10).replaceAll("\\s+", "");
    }

    public Post getPost(String postId) {
        return dynamoDBMapper.load(Post.class, postId);
    }

    public String updatePostById(String id, Post post) {
        dynamoDBMapper.save(post,
                new DynamoDBSaveExpression()
                        .withExpectedEntry("id",
                                new ExpectedAttributeValue(
                                        new AttributeValue().withS(id))));
        return id;
    }

    public Post deletePostById(String id) {
        Post post = dynamoDBMapper.load(Post.class, id);
        if (Objects.nonNull(post)) {
            dynamoDBMapper.delete(post);
        }
        return post;
    }

    public String likePost(String id, String userId) {
        Post post = dynamoDBMapper.load(Post.class, id);

        if (Objects.isNull(post))
            return "No such post";

        if (post.getLikes().contains(userId))
            return "You already liked this post";

        List<String> likes = post.getLikes();
        likes.add(userId);

        post.setLikes(likes);

        if (updatePostById(id, post).equals(id)) {
            return "Liked";
        } else
            return "Problem in updating";
    }

    public String unlikePost(String id, String userId) {
        Post post = dynamoDBMapper.load(Post.class, id);

        if (!post.getLikes().contains(userId))
            return "Like the post first";

        List<String> likes = post.getLikes();
        likes.remove(userId);

        post.setLikes(likes);

        if (updatePostById(id, post).equals(id)) {
            return "Unliked";
        } else
            return "Problem in updating";
    }

    public String commentPost(String id, String userId, String message) {
        Post post = dynamoDBMapper.load(Post.class, id);

        List<Comment> comments = post.getComments();
        comments.add(new Comment(message, userId));

        post.setComments(comments);

        if (updatePostById(id, post).equals(id))
            return "Comment Successful";
        else
            return "Problem in updating";
    }

    public String uncommentPost(String id, int index) {
        Post post = dynamoDBMapper.load(Post.class, id);

        List<Comment> comments = post.getComments();

        if (comments.size() > index)
            comments.remove(index);
        else
            return "No comment found";

        post.setComments(comments);

        if (updatePostById(id, post).equals(id))
            return "Uncomment Successful";
        else
            return "Problem in updating";
    }

    public List<Post> getAllPosts() {
        return dynamoDBMapper.scan(Post.class, new DynamoDBScanExpression());
    }
}
