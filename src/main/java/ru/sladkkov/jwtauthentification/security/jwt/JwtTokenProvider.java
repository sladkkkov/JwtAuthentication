package ru.sladkkov.jwtauthentification.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.sladkkov.jwtauthentification.exception.JwtAuthenticationException;
import ru.sladkkov.jwtauthentification.model.Role;
import ru.sladkkov.jwtauthentification.security.UserDetailsServiceImpl;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {
    private final UserDetailsServiceImpl userDetailsService;
    @Value("${jwt.token.secret}")
    private String jwtSecret;
    @Value("${jwt.token.expired}")
    private String jwtExpirationMs;

    @Autowired
    public JwtTokenProvider(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        jwtSecret = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
    }

    public String createJwtToken(String username, List<Role> roles) {
        return Jwts.builder()
                .setClaims(getClaims(username, roles))
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(Long.parseLong(jwtExpirationMs))))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    private Claims getClaims(String username, List<Role> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", getRoleNames(roles));
        return claims;
    }

    private List<String> getRoleNames(List<Role> roles) {
        return roles.stream()
                .map(role -> role.getName().getAuthority())
                .toList();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }


    public boolean validateToken(String token) {
        Jws<Claims> claimsJws;

        try {
            claimsJws = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
        } catch (JwtAuthenticationException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("Jwt token invalid or expired");
        }

        return !claimsJws.getBody().getExpiration().before(new Date());
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");

    }
}
