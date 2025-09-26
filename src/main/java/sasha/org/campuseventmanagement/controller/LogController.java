package sasha.org.campuseventmanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sasha.org.campuseventmanagement.dto.LogDTO;
import sasha.org.campuseventmanagement.service.LogService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LogController {
    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    //ADMIN
    @GetMapping("/admin/getAllLogs")
    public List<LogDTO> getAllLogs() {
        List<LogDTO> logDTOS = logService.getAllLogs();
        return logDTOS;  // Return list of logs
    }



}
