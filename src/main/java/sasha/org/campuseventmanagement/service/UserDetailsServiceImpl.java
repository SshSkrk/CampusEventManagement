package sasha.org.campuseventmanagement.service;

import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import sasha.org.campuseventmanagement.model.Person;
import sasha.org.campuseventmanagement.repo.PersonRepository;

import java.util.Optional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PersonRepository personRepository;

    public UserDetailsServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<Person> personOptional = personRepository.getPersonByUsername(username);

        if (personOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        Person person = personOptional.get();

        return User.builder()
                .username(person.getUsername())
                .password(person.getPassword()) // bcrypt-encoded in DB
                .authorities("ROLE_" + person.getRole()) // "ROLE_ADMIN", etc.
                .build();
    }
}

