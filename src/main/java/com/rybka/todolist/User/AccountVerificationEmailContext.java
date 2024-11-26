package com.rybka.todolist.User;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@Setter
public class AccountVerificationEmailContext extends AbstractEmailContext {
    private String token;

    public <T> void init(T context){
        User customer; // we pass the customer informati
        customer = (User) context;
        put("firstName", customer.getUsername());
        setTemplateLocation("emails/email-verification");
        setSubject("Complete your registration");
        setFrom("dancrafak@seznam.cz");
        setTo(customer.getEmail());
    }
    public void setToken(String token) {
        this.token = token;
        put("token", token);
    }
    public void buildVerificationUrl(final String baseURL, final String token){
        final String url= UriComponentsBuilder.fromHttpUrl(baseURL)
                .path("/verify").queryParam("token", token).toUriString();
        put("verificationURL", url);
    }
}
