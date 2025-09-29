package sasha.org.campuseventmanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sasha.org.campuseventmanagement.dto.EventDTO;
import sasha.org.campuseventmanagement.dto.LogDTO;
import sasha.org.campuseventmanagement.dto.PersonDTO;
import sasha.org.campuseventmanagement.model.Person;
import sasha.org.campuseventmanagement.service.LogService;
import sasha.org.campuseventmanagement.service.PersonService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PersonController {
    private final PersonService personService;
    private final LogService logService;

    public PersonController(PersonService personService, LogService logService) {
        this.personService = personService;
        this.logService = logService;
    }
    @PostMapping(value = "/createPerson")
    public ResponseEntity<?> createPerson(@RequestBody PersonDTO personDTO) {
        try {
            if (personService.existsByUsername(personDTO.getUsername())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
            }

            if (personService.existsByEmail(personDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
            }

            boolean created = personService.createPerson(personDTO);

            if (!created) {
                LogDTO logDTO = new LogDTO();
                logDTO.setDate(new Date());
                logDTO.setMessage("Creation of person failed. PersonUsername: " + personDTO.getUsername());
                logService.createLog(logDTO);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create user");
            }

            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
        }catch (Exception e) {
            LogDTO logDTO = new LogDTO();
            logDTO.setDate(new Date());
            logDTO.setMessage("Exception during event createPerson. PersonUsername: " + personDTO.getUsername());
            logService.createLog(logDTO);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create user");
        }
    }
    //done
    @PostMapping("/updatePerson")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Boolean> updatePerson(@RequestBody PersonDTO personDTO) {
        try {
            boolean updated = personService.updatePerson(personDTO);
            if (!updated) {
                LogDTO logDTO = new LogDTO();
                logDTO.setDate(new Date());
                logDTO.setMessage("Update of person failed. PersonUsername: " + personDTO.getUsername());
                logService.createLog(logDTO);
            }
            return updated
                    ? ResponseEntity.ok(true)
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }catch (Exception e) {
            LogDTO logDTO = new LogDTO();
            logDTO.setDate(new Date());
            logDTO.setMessage("Exception during event createPerson. PersonUsername: " + personDTO.getUsername());
            logService.createLog(logDTO);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);

        }
    }

    //ADMIN
    @GetMapping("/admin/getAllPersonS")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PersonDTO> getAllPersonS() {
        try {
            List<PersonDTO> personDTOS = personService.getAllPersonS();
            if (personDTOS.isEmpty()) {
                LogDTO logDTO = new LogDTO();
                logDTO.setDate(new Date());
                logDTO.setMessage("Getting all personS failed");
                logService.createLog(logDTO);
                return new ArrayList<>();
            }
            return personDTOS; // Return all person list
        }catch (Exception e) {
            LogDTO logDTO = new LogDTO();
            logDTO.setDate(new Date());
            logDTO.setMessage("Exception during event getAllPersonS");
            logService.createLog(logDTO);
            return new ArrayList<>();
        }
    }

    //ADMIN
    @GetMapping("/admin/getPersonListByCourse/{course}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PersonDTO> getPersonListByCourse(@PathVariable String course) {
        try {
            List<PersonDTO> personDTOS = personService.getPersonListByCourse(course);
            if (personDTOS.isEmpty()) {
                LogDTO logDTO = new LogDTO();
                logDTO.setDate(new Date());
                logDTO.setMessage("Getting all personS by course failed. Course: " + course);
                logService.createLog(logDTO);
                return new ArrayList<>();
            }
            return personDTOS; // Return person list by course
        }catch (Exception e) {
            LogDTO logDTO = new LogDTO();
            logDTO.setDate(new Date());
            logDTO.setMessage("Exception during event getPersonListByCourse. Course: " + course);
            logService.createLog(logDTO);
            return new ArrayList<>();
        }
    }

    //ADMIN
    @DeleteMapping("/admin/deletePerson/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> deletePerson(@PathVariable("id") Integer id) {
        try {
            boolean deleted = personService.deletePerson(id);
            if (!deleted) {
                LogDTO logDTO = new LogDTO();
                logDTO.setDate(new Date());
                logDTO.setMessage("Deleting person failed. PersonId: " + id);
                logService.createLog(logDTO);
            }
            return deleted
                    ? ResponseEntity.ok(true)  // Deleted successfully
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false); // Customer not found to delete
        }catch (Exception e) {
            LogDTO logDTO = new LogDTO();
            logDTO.setDate(new Date());
            logDTO.setMessage("Exception during event deletePerson. PersonId: " + id);
            logService.createLog(logDTO);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @GetMapping("/getEventsForPerson/{personId}")
    public List<EventDTO> getEventsForPerson(@PathVariable Integer personId) {
        try {
            List<EventDTO> eventDTOS = personService.getEventsForPerson(personId);
            if (eventDTOS.isEmpty()) {
                LogDTO logDTO = new LogDTO();
                logDTO.setDate(new Date());
                logDTO.setMessage("Getting events for person failed. PersonId: " + personId);
                logService.createLog(logDTO);
                return new ArrayList<>();
            }
            return eventDTOS;
        }catch (Exception e) {
            LogDTO logDTO = new LogDTO();
            logDTO.setDate(new Date());
            logDTO.setMessage("Exception during event getEventsForPerson. PersonId: " + personId);
            logService.createLog(logDTO);
            return new ArrayList<>();
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam Integer userId) {
        try {
            personService.verifyUser(userId);
            return ResponseEntity.ok("✅ Email verified successfully!");
        } catch (RuntimeException ex) {
            LogDTO logDTO = new LogDTO();
            logDTO.setDate(new Date());
            logDTO.setMessage("Exception during event verifyUser. UserId: " + userId);
            logService.createLog(logDTO);
            return ResponseEntity.badRequest().body("❌ " + ex.getMessage());
        }
    }

}
