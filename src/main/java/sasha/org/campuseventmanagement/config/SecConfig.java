package sasha.org.campuseventmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecConfig {

    private final UserDetailsService userDetailsServiceImpl;
    private final PasswordEncoder encoder;

    public SecConfig(UserDetailsService userDetailsServiceImpl, PasswordEncoder encoder) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.encoder = encoder;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsServiceImpl)
                .passwordEncoder(encoder)
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Allow session creation
                )

                .authorizeHttpRequests(auth -> auth


                        .requestMatchers("/", "/index.html", "/register.html", "/login.html","/js/**").permitAll()
                        .requestMatchers("/admin.html").hasAnyRole("ADMIN")
                        .requestMatchers("/profile.html", "/event.html").hasAnyRole("USER", "ADMIN")

                        .requestMatchers("/api/login", "/api/current-user").permitAll()
                        .requestMatchers("/api/logout").hasAnyRole("USER", "ADMIN")

                        .requestMatchers("/api/").permitAll()
                        .requestMatchers("/api/getAllEvents").permitAll()
                        .requestMatchers("/api/getEventById/{id}").permitAll()
                        //.requestMatchers("/api/getEventsByLocation/{location}").permitAll()
                        .requestMatchers("/api/findByLocationPart").permitAll()

                        .requestMatchers("/api/getEventsByDate/{date}").permitAll()
                        .requestMatchers("/api/createPerson").permitAll()

                        .requestMatchers("/uploadPictures/{eventId}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/downloadPictures/{filename:.+}").hasAnyRole("USER", "ADMIN")

                        .requestMatchers("/api/updatePerson").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/getEventsForPerson/{personId}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/addPersonToEvent").hasAnyRole("USER", "ADMIN")

                        .requestMatchers("/api/admin/createEvent").hasAnyRole("ADMIN")
                        .requestMatchers("/api/admin/updateEvent").hasAnyRole("ADMIN")
                        .requestMatchers("/api/admin/deleteEventById/{id}").hasAnyRole("ADMIN")
                        .requestMatchers("/api/admin/getAllCourses").hasAnyRole("ADMIN")
                        .requestMatchers("/api/admin/getPersonListByCourse/{course}").hasAnyRole("ADMIN")
                        .requestMatchers("/api/admin/getPersonListByTitle/{title}").hasAnyRole("ADMIN")
                        .requestMatchers("/api/admin/deletePerson/{id}").hasAnyRole("ADMIN")
                        .requestMatchers("/api/admin/getAllPersonS").hasAnyRole("ADMIN")
                        .requestMatchers("/api/admin/getAllLogs").hasAnyRole("ADMIN")

                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
