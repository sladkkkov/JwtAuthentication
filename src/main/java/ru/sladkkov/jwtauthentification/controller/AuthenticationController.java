package ru.sladkkov.jwtauthentification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import ru.sladkkov.jwtauthentification.dto.request.LoginRequest;
import ru.sladkkov.jwtauthentification.dto.response.JwtResponse;
import ru.sladkkov.jwtauthentification.model.User;
import ru.sladkkov.jwtauthentification.security.jwt.TokenProvider;
import ru.sladkkov.jwtauthentification.service.UserService;

@RestController
@RequestMapping(value = "api/v1")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final TokenProvider tokenProvider;

    private final UserService userService;

    public AuthenticationController(AuthenticationManager authenticationManager, TokenProvider tokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            String username = loginRequest.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, loginRequest.getPassword()));
            User user = userService.findByUsername(username);
            String token = tokenProvider.createJwtToken(username, user.getRoles());

            return ResponseEntity.ok(new JwtResponse(user.getId(), token, username, user.getRoles()));
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/admin/1")
    public ResponseEntity<String> getString() {
        return ResponseEntity.ok("Проверка преАвторайза ");
    }
}
