package com.example.instagramclone.instagramclone.examples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

@Repository
@Component
public class EmployeeRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public Example save(Example employee) {
        dynamoDBMapper.save(employee);
        return employee;
    }

    public Example getEmployeeById(int employeeId) {
        return dynamoDBMapper.load(Example.class, employeeId);
    }
}