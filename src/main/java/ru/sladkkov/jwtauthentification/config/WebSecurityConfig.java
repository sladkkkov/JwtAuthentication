package ru.sladkkov.jwtauthentification.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.sladkkov.jwtauthentification.security.UserDetailsServiceImpl;
import ru.sladkkov.jwtauthentification.security.jwt.JwtTokenConfigurer;
import ru.sladkkov.jwtauthentification.security.jwt.JwtTokenProvider;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsService;

    private final JwtTokenProvider jwtTokenProvider;


    @Autowired
    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, JwtTokenProvider jwtTokenProvider) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtTokenProvider authTokenFilter() {
        return new JwtTokenProvider(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("api/v1/login").permitAll()
                .antMatchers("api/v1/register").permitAll()
                .antMatchers("api/v1/admin/**").hasRole("ADMIN")
                .antMatchers("api/v1/user/**").hasRole("USER")
                .and()
                .apply(new JwtTokenConfigurer(jwtTokenProvider));
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("api/v1/login").permitAll()
                .antMatchers("api/v1/register").permitAll()
                .antMatchers("api/v1/admin/**").hasRole("ADMIN")
                .antMatchers("api/v1/user/**").hasRole("USER")
                .and()
                .apply(new JwtTokenConfigurer(jwtTokenProvider));
        return http.build();
    }
}
