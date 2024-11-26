package com.rybka.todolist.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import static org.hibernate.cfg.JdbcSettings.USER;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private SecureTokenRepository secureTokenRepository;
    @Autowired
    private JwtUtil jwtUtil;

    public User createUser(User user) throws Exception {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        user.setTimestamp(timestamp);  // Assuming you have a `createdAt` field

        if (checkIfUserExist(user.getEmail())) {
            throw new Exception("User already exists for this email");
        }
        return userRepository.save(user);
    }

    public boolean checkIfUserExist(String email) {
        return userRepository.getByEmail(email) != null;
    }

    public boolean verifyUser(String token) throws Exception {
        TokenService tokenService = new TokenService(secureTokenRepository);
        SecureToken secureToken = tokenService.findByToken(token);
        if (Objects.isNull(secureToken) || !StringUtils.equals(token, secureToken.getToken()) || secureToken.isExpired()) {
            throw new Exception("Token is not valid");
        }
        User user = userRepository.getOne(secureToken.getUser().getId());
        if (Objects.isNull(user)) {
            return false;
        }
        user.setAccountVerified(true);
        userRepository.save(user); // let’s same user details

        // we don’t need invalid password now
        tokenService.removeToken(secureToken);
        return true;
    }

    public String validateUser(String username, String password) {
        User user = userRepository.getByEmail(username);
        if (user == null) {
        }
        String storedPassword = user.getPassword();
        if (passwordEncoder.matches(password, storedPassword)) {
            // Generate token
            String token = jwtUtil.generateToken(user);

            System.out.println(token);
            return token; // Return the generated token
        } else {
            return null;
        }
    }


}

