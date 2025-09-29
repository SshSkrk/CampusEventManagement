package sasha.org.campuseventmanagement.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import sasha.org.campuseventmanagement.model.Event;
import sasha.org.campuseventmanagement.model.Person;
import sasha.org.campuseventmanagement.service.EventService;
import sasha.org.campuseventmanagement.service.PersonService;

import java.time.LocalDate;
import java.util.Date;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner demo(
            final EventService eventService,
            final PersonService personService) {
        return strings -> {

            // Example: create persons only if they don't exist
            if (personService.getPersonByUsername("OS") == null) {
                Person person1 = new Person();
                person1.setUsername("OS");
                person1.setEmail("skorykoleksandra@gmail.com");
                person1.setPassword("123456");
                person1.setRole("ADMIN");
                person1.setFirstName("Oleksandra");
                person1.setLastName("Skoryk");
                person1.setCourse("Computer Science");
                personService.createPerson(person1.toPersonDTO());
            }

            if (personService.getPersonByUsername("AB") == null) {
                Person person2 = new Person();
                person2.setUsername("AB");
                person2.setEmail("bidenko@gmail.com");
                person2.setPassword("123456");
                person2.setRole("USER");
                person2.setFirstName("Anna");
                person2.setLastName("Bidenko");
                person2.setCourse("Computer Science");
                personService.createPerson(person2.toPersonDTO());
            }

            // Create events only if they don't exist
            if (!eventService.existsByTitle("Event 1")) {
                Event event1 = new Event();
                event1.setTitle("Event 1");
                event1.setLocation("Kyiv");
                event1.setDate(LocalDate.now());
                eventService.createEvent(event1.toEventDTO());
            }

            if (!eventService.existsByTitle("Event 2")) {
                Event event2 = new Event();
                event2.setTitle("Event 2");
                event2.setLocation("Brussels");
                event2.setDate(LocalDate.now());
                eventService.createEvent(event2.toEventDTO());
            }

            if (!eventService.existsByTitle("Event 3")) {
                Event event3 = new Event();
                event3.setTitle("Event 3");
                event3.setLocation("Gent");
                event3.setDate(LocalDate.now());
                eventService.createEvent(event3.toEventDTO());
            }
        };
    }
}
