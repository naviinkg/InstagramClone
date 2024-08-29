package com.example.instagramclone.instagramclone.repository;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.example.instagramclone.instagramclone.model.Post;
import com.example.instagramclone.instagramclone.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Repository
public class UserRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User createUser(User user) {
        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        dynamoDBMapper.save(user);
        return user;
    }

    public User getUser(String userId) {
        return dynamoDBMapper.load(User.class, userId);
    }

    public User updateUserById(String userId, User user) {
        dynamoDBMapper.save(user,
                new DynamoDBSaveExpression()
                        .withExpectedEntry("id",
                                new ExpectedAttributeValue(
                                        new AttributeValue().withS(userId))));
        return user;
    }

    public User deleteUserById(String userId) {
        User user = dynamoDBMapper.load(User.class, userId);
        if (Objects.nonNull(user)) {
            dynamoDBMapper.delete(user);
        }
        return user;
    }

    public String follow(String userId, String followerId) {
        User user = dynamoDBMapper.load(User.class, userId);
        User follower = dynamoDBMapper.load(User.class, followerId);

        if (Objects.nonNull(follower)) {
            List<String> followingList = user.getFollowing();
            if (followingList.contains(followerId))
                return "You are already following " + follower.getFullName();
            followingList.add(followerId);

            user.setFollowing(followingList);
            updateUserById(userId, user);

            List<String> followers = follower.getFollowers();
            followers.add(user.getId());
            follower.setFollowers(followers);

            updateUserById(followerId, follower);

            return "Started following " + follower.getFullName();
        }
        return "No such user found";
    }

    public String unfollow(String userId, String followingId) {
        User user = dynamoDBMapper.load(User.class, userId);
        User follower = dynamoDBMapper.load(User.class, followingId);

        if (Objects.nonNull(follower)) {
            List<String> followingList = user.getFollowing();
            if (followingList.contains(followingId))
                followingList.remove(followingId);
            else
                return "No such following user found";

            user.setFollowing(followingList);
            updateUserById(userId, user);

            List<String> followers = follower.getFollowers();
            if (followers.contains(userId))
                followers.remove(userId);

            follower.setFollowers(followers);
            updateUserById(followingId, follower);

            return "Unfollowed " + follower.getFullName();
        }
        return "No such user found";
    }

    public User signIn(User userDetails) {

        return findByEmail(userDetails.getEmail());
    }

    public String signOut() {
        // Implement sign-out logic here
        return "Sign-out successful";
    }

    public boolean deleteById(String id) {
        User user = dynamoDBMapper.load(User.class, id);
        if (Objects.nonNull(user)) {
            dynamoDBMapper.delete(user);
            return true;
        }
        return false;
    }

    public User findByEmail(String email) {
        // Create a map of expression attribute values
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":email", new AttributeValue().withS(email));

        // Create a query expression
        DynamoDBQueryExpression<User> queryExpression = new DynamoDBQueryExpression<User>()
                .withIndexName("email-index") // Ensure this index exists
                .withKeyConditionExpression("email = :email")
                .withExpressionAttributeValues(expressionAttributeValues)
                .withConsistentRead(false); // Global secondary indexes do not support consistent reads

        // Execute the query
        List<User> result = dynamoDBMapper.query(User.class, queryExpression);

        // Check result and handle accordingly
        if (result.isEmpty()) {
            return null; // No user found
        }

        return result.get(0); // Return the first user found
    }

    public User findUserById(String id) {
        // DynamoDB query to find user by email
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":id", new AttributeValue().withS(id));

        DynamoDBQueryExpression<User> queryExpression = new DynamoDBQueryExpression<User>()
                .withIndexName("id-index")
                .withKeyConditionExpression("id = :id")
                .withExpressionAttributeValues(expressionAttributeValues)
                .withConsistentRead(false);

        List<User> result = dynamoDBMapper.query(User.class, queryExpression);

        System.out.println("user in findUserById = " + result);

        // Return the first result or null if not found
        return result.isEmpty() ? null : result.get(0);
    }

}
