package sasha.org.campuseventmanagement.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sasha.org.campuseventmanagement.dto.LoginRequestDTO;
import sasha.org.campuseventmanagement.dto.PersonDTO;
import sasha.org.campuseventmanagement.service.PersonService;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final PersonService personService;

    public AuthController(AuthenticationManager authenticationManager, PersonService personService) {
        this.authenticationManager = authenticationManager;
        this.personService = personService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getUsername(),
                            loginRequestDTO.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

            //This line makes the session persist the login
            request.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            PersonDTO personDTO = personService.getPersonByUsername(loginRequestDTO.getUsername());
            return ResponseEntity.ok(personDTO);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate(); // clear session
        SecurityContextHolder.clearContext(); // clear security context
        return ResponseEntity.ok("Logged out successfully.");
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }

        PersonDTO personDTO = personService.getPersonByUsername(authentication.getName());
        if (personDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Return full customer info
        Map<String, Object> response = new HashMap<>();
        response.put("id", personDTO.getId());
        response.put("username", personDTO.getUsername());
        response.put("email", personDTO.getEmail());
        response.put("verified", personDTO.isVerified());
        response.put("role", personDTO.getRole());
        response.put("firstName", personDTO.getFirstName());
        response.put("lastName", personDTO.getLastName());
        response.put("course", personDTO.getCourse());

        return ResponseEntity.ok(response);
    }
}

