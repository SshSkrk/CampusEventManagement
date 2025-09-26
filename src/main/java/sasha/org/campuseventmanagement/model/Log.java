package sasha.org.campuseventmanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import sasha.org.campuseventmanagement.dto.LogDTO;

import java.util.Date;

@Entity
@Data @NoArgsConstructor
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false)
    private Date date;

    public Log(String message, Date date) {
        this.message = message;
        this.date = date;
    }

    public Log(int id, String message, Date date) {
        this.id = id;
        this.message = message;
        this.date = date;
    }

    public static Log of(LogDTO logDTO) {
        Log log = new Log();
        log.setMessage(logDTO.getMessage());
        log.setDate(logDTO.getDate());
        return log;
    }

    public LogDTO toLogDTO() {
        LogDTO logDTO = new LogDTO();
        logDTO.setMessage(this.message);
        logDTO.setDate(this.date);
        return logDTO;
    }
}