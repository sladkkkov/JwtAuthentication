package ru.sladkkov.jwtauthentification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sladkkov.jwtauthentification.model.Role;
import ru.sladkkov.jwtauthentification.model.RoleEnum;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleEnum name);



}
