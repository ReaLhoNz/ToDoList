package com.rybka.todolist.User;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.util.StringUtils;

@Controller
public class UserController {
    @Autowired
    private SecureTokenRepository secureTokenRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    TemplateEngine templateEngine;

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "registration.html"; // Serve the registration HTML page
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String email, @RequestParam String password, RedirectAttributes redirectAttributes) throws Exception {
        if (userService.checkIfUserExist(email)) {
            redirectAttributes.addFlashAttribute("error", "A user already exists with this email.");
            return "redirect:/register.html"; // Redirect back to the registration page with the error
        }

        // Create new user if they do not exist
        User user = new User();

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        userService.createUser(user);
        sendRegistrationConfirmationEmail(user);
        redirectAttributes.addFlashAttribute("message", "User registered successfully!");
        return "redirect:/register.html"; // Redirect back to the registration page
    }
    public void sendRegistrationConfirmationEmail(User user) {

        EmailServices emailServices = new EmailServices(templateEngine,mailSender);
        TokenService tokenService = new TokenService(secureTokenRepository);
        SecureToken secureToken= tokenService.createSecureToken();
        secureToken.setUser(user);
        secureTokenRepository.save(secureToken);
        AccountVerificationEmailContext emailContext = new AccountVerificationEmailContext();
        emailContext.init(user);
        emailContext.setToken(secureToken.getToken());
        String baseURL = "http://localhost:8088";
        emailContext.buildVerificationUrl(baseURL, secureToken.getToken());
        try {
            emailServices.sendMail(emailContext);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }
    private static final String REDIRECT_LOGIN= "redirect:/login";


    private final MessageSource messageSource = messageSource();

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }


    @GetMapping("/verify")
    public String verifyCustomer(@RequestParam(required = false) String token, final Model model, RedirectAttributes redirAttr){
        if(StringUtils.isEmpty(token)){
            redirAttr.addFlashAttribute("tokenError", messageSource.getMessage("user.registration.verification.missing.token", null,LocaleContextHolder.getLocale()));
            return REDIRECT_LOGIN;
        }
        try {
            userService.verifyUser(token);
        } catch (Exception e) {
            redirAttr.addFlashAttribute("tokenError", messageSource.getMessage("user.registration.verification.invalid.token", null, LocaleContextHolder.getLocale()));
            return REDIRECT_LOGIN;
        }

        redirAttr.addFlashAttribute("verifiedAccountMsg", messageSource.getMessage("user.registration.verification.success", null,LocaleContextHolder.getLocale()));
        return REDIRECT_LOGIN;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse request) {
        try {
            String token = userService.validateUser(loginRequest.getEmail(), loginRequest.getPassword());
            this.setJwtCookie(request, token);
            return ResponseEntity.ok("response Send"); // Send token in the response
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    public void setJwtCookie(HttpServletResponse response, String jwt) {
        // Create a new cookie to store the JWT
        Cookie cookie = new Cookie("jwt", jwt);

        // Set the cookie as HTTP-only to prevent JS access
        cookie.setHttpOnly(true);

        // Only send the cookie over HTTPS to ensure security
        cookie.setSecure(true);

        // Set the cookie path to "/" so it's sent with every request to the domain
        cookie.setPath("/");

        // Set the cookie expiration time (e.g., 1 day)
        cookie.setMaxAge(60 * 60 * 24);

        // Add the cookie to the response
        response.addCookie(cookie);
    }
    }

