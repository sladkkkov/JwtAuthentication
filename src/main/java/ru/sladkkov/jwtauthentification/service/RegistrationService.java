package ru.sladkkov.jwtauthentification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.sladkkov.jwtauthentification.dto.request.RegisterRequest;
import ru.sladkkov.jwtauthentification.model.RoleEnum;
import ru.sladkkov.jwtauthentification.model.User;
import ru.sladkkov.jwtauthentification.repository.RoleRepository;
import ru.sladkkov.jwtauthentification.repository.UserRepository;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

@Service
public class RegistrationService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public RegistrationService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public void registration(RegisterRequest registerRequest) throws RoleNotFoundException {
        User user = new User(registerRequest.getUsername(), encodePassword(registerRequest), registerRequest.getFirstName(), registerRequest.getLastName());
        userRepository.save(user);
        setUserRole(user);
    }

    private String encodePassword(RegisterRequest registerRequest) {
        return bCryptPasswordEncoder.encode(registerRequest.getPassword());
    }

    private void setUserRole(User user) throws RoleNotFoundException {
        user.setRoles(List.of(roleRepository
                .findByName(RoleEnum.USER)
                .orElseThrow(() -> new RoleNotFoundException("Такой роли не существует"))));
    }
}
