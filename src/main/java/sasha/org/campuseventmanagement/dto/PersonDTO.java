package sasha.org.campuseventmanagement.dto;

import lombok.Data;
import sasha.org.campuseventmanagement.model.Event;

import java.util.List;

@Data
public class PersonDTO {
    private Integer id;
    private String username;
    private String email;
    private boolean verified;
    private String password;
    private String role;
    private String firstName;
    private String lastName;
    private String course;
    private List<EventDTO> events;

}
