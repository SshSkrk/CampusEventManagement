package sasha.org.campuseventmanagement.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sasha.org.campuseventmanagement.dto.EventDTO;
import sasha.org.campuseventmanagement.dto.PersonDTO;
import sasha.org.campuseventmanagement.model.Event;
import sasha.org.campuseventmanagement.model.Person;
import sasha.org.campuseventmanagement.repo.EventRepository;
import sasha.org.campuseventmanagement.repo.PersonRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final EventRepository eventRepository;
    private final PasswordEncoder encoder;

    public PersonService(PersonRepository personRepository,EventRepository eventRepository ,PasswordEncoder encoder) {
        this.personRepository = personRepository;
        this.eventRepository = eventRepository;
        this.encoder = encoder;
    }

    @Transactional
    public boolean createPerson(PersonDTO personDTO) {
        Optional<Person> personVar = personRepository.getPersonByUsername(personDTO.getUsername());
        if (personVar.isEmpty()) {
            Person newPerson = Person.of(personDTO, eventRepository);
            newPerson.setPassword(encoder.encode(personDTO.getPassword()));
            personRepository.save(newPerson);
            System.out.println("Saved Person ID: " + newPerson.getId());
            return true;
        }
        return false;
    }

    public boolean existsByUsername(String username) {
        return personRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return personRepository.existsByEmail(email);
    }

    @Transactional
    public boolean updatePerson(PersonDTO personDTO) {
        Optional<Person> personVar = personRepository.getPersonByUsername(personDTO.getUsername());
        if (personVar.isPresent()) {
            Person person = personVar.get();
            person.setEmail(personDTO.getEmail());
            person.setFirstName(personDTO.getFirstName());
            person.setLastName(personDTO.getLastName());
            person.setCourse(personDTO.getCourse());
            if (personDTO.getEvents() != null) {
                List<Event> managedEvents = personDTO.getEvents().stream()
                        .map(eventDTO -> eventRepository.findById(eventDTO.getId()).orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                person.setEvents(managedEvents);
            }

            // Only encode if it's not already encoded (BCrypt starts with "$2a$")
            String incomingPassword = personDTO.getPassword();
            if (!incomingPassword.startsWith("$2a$")) {
                person.setPassword(encoder.encode(incomingPassword));
            } else {
                person.setPassword(incomingPassword); // already encoded
            }
            personRepository.save(person);
            return true;
        }
        return false;
    }

    //ADMIN
    @Transactional(readOnly = true)
    public List<PersonDTO> getAllPersonS() {
        List<Person> personList = personRepository.findAll();
        if (personList.isEmpty()) {
            return new ArrayList<>();
        }
        List<PersonDTO> personDTOS = new ArrayList<>();
        for (Person person : personList) {
            personDTOS.add(person.toPersonDTO());
        }
        return personDTOS;
    }

    //ADMIN
    @Transactional(readOnly = true)
    public List<PersonDTO> getPersonListByCourse(String course) {
        List<Person> personList = personRepository.findAllByCourse(course);
        if (personList.isEmpty()) {
            return new ArrayList<>();
        }
        List<PersonDTO> personDTOS = new ArrayList<>();
        for (Person person : personList) {
            personDTOS.add(person.toPersonDTO());
        }
        return personDTOS;
    }

    //ADMIN
    @Transactional
    public boolean deletePerson(Integer id) {
        Optional<Person> personVar = personRepository.getPersonById(id);
        if (personVar.isPresent()) {
            Person personToDelete = personVar.get();
            List<Event> eventList = personToDelete.getEvents();
            if (!eventList.isEmpty()) {
                for (Event event : eventList) {
                    List<Person> eventPersons = event.getPersonList();
                    eventPersons.removeIf(e -> e.getId().equals(personToDelete.getId()));
                    eventRepository.save(event);
                }
            }
            personRepository.delete(personToDelete);

            // deleting person -> deatach in event persons

            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public PersonDTO getPersonByUsername(String username) {
        Optional<Person> personVar = personRepository.getPersonByUsername(username);
        if (personVar.isEmpty()) {
            return null;
        }
        Person person = personVar.get();
        PersonDTO personDTO = person.toPersonDTO();
        return personDTO;
    }

    @Transactional(readOnly = true)
    public List<EventDTO> getEventsForPerson(int personId) {
       Optional<Person> personOptional = personRepository.getPersonById(personId);
       if (personOptional.isEmpty()) {
           return new ArrayList<>();
       }
       List<Event> eventList = personOptional.get().getEvents();
        List<EventDTO> eventDTOList =eventList.stream()
                .map(event -> event.toEventDTO()).collect(Collectors.toList());
        return eventDTOList;
    }
}
