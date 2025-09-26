package sasha.org.campuseventmanagement.controller;

import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sasha.org.campuseventmanagement.dto.EventDTO;
import sasha.org.campuseventmanagement.dto.LogDTO;
import sasha.org.campuseventmanagement.dto.PersonDTO;
import sasha.org.campuseventmanagement.service.EventService;
import sasha.org.campuseventmanagement.service.LogService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EventController {
    private final EventService eventService;
    private final LogService logService;

    public EventController(EventService eventService, LogService logService) {
        this.eventService = eventService;
        this.logService = logService;
    }

    //ADMIN
    @PostMapping(value = "/admin/createEvent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> createEvent(@RequestBody EventDTO eventDTO) {
        try {
            boolean created = eventService.createEvent(eventDTO);
            if (!created) {
                LogDTO logDTO = new LogDTO();
                logDTO.setDate(new Date());
                logDTO.setMessage("Business validation failed during event creation. Title: " + eventDTO.getTitle());
                logService.createLog(logDTO);
            }
            return created
                    ? ResponseEntity.status(HttpStatus.CREATED).body(true)  // Created successfully
                    : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false); // Failed to create
        }catch (Exception e) {
            LogDTO logDTO = new LogDTO();
            logDTO.setDate(new Date());
            logDTO.setMessage("Exception during event creation. Title: " + eventDTO.getTitle());
            logService.createLog(logDTO);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    //ADMIN
    @PostMapping("/admin/updateEvent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> updateEvent(@RequestBody EventDTO eventDTO) {
        try {
            boolean updated = eventService.updateEvent(eventDTO);
            if (!updated) {
                LogDTO logDTO = new LogDTO();
                logDTO.setDate(new Date());
                logDTO.setMessage("Business validation failed during event update. Title: " + eventDTO.getTitle());
                logService.createLog(logDTO);
            }
            return updated
                    ? ResponseEntity.ok(true)
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }catch (Exception e) {
            LogDTO logDTO = new LogDTO();
            logDTO.setDate(new Date());
            logDTO.setMessage("Exception during event update. Title " + eventDTO.getTitle());
            logService.createLog(logDTO);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }

    }
    //done
    @GetMapping("/getAllEvents")
    public List<EventDTO> getAllEvents() {
        try {
            List<EventDTO> eventDTOS = eventService.getAllEvents();
            if (eventDTOS.isEmpty()) {
                LogDTO logDTO = new LogDTO();
                logDTO.setDate(new Date());
                logDTO.setMessage("No events found for query getAllEvents");
                logService.createLog(logDTO);
                return new ArrayList<>();
            }
            return eventDTOS;  // Return list of event
        }catch (Exception e) {
            LogDTO logDTO = new LogDTO();
            logDTO.setDate(new Date());
            logDTO.setMessage("Exception during event getAllEvents" );
            logService.createLog(logDTO);
            return new ArrayList<>();
        }
    }

    @GetMapping("/getEventById/{id}")
    public EventDTO getEventById(@PathVariable int id) {
        try {
            EventDTO eventDTO = eventService.getEventById(id);
            if (eventDTO == null) {
                LogDTO logDTO = new LogDTO();
                logDTO.setDate(new Date());
                logDTO.setMessage("No events found for query getEventById: " + id);
                logService.createLog(logDTO);
                return null;
            }
            return eventDTO;  // Return event
        }catch (Exception e) {
            LogDTO logDTO = new LogDTO();
            logDTO.setDate(new Date());
            logDTO.setMessage("Exception during event getEventById. Id: " + id);
            logService.createLog(logDTO);
            return null;
        }
    }

    //done
    @GetMapping("/getEventsByLocation/{location}")
    public List<EventDTO> getEventsByLocation(@PathVariable String location) {
        try {
            List<EventDTO> eventDTOS = eventService.getEventsByLocation(location);
            if (eventDTOS.isEmpty()) {
                LogDTO logDTO = new LogDTO();
                logDTO.setDate(new Date());
                logDTO.setMessage("No events found for query getEventsByLocation: " + location);
                logService.createLog(logDTO);
                return new ArrayList<>();
            }
            return eventDTOS; // Return list of events by location
        }catch (Exception e) {
            LogDTO logDTO = new LogDTO();
            logDTO.setDate(new Date());
            logDTO.setMessage("Exception during event getEventsByLocation: " + location);
            logService.createLog(logDTO);
            return new ArrayList<>();
        }
    }
    //done
    @GetMapping("/getEventsByDate/{date}")
    public List<EventDTO> getEventsByDate(@PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate  date) {
        try {
            List<EventDTO> eventDTOS = eventService.getEventsByDate(date);
            if (eventDTOS.isEmpty()) {
                LogDTO logDTO = new LogDTO();
                logDTO.setDate(new Date());
                logDTO.setMessage("No events found for query getEventsByDate: " + date);
                logService.createLog(logDTO);
                return new ArrayList<>();
            }
            return eventDTOS; // Return list of events by date
        }catch (Exception e) {
            LogDTO logDTO = new LogDTO();
            logDTO.setDate(new Date());
            logDTO.setMessage("Exception during event getEventsByDate: " + date);
            logService.createLog(logDTO);
            return new ArrayList<>();
        }
    }

    //ADMIN
    @GetMapping("/admin/getPersonListByTitle/{title}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PersonDTO> getPersonListByTitle(@PathVariable String title) {
        try {
            List<PersonDTO> personDTOS = eventService.getPersonSByTitle(title);
            if (personDTOS.isEmpty()) {
                LogDTO logDTO = new LogDTO();
                logDTO.setDate(new Date());
                logDTO.setMessage("No personS found for query getPersonListByTitle: " + title);
                logService.createLog(logDTO);
                return new ArrayList<>();
            }
            return personDTOS; // Return person list by event title
        }catch (Exception e) {
            LogDTO logDTO = new LogDTO();
            logDTO.setDate(new Date());
            logDTO.setMessage("Exception during event getPersonListByTitle: " + title);
            logService.createLog(logDTO);
            return new ArrayList<>();
        }
    }

    //ADMIN
    @DeleteMapping("/admin/deleteEventById/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> deleteEventById(@PathVariable("id") Integer id) {
        try {
            boolean deleted = eventService.deleteEventById(id);
            if (!deleted) {
                LogDTO logDTO = new LogDTO();
                logDTO.setDate(new Date());
                logDTO.setMessage("Event deletion failed. Id: " + id);
                logService.createLog(logDTO);
            }
            return deleted
                    ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(true)  // Deleted successfully
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false); // Customer not found to delete
        }catch (Exception e) {
            LogDTO logDTO = new LogDTO();
            logDTO.setDate(new Date());
            logDTO.setMessage("Exception during event deleteEventById: " + id);
            logService.createLog(logDTO);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @PostMapping("/addPersonToEvent")
    public ResponseEntity<Boolean> addPersonToEvent(@RequestParam Integer eventId,
                                                   @RequestParam Integer personId) {
        try {
            boolean added = eventService.addPersonToEvent(eventId, personId);
            if (!added) {
                LogDTO logDTO = new LogDTO();
                logDTO.setDate(new Date());
                logDTO.setMessage("Adding person to event failed. Person id: " + personId + " Event id: " + eventId);
                logService.createLog(logDTO);
            }return added
                    ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(true)  // Person added to event
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false); // Person not added to event

        } catch (Exception e) {
            LogDTO logDTO = new LogDTO();
            logDTO.setDate(new Date());
            logDTO.setMessage("Exception during event addPersonToEvent. Person id: " + personId + " Event id: " + eventId);
            logService.createLog(logDTO);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @PostMapping("/uploadPictures/{eventId}")
    public ResponseEntity<String> uploadPhoto(@PathVariable Integer eventId,
                                              @RequestParam("file") MultipartFile file) {
        try {
            eventService.uploadPhoto(eventId, file);
            return ResponseEntity.ok("Photo uploaded successfully");
        } catch (Exception e) {
            LogDTO logDTO = new LogDTO();
            logDTO.setDate(new Date());
            logDTO.setMessage("Photo upload failed. Event id: " + eventId + " File name: " + file.getOriginalFilename());
            logService.createLog(logDTO);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/downloadPictures/{filename:.+}")  // note the regex to allow dots in filename
    public ResponseEntity<Resource> downloadPhoto(@PathVariable String filename) {
        try {
            Resource file = eventService.downloadPhoto(filename);

            // Try to determine content type dynamically (optional but recommended)
            String contentType = "application/octet-stream";
            try {
                Path path = Paths.get(file.getFile().getAbsolutePath());
                String detectedType = Files.probeContentType(path);
                if (detectedType != null) {
                    contentType = detectedType;
                }
            } catch (IOException ignored) {
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + file.getFilename() + "\"")  // serve inline
                    .body(file);
        } catch (Exception e) {
            LogDTO logDTO = new LogDTO();
            logDTO.setDate(new Date());
            logDTO.setMessage("Download failed. Filename: " + filename);
            logService.createLog(logDTO);
            return ResponseEntity.notFound().build();
        }
    }
}
