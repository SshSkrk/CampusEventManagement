package sasha.org.campuseventmanagement.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sasha.org.campuseventmanagement.model.Log;

import java.util.List;

@Repository
public interface LogRepository extends CrudRepository<Log, Integer> {
    @Override
    List<Log> findAll();
}
