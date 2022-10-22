package ru.sladkkov.jwtauthentification.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequest {
    private String username;

    private String password;

    private String firstName;

    private String lastName;

}
