package com.example.instagramclone.instagramclone.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBDocument
public class Highlights {

    @DynamoDBAttribute
    private String title;

    @DynamoDBAttribute
    private List<String> images;
}
