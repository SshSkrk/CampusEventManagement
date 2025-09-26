package sasha.org.campuseventmanagement.dto;

import lombok.Data;
import sasha.org.campuseventmanagement.model.Person;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class EventDTO {
    private Integer id;
    private String title;
    private String location;
    private LocalDate date;
    private List<PersonDTO> personList;
    private List<String> photoFilenames;
}
