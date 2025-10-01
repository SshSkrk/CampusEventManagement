package sasha.org.campuseventmanagement.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sasha.org.campuseventmanagement.model.Event;
import sasha.org.campuseventmanagement.model.Person;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

    Optional<Person> getPersonByUsername(String username);

    List<Person> findAllByCourse(String course);

    Optional<Person> getPersonById(Integer id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<Person> findByCourseContainingIgnoreCase(String course);

}
