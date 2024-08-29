package com.example.instagramclone.instagramclone.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@Getter
@Setter
@AllArgsConstructor
@DynamoDBTable(tableName = "PostDetails")
public class Post {

    @DynamoDBHashKey(attributeName = "id")
    private String id;
    @DynamoDBAttribute
    private String userId; // User ID of the poster
    @DynamoDBAttribute
    private String caption;
    @DynamoDBAttribute
    private String image; // Store image URL instead of byte array
    @DynamoDBAttribute
    private String dateTime;
    @DynamoDBAttribute
    private List<String> likes; // List of User IDs who liked the post
    @DynamoDBAttribute
    private List<Comment> comments;
    @DynamoDBAttribute
    private List<String> taggedUsers;

    public Post() {
        this.dateTime = getCurrentDateTime();
        likes = Collections.EMPTY_LIST;
        comments = Collections.EMPTY_LIST;
        taggedUsers = Collections.EMPTY_LIST;
    }

    private String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

}
