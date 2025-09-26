package sasha.org.campuseventmanagement.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sasha.org.campuseventmanagement.dto.LogDTO;
import sasha.org.campuseventmanagement.model.Log;
import sasha.org.campuseventmanagement.repo.LogRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LogService {
    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean createLog(LogDTO logDTO) {
        logRepository.save(Log.of(logDTO));
        return true;
    }

    @Transactional (readOnly = true)
    public List<LogDTO> getAllLogs() {
        List<Log> logs =  logRepository.findAll();
        if (logs.isEmpty()) {
            return new ArrayList<>();
        }
        List<LogDTO> logDTOS = new ArrayList<>();
        for (Log log : logs) {
            logDTOS.add(log.toLogDTO());
        }
        return logDTOS;
    }
}
