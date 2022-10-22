package ru.sladkkov.jwtauthentification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import ru.sladkkov.jwtauthentification.dto.request.LoginRequest;
import ru.sladkkov.jwtauthentification.dto.response.JwtResponse;
import ru.sladkkov.jwtauthentification.model.User;
import ru.sladkkov.jwtauthentification.security.jwt.TokenProvider;

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;

    private final TokenProvider tokenProvider;

    private final UserService userService;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager, TokenProvider tokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }


    public JwtResponse login(LoginRequest loginRequest) {
        try {
            String username = loginRequest.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, loginRequest.getPassword()));
            User user = userService.findByUsername(username);
            String token = tokenProvider.createJwtToken(username, user.getRoles());

            return JwtResponse.builder()
                    .id(user.getId())
                    .token(token)
                    .username(username)
                    .roles(user.getRoles())
                    .build();
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password, try again");
        }
    }
}
