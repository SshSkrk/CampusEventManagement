package sasha.org.campuseventmanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import sasha.org.campuseventmanagement.dto.EventDTO;
import sasha.org.campuseventmanagement.dto.PersonDTO;
import sasha.org.campuseventmanagement.repo.EventRepository;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Data @NoArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String course;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_event",
    joinColumns = @JoinColumn (name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "event_id"))
    @JsonIgnore @JsonManagedReference
    private List<Event> events = new ArrayList<>();

    public Person(String username, String email, String password, String role, String firstName, String lastName,
                  String course){
        this.username = username;
        this.email = email;
        this.verified = false;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.course = course;

    }

    public Person(Integer id, String username, String email, String password, String role, String firstName, String lastName,
                  String course, List<Event> events){
        this.id = id;
        this.username = username;
        this.email = email;
        this.verified = false;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.course = course;
        this.events = events;

    }

    public static Person of(PersonDTO personDTO, EventRepository eventRepository) {
        Person person = new Person();
        if (personDTO.getId() != null) {
            person.setId(personDTO.getId());
        }
        person.setUsername(personDTO.getUsername());
        person.setEmail(personDTO.getEmail());
        person.setVerified(personDTO.isVerified());
        person.setPassword(personDTO.getPassword());
        person.setRole(personDTO.getRole());
        person.setFirstName(personDTO.getFirstName());
        person.setLastName(personDTO.getLastName());
        person.setCourse(personDTO.getCourse());
        if (personDTO.getEvents() != null) {
            List<Event> eventList = personDTO.getEvents().stream()
                    .map(dto -> eventRepository.findById(dto.getId()).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            person.setEvents(eventList);
        }
        return person;
    }

    public PersonDTO toPersonDTO() {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setId(this.id);
        personDTO.setUsername(this.username);
        personDTO.setEmail(this.email);
        personDTO.setVerified(this.verified);
        personDTO.setPassword(this.password);
        personDTO.setRole(this.role);
        personDTO.setFirstName(this.firstName);
        personDTO.setLastName(this.lastName);
        personDTO.setCourse(this.course);
        if (this.events != null) {
            List<EventDTO> eventDTOList = this.events.stream()
                    .map(event -> {
                        EventDTO dto = new EventDTO();
                        dto.setId(event.getId());
                        dto.setTitle(event.getTitle());
                        // omit personList to avoid recursion
                        return dto;
                    })
                    .collect(Collectors.toList());
            personDTO.setEvents(eventDTOList);
        }
        return personDTO;
    }
}