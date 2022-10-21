package ru.sladkkov.jwtauthentification.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.sladkkov.jwtauthentification.model.Role;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class SignUpRequest {
    private String username;
    private String password;
    private List<Role> roles;
}
