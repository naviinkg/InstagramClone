package com.example.instagramclone.instagramclone.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@DynamoDBTable(tableName = "UserDetails")
public class User {

    @DynamoDBHashKey(attributeName = "id")
    private String id;
    @DynamoDBAttribute
    private String fullName;
    @DynamoDBAttribute
    private String bio;
    @DynamoDBAttribute
    private String location;
    @DynamoDBAttribute
    private String website;
    @DynamoDBAttribute
    private int postsCount;
    @DynamoDBAttribute
    private int followersCount;
    @DynamoDBAttribute
    private int followingCount;
    @DynamoDBAttribute
    private String profilePicture;
    @DynamoDBAttribute
    private String email;
    @DynamoDBAttribute
    private String password;
    @DynamoDBAttribute
    private boolean privateAccount;

    @DynamoDBAttribute
    private List<String> followers;

    @DynamoDBAttribute
    private List<String> following;

    @DynamoDBAttribute
    private List<String> requests;

    @DynamoDBAttribute
    private List<Highlights> highlights;

    @DynamoDBAttribute
    private List<String> posts;

    @DynamoDBAttribute
    private List<String> saved;

    @DynamoDBAttribute
    private List<String> reels;

    @DynamoDBAttribute
    private List<String> tagged;

    public User() {
        followers = Collections.EMPTY_LIST;
        highlights = Collections.EMPTY_LIST;
        posts = Collections.EMPTY_LIST;
        requests = Collections.EMPTY_LIST;
        saved = Collections.EMPTY_LIST;
        reels = Collections.EMPTY_LIST;
        tagged = Collections.EMPTY_LIST;
        privateAccount = true;
        email = "";
        followersCount = 0;
        postsCount = 0;
        profilePicture = "";
        following = Collections.EMPTY_LIST;
        bio = "";
        location = "";
        website = "";
    }
}
