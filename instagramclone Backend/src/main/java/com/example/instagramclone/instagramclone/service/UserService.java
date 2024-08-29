package com.example.instagramclone.instagramclone.service;

import com.example.instagramclone.instagramclone.exceptions.NotFoundException;
import com.example.instagramclone.instagramclone.exceptions.UnauthorizedException;
import com.example.instagramclone.instagramclone.model.Highlights;
import com.example.instagramclone.instagramclone.model.User;
import com.example.instagramclone.instagramclone.model.registeration.UserRegistrationRequest;
import com.example.instagramclone.instagramclone.repository.UserRepository;
import com.example.instagramclone.instagramclone.session.SessionManager;
import com.example.instagramclone.instagramclone.util.EmailValidator;
import com.example.instagramclone.instagramclone.util.IdGeneratorUtil;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private OtpService otpService;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ResponseEntity<String> registerUser(UserRegistrationRequest registrationRequest) {
        // Check if the email already exists
        User existingUser = getUserByEmail(registrationRequest.getEmail());

        if (existingUser != null) {
            // If the user already exists, return a message indicating so
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already registered.");
        }

        try {
            // Generate and send OTP
            String otp = otpService.generateOtp();
            otpService.sendOtp(registrationRequest.getEmail(), otp);

            // Save OTP in the request to verify later
            registrationRequest.setOtp(otp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }

        // Response to indicate that OTP has been sent
        return ResponseEntity.ok("OTP has been sent to your email.");
    }

    public ResponseEntity<?> verifyUserOtp(UserRegistrationRequest registrationRequest) {
        User existingUser = getUserByEmail(registrationRequest.getEmail());

        if (existingUser != null) {
            // If the user already exists, return a message indicating so
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already registered.");
        }

        // Verify the OTP
        boolean isValidOtp = otpService.verifyOtp(registrationRequest.getEmail(), registrationRequest.getOtp());

        if (isValidOtp) {
            // Create user if OTP is valid
            User user = new User();
            String userId = IdGeneratorUtil.generateUniqueId(registrationRequest.getEmail(),
                    registrationRequest.getName()); // Generate unique ID
            user.setId(userId); // Set unique ID
            user.setFullName(registrationRequest.getName());
            user.setEmail(registrationRequest.getEmail());
            user.setPassword(registrationRequest.getPassword()); // Encrypt the password in service
            user.setFollowers(Collections.EMPTY_LIST);
            user.setFollowing(Collections.EMPTY_LIST);
            user.setPosts(Collections.EMPTY_LIST);
            user.setRequests(Collections.EMPTY_LIST);

            createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect OTP");
        }
    }

    public User updateUserDetails(String userId, User userDetails) {
        // Fetch the user from the database
        User existingUser = userRepository.getUser(userId);

        if (existingUser == null) {
            throw new NotFoundException("User not found");
        }

        // Update user details (you may want to selectively update fields)
        existingUser.setFullName(
                userDetails.getFullName() != null ? userDetails.getFullName() : existingUser.getFullName());
        existingUser.setEmail(userDetails.getEmail() != null ? userDetails.getEmail() : existingUser.getEmail());
        // Add other fields as needed...

        // Save updated user back to the database
        userRepository.updateUserById(userId, existingUser);

        return existingUser;
    }

    public boolean addHighlight(String userId, Highlights highlight) {
        User user = userRepository.findUserById(userId);
        if (user != null) {
            user.getHighlights().add(highlight);
            userRepository.updateUserById(userId, user);
            return true;
        }
        return false;
    }

    public boolean removeHighlight(String userId, int highlightIndex) {
        User user = userRepository.findUserById(userId);
        if (user != null && highlightIndex >= 0 && highlightIndex < user.getHighlights().size()) {
            user.getHighlights().remove(highlightIndex);
            userRepository.updateUserById(userId, user);
            return true;
        }
        return false;
    }

    // testing part
    public User createUser(User user) {
        return userRepository.createUser(user);
    }

    public User getUser(String userId) {
        return userRepository.getUser(userId);
    }

    // testing part
    public User updateUserById(String userId, User user) {
        return userRepository.updateUserById(userId, user);
    }

    public String follow(String userId, String followingId) {
        return userRepository.follow(userId, followingId);
    }

    public String unfollow(String userId, String followingId) {
        return userRepository.unfollow(userId, followingId);
    }

    public User signIn(User userDetails) {
        // Validate email format
        if (!EmailValidator.isValidEmail(userDetails.getEmail())) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        System.out.println("here in userService signIn");

        // Check if a session is already active
        if (sessionManager.isUserSignedIn(userDetails)) {
            throw new UnauthorizedException("User is already signed in.");
        }

        // Check if email exists
        User user = userRepository.signIn(userDetails);
        if (user == null) {
            throw new NotFoundException("Email not registered.");
        }

        // Verify password
        if (!passwordEncoder.matches(userDetails.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Incorrect password.");
        }

        return user;
    }

    public boolean deleteUserById(String userId) {
        try {
            if (userRepository.findUserById(userId) != null) {
                return userRepository.deleteById(userId);
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean signOut(User user) {
        return sessionManager.signOut(user);
    }

    public User getUserByEmail(String email) {
        // Assuming you have a method in your UserRepository to find users by email
        return userRepository.findByEmail(email);
    }

}
