package com.gustavo.springsecurity.controller;

import com.gustavo.springsecurity.dto.LoginRequestDTO;
import com.gustavo.springsecurity.dto.LoginResponseDTO;
import com.gustavo.springsecurity.entities.User;
import com.gustavo.springsecurity.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class Tokencontroller {

    private final JwtEncoder jwtEncoder;

    private final UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder;

    public Tokencontroller(JwtEncoder jwtEncoder, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {

        var user = userRepository.findByUsername(loginRequestDTO.username());

        if (user.isEmpty() || !user.get().isLoginCorrect(loginRequestDTO, passwordEncoder)) {
            throw new BadCredentialsException("User or Password is invalid!");
        }

        var now = Instant.now();
        var expiresIn = 300L;

        var claims = JwtClaimsSet
                .builder()
                .issuer("backend-Spring-Security")
                .subject(user.get().getUserId().toString())
                .expiresAt(now.plusSeconds(expiresIn))
                .issuedAt(now)
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(new LoginResponseDTO(jwtValue, expiresIn));

    }
}
