package sasha.org.campuseventmanagement.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sasha.org.campuseventmanagement.dto.EventDTO;
import sasha.org.campuseventmanagement.dto.PersonDTO;
import sasha.org.campuseventmanagement.model.Event;
import sasha.org.campuseventmanagement.model.Person;
import sasha.org.campuseventmanagement.repo.EventRepository;
import sasha.org.campuseventmanagement.repo.PersonRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;

@Service
public class EventService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final EventRepository eventRepository;
    private final PersonRepository personRepository;

    public EventService(EventRepository eventRepository, PersonRepository personRepository) {
        this.eventRepository = eventRepository;
        this.personRepository = personRepository;
    }

    @PostConstruct
    public void init() {
        // Create folder if it doesn't exist
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }
    }

    //ADMIN
    @Transactional
    public boolean createEvent(EventDTO eventDTO) {
        Optional<Event> eventOptional = eventRepository.findEventById(eventDTO.getId());
        if (eventOptional.isEmpty()) {
            eventRepository.save(Event.of(eventDTO, personRepository));
            return true;
        }
        return false;
    }

    //ADMIN
    @Transactional
    public boolean updateEvent(EventDTO eventDTO) {
        Optional<Event> eventOptional = eventRepository.findEventById(eventDTO.getId());
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            event.setTitle(eventDTO.getTitle());
            event.setLocation(eventDTO.getLocation());
            event.setDate(eventDTO.getDate());
            // Do NOT reset photoFilenames or personList here

            eventRepository.save(event);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public EventDTO getEventById(int id) {
        Optional<Event> event = eventRepository.findEventById(id);
        if (event.isEmpty()) {
            return null;
        }
        EventDTO eventDTO = event.get().toEventDTO();
        return eventDTO;
    }

    public boolean existsByTitle(String title) {
        return eventRepository.existsByTitle(title);
    }

    @Transactional(readOnly = true)
    public List<EventDTO> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        if (events.isEmpty()) {
            return new ArrayList<>();
        }
        List<EventDTO> eventDTO = new ArrayList<>();
        for (Event event : events) {
            eventDTO.add(event.toEventDTO());
        }
        return eventDTO;
    }

    @Transactional(readOnly = true)
    public List<EventDTO> getEventsByLocation(String location) {
        List<Event> events = eventRepository.findAllByLocation(location);
        if (events.isEmpty()) {
            return new ArrayList<>();
        }
        List<EventDTO> eventDTO = new ArrayList<>();
        for (Event event : events) {
            eventDTO.add(event.toEventDTO());
        }
        return eventDTO;
    }

    @Transactional(readOnly = true)
    public List<EventDTO> getEventsByDate(LocalDate date) {
        List<Event> events = eventRepository.findAllByDate(date);
        if (events.isEmpty()) {
            return new ArrayList<>();
        }
        List<EventDTO> eventDTO = new ArrayList<>();
        for (Event event : events) {
            eventDTO.add(event.toEventDTO());
        }
        return eventDTO;
    }

    @Transactional(readOnly = true)
    public List<PersonDTO> getPersonSByTitle(String title) {
        Optional<Event> eventOpt = eventRepository.findByTitle(title);
        if (eventOpt.isPresent()) {
            List<Person> personList = eventOpt.get().getPersonList();
            if (personList.isEmpty()) {
                return new ArrayList<>();
            }
            List<PersonDTO> personDTOS = new ArrayList<>();
            for (Person person : personList) {
                personDTOS.add(person.toPersonDTO());
            }
            return personDTOS;
        }
        return new ArrayList<>();
    }

    // ADMIN
    @Transactional
    public boolean deleteEventById(Integer id) {
        Optional<Event> eventOptional = eventRepository.findEventById(id);
        if (eventOptional.isPresent()) {
            Event eventToDelete = eventOptional.get();
            List<Person> personList = eventToDelete.getPersonList();
            if (!personList.isEmpty()) {
                for (Person person : personList) {
                    person.getEvents().removeIf(e -> e.getId().equals(eventToDelete.getId()));
                    personRepository.save(person);
                }
            }
            eventRepository.delete(eventToDelete);
            return true;
        }
        return false;
    }


    @Transactional
    public boolean addPersonToEvent(Integer eventId, Integer personId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Person not found"));

        if (!event.getPersonList().contains(person)) {
            event.getPersonList().add(person);
        }
        if (!person.getEvents().contains(event)) {
            person.getEvents().add(event);
        }

         personRepository.save(person);
        return true;
    }

    public void uploadPhoto(Integer eventId, MultipartFile file) throws IOException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Save file to disk
        String filename = saveFileToDisk(file);

        // Add the filename to the event’s photos
        List<String> photoList = event.getPhotoFilenames();
        if (photoList == null) {
            photoList = new ArrayList<>();
        }

        photoList.add(filename);
        event.setPhotoFilenames(photoList);

        // Save the updated event
        eventRepository.save(event);
    }

    private String saveFileToDisk(MultipartFile file) throws IOException {
        String originalFilename = Paths.get(file.getOriginalFilename()).getFileName().toString();

        Path uploadPath = Paths.get(uploadDir);

        // Create the directory if it doesn't exist
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(originalFilename);

        // Save the file
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        return originalFilename;
    }

    public Resource downloadPhoto(String filename) throws IOException {
        Path filePath = Paths.get(uploadDir, filename);
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found: " + filename);
        }
        return new UrlResource(filePath.toUri());
    }
}
