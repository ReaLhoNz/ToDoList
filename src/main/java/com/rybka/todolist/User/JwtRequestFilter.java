package com.rybka.todolist.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;

    @Autowired
    public JwtRequestFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = null;
        String username = null;

        // Check if there is a cookie containing the JWT token
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            // Search for the cookie that contains the JWT token
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();  // Extract the JWT token from the cookie
                    break;
                }
            }
        }

        try {
            if (token != null) {
                // Extract the username from the JWT token
                username = jwtUtil.extractUsername(token);

                // If there's a username and no authentication is set in the context, validate the token
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    User user = userRepository.getByEmail(username);  // Load user details
                    if (jwtUtil.validateToken(token, user)) {  // Validate the token
                        // If the token is valid, set the authentication in the security context
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(user,null);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed: " + e.getMessage());
            return;
        }

        // Continue the filter chain
        chain.doFilter(request, response);
    }
}
