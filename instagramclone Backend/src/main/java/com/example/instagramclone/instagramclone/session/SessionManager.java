package com.example.instagramclone.instagramclone.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.instagramclone.instagramclone.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class SessionManager {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String SESSION_PREFIX = "session:";

    public String generateSignInCode() {
        return UUID.randomUUID().toString().substring(0, 10); // Example code
    }

    public void storeCode(String code, User user) {
        // redisTemplate.opsForValue().set(code, user, 30, TimeUnit.MINUTES); // Set
        // expiry
        redisTemplate.opsForValue().set(code, user);
        redisTemplate.opsForValue().set(SESSION_PREFIX + user.getEmail(), code); // Store session

        System.out.println("sessionCode in sessionManager = " + SESSION_PREFIX + user.getEmail());
        System.out.println("Code in sessionManager = " + code);
    }

    public User getUserByCode(String code) {
        Object obj = redisTemplate.opsForValue().get(code);

        if (obj == null)
            return null;

        // Deserialize the object
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.convertValue(obj, User.class);
        } catch (IllegalArgumentException e) {
            System.out.println("Error converting Redis value to User: " + e.getMessage());
            return null;
        }
    }

    public void removeCode(String code, String sessionCode) {
        System.out.println("Attempting to delete key from Redis: " + code);
        Boolean deleted = redisTemplate.delete(code) && redisTemplate.delete(sessionCode);
        if (deleted) {
            System.out.println("Key successfully deleted: " + code);
        } else {
            System.out.println("Key deletion failed or key not found: " + code);
        }
    }

    public boolean signOut(User user) {
        // Retrieve the session code associated with the user's email
        String sessionCode = (String) redisTemplate.opsForValue().get(SESSION_PREFIX + user.getEmail());

        System.out.println("Session code retrieved for sign out: " + sessionCode + " got using " + SESSION_PREFIX
                + user.getEmail());

        if (sessionCode != null) {
            removeCode(sessionCode, SESSION_PREFIX + user.getEmail());
            return true;
        }

        // No active session found for the user
        return false;
    }

    public boolean isUserSignedInByToken(String code) {
        System.out.println("isUserSigned in SessionManager = " + redisTemplate.hasKey(code));
        return redisTemplate.hasKey(code);
    }

    public boolean isUserSignedIn(User user) {
        System.out.println("here in isUserSignedIn");
        String sessionCode = (String) redisTemplate.opsForValue().get(SESSION_PREFIX + user.getEmail());
        // Check if there's an active session for the user
        System.out.println("sessionCode in IsUserSignIn of sessionManager = " + sessionCode);
        return sessionCode != null;
    }

}
