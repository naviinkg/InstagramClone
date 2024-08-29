package com.example.instagramclone.instagramclone.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.instagramclone.instagramclone.service.UserService;
import com.example.instagramclone.instagramclone.session.SessionManager;
import com.example.instagramclone.instagramclone.model.Highlights;
import com.example.instagramclone.instagramclone.model.User;
import com.example.instagramclone.instagramclone.model.registeration.UserRegistrationRequest;

import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    public UserService userService;
    @Autowired
    private SessionManager sessionManager;

    @PostMapping("/user/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequest registrationRequest) {
        return userService.registerUser(registrationRequest);
    }

    @PostMapping("/user/verifyOtp")
    public ResponseEntity<?> verifyOtp(@RequestBody UserRegistrationRequest registrationRequest) {
        return userService.verifyUserOtp(registrationRequest);
    }

    // testing onlys
    @PostMapping("/user")
    public User createUser(@RequestBody User user) {
        System.out.println("here in controller");
        return userService.createUser(user);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") String userId, @RequestHeader("Authorization") String token) {
        if (!sessionManager.isUserSignedInByToken(token)) {
            return ResponseEntity.status(401).body("User not signed in");
        }
        System.out.println("here at get User");
        return ResponseEntity.status(200).body(userService.getUser(userId));
    }

    @PutMapping("user/{id}")
    public User updateUserById(@PathVariable String id, @RequestBody User user) {
        System.out.println(user);
        return userService.updateUserById(id, user);
    }

    @GetMapping("user/{user_id}/follow/{follow_id}")
    public ResponseEntity<String> follow(@PathVariable String user_id, @PathVariable String follow_id,
            @RequestHeader("Authorization") String token) {
        if (!sessionManager.isUserSignedInByToken(token)) {
            return ResponseEntity.status(401).body("User not signed in");
        }
        return ResponseEntity.status(200).body(userService.follow(user_id, follow_id));
    }

    @GetMapping("user/{user_id}/unFollow/{follow_id}")
    public ResponseEntity<String> unFollow(@PathVariable String user_id, @PathVariable String follow_id,
            @RequestHeader("Authorization") String token) {
        if (!sessionManager.isUserSignedInByToken(token)) {
            return ResponseEntity.status(401).body("User not signed in");
        }
        return ResponseEntity.status(200).body(userService.unfollow(user_id, follow_id));
    }

    @PostMapping("/user/signIn")
    public ResponseEntity<?> signIn(@RequestBody User user) {
        System.out.println("here in signIn");
        try {
            User authenticatedUser = userService.signIn(user);
            if (authenticatedUser != null) {
                String signInCode = sessionManager.generateSignInCode();
                sessionManager.storeCode(signInCode, authenticatedUser);

                Map<String, String> response = new HashMap<>();
                response.put("token", signInCode);
                response.put("userId", authenticatedUser.getId());

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(401).body("Invalid credentials");
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/user/signOut")
    public ResponseEntity<?> signOut(@RequestBody User user,
            @RequestHeader("Authorization") String token) {

        System.out.println("user == " + user);

        System.out.println("signout useremail = =" + user.getEmail());

        System.out.println("token = " + token);
        if (!sessionManager.isUserSignedInByToken(token)) {
            return ResponseEntity.status(401).body("User not signed in");
        }

        boolean success = userService.signOut(user);
        if (success) {
            return ResponseEntity.ok("Successfully signed out.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session found.");
        }
    }

    @PutMapping("/user/update")
    public ResponseEntity<?> updateUser(@RequestBody User userDetails, @RequestHeader("Authorization") String token) {
        // Check if the user is signed in
        if (!sessionManager.isUserSignedInByToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not signed in");
        }

        // Assuming the sessionManager can return the user ID based on the token
        User user = sessionManager.getUserByCode(token);

        try {
            User updatedUser = userService.updateUserDetails(user.getId(), userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @DeleteMapping("/user/delete")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token, @RequestBody User user) {
        if (!sessionManager.isUserSignedInByToken(token)) {
            return ResponseEntity.status(401).body("User not signed in");
        }
        userService.signOut(user);
        boolean success = userService.deleteUserById(user.getId());
        if (success) {
            return ResponseEntity.ok("User successfully deleted.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

    @PostMapping("/user/{id}/addHighlight")
    public ResponseEntity<?> addHighlightToUser(
            @PathVariable("id") String userId,
            @RequestBody Highlights highlight,
            @RequestHeader("Authorization") String token) {

        if (!sessionManager.isUserSignedInByToken(token)) {
            return ResponseEntity.status(401).body("User not signed in");
        }

        boolean success = userService.addHighlight(userId, highlight);
        if (success) {
            return ResponseEntity.status(200).body("Highlight added successfully");
        } else {
            return ResponseEntity.status(500).body("Failed to add highlight");
        }
    }

    @DeleteMapping("/user/{id}/removeHighlight/{index}")
    public ResponseEntity<?> removeHighlightFromUser(
            @PathVariable("id") String userId,
            @PathVariable("index") int highlightIndex,
            @RequestHeader("Authorization") String token) {

        if (!sessionManager.isUserSignedInByToken(token)) {
            return ResponseEntity.status(401).body("User not signed in");
        }

        boolean success = userService.removeHighlight(userId, highlightIndex);
        if (success) {
            return ResponseEntity.status(200).body("Highlight removed successfully");
        } else {
            return ResponseEntity.status(404).body("Highlight not found");
        }
    }

}
