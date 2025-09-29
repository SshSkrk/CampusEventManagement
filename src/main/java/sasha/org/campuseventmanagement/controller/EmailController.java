package sasha.org.campuseventmanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sasha.org.campuseventmanagement.dto.LogDTO;
import sasha.org.campuseventmanagement.model.Person;
import sasha.org.campuseventmanagement.service.EmailService;
import sasha.org.campuseventmanagement.service.LogService;
import sasha.org.campuseventmanagement.service.PersonService;

import java.util.Date;

@RestController
@RequestMapping("/api")
public class EmailController {
    private final PersonService personService;
    private final LogService logService;
    private final EmailService emailService;

    public EmailController(PersonService personService,
                           LogService logService,
                           EmailService emailService) {
        this.personService = personService;
        this.logService = logService;
        this.emailService = emailService;
    }

    @PostMapping("/sendEmail")
    public ResponseEntity<String> sendVerificationEmail(@RequestParam Integer userId) {
        try {
            Person person = personService.getPersonById(userId);

            if (person.isVerified()) {
                return ResponseEntity.badRequest().body("User is already verified.");
            }

            // Build verification link
            //localhost:
            //String link = "http://localhost:8080/api/verify?userId=" + person.getId();

            String link = "https://campuseventmanagement-38e1d2d976b1.herokuapp.com/api/verify?userId=" + person.getId();


            // Send email
            emailService.sendEmail(
                    person.getEmail(),
                    "Verify your email",
                    "Hi " + person.getFirstName() + ",\n\nClick the link below to verify your account:\n" + link
            );

            return ResponseEntity.ok("Verification email sent to " + person.getEmail());

        } catch (RuntimeException ex) {
            LogDTO logDTO = new LogDTO();
            logDTO.setDate(new Date());
            logDTO.setMessage("Exception during sendVerificationEmail. UserId: " + userId);
            logService.createLog(logDTO);
            return ResponseEntity.badRequest().body("❌ " + ex.getMessage());
        }
    }
}
