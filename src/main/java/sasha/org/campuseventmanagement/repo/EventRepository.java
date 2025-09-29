package sasha.org.campuseventmanagement.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sasha.org.campuseventmanagement.dto.EventDTO;
import sasha.org.campuseventmanagement.model.Event;
import sasha.org.campuseventmanagement.model.Person;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends CrudRepository<Event, Integer> {
    Optional<Event> findEventById(Integer id);

    List<Event> findAll();

    List<Event> findAllByLocation(String location);

    List<Event> findAllByDate(LocalDate date);

    Optional<Event> findByTitle(String title);

    boolean existsByTitle(String title);
}