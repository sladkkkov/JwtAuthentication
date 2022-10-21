package ru.sladkkov.jwtauthentification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sladkkov.jwtauthentification.model.User;
import ru.sladkkov.jwtauthentification.dto.request.LoginRequest;
import ru.sladkkov.jwtauthentification.dto.response.JwtResponse;
import ru.sladkkov.jwtauthentification.security.jwt.JwtTokenProvider;
import ru.sladkkov.jwtauthentification.service.UserService;

@RestController
@RequestMapping(value = "api/v1")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    public AuthenticationController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            String username = loginRequest.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, loginRequest.getPassword()));
            User user = userService.findByUsername(username);
            String token = jwtTokenProvider.createJwtToken(username, user.getRoles());

            return ResponseEntity.ok(new JwtResponse(user.getId(), token, username, user.getRoles()));
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
