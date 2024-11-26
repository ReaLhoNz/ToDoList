package com.rybka.todolist.User;

import com.rybka.todolist.User.SecureToken;
import com.rybka.todolist.User.SecureTokenRepository;
import lombok.Getter;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class TokenService {

    private static final BytesKeyGenerator DEFAULT_TOKEN_GENERATOR = KeyGenerators.secureRandom(15);

    @Getter
    private int tokenValidityInSeconds = 18000;


    private final SecureTokenRepository secureTokenRepository;
    @Autowired
    public TokenService(SecureTokenRepository secureTokenRepository) {
        this.secureTokenRepository = secureTokenRepository;
    }


    // Create a secure token and save it
    public SecureToken createSecureToken() {
        byte[] tokenBytes = DEFAULT_TOKEN_GENERATOR.generateKey();
        String tokenValue = Hex.encodeHexString(tokenBytes);  // Converts to Hex string

        // Create a SecureToken instance and set its properties
        SecureToken secureToken = new SecureToken();
        secureToken.setToken(tokenValue);
        System.out.println(tokenValidityInSeconds);
        secureToken.setExpireAt(LocalDateTime.now().plusSeconds(getTokenValidityInSeconds()));

        // Save the token
        this.saveSecureToken(secureToken);

        return secureToken;
    }

    // Save the SecureToken to the database
    public void saveSecureToken(SecureToken token) {
        secureTokenRepository.save(token);
    }

    // Find a token by its value
    public SecureToken findByToken(String token) {
        return secureTokenRepository.findByToken(token);
    }

    // Remove a token from the database
    public void removeToken(SecureToken token) {
        secureTokenRepository.delete(token);
    }

    // Remove a token by its token value
    public void removeTokenByToken(String token) {
        secureTokenRepository.removeByToken(token);
    }
}
