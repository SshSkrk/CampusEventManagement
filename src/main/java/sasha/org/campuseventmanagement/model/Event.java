package sasha.org.campuseventmanagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import sasha.org.campuseventmanagement.dto.EventDTO;
import sasha.org.campuseventmanagement.dto.PersonDTO;
import sasha.org.campuseventmanagement.repo.PersonRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Data @NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToMany(mappedBy = "events")
    private List<Person> personList= new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "event_photos", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "photo_filename")
    @JsonIgnore @JsonBackReference
    private List<String> photoFilenames = new ArrayList<>();

    public Event(String title, String location, LocalDate  date) {
        this.title = title;
        this.location = location;
        this.date = date;

    }

    public Event(Integer id, String title, String location, LocalDate  date, List<Person> users, List<String> photoFilenames) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.date = date;
        this.personList = users;
        this.photoFilenames = photoFilenames;

    }

    public static Event of(EventDTO eventDTO, PersonRepository personRepository) {
        Event event = new Event();
        if (eventDTO.getId() != null) {
            event.setId(eventDTO.getId());
        }
        event.setTitle(eventDTO.getTitle());
        event.setLocation(eventDTO.getLocation());
        event.setDate(eventDTO.getDate());
        if (eventDTO.getPersonList() != null) {
            List<Person> personEntities = eventDTO.getPersonList().stream()
                    .map(dto -> personRepository.getPersonById(dto.getId()).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            event.setPersonList(personEntities);
        }
        event.setPhotoFilenames(eventDTO.getPhotoFilenames());
        return event;
    }

    public EventDTO toEventDTO() {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setId(this.id);
        eventDTO.setTitle(this.title);
        eventDTO.setLocation(this.location);
        eventDTO.setDate(this.date);
        if (this.personList != null) {
            List<PersonDTO> personDTOs = this.personList.stream()
                    .map(person -> {
                        PersonDTO dto = new PersonDTO();
                        dto.setId(person.getId());
                        dto.setUsername(person.getUsername());
                        // omit events list here
                        return dto;
                    })
                    .collect(Collectors.toList());
            eventDTO.setPersonList(personDTOs);
        }

        eventDTO.setPhotoFilenames(this.photoFilenames);
        return eventDTO;
    }

}