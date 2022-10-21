package ru.sladkkov.jwtauthentification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.sladkkov.jwtauthentification.model.Role;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class JwtResponse {
    private Long id;
    private String token;
    private String username;
    private List<Role> roles;
}
