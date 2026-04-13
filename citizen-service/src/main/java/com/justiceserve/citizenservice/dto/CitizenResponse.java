package com.justiceserve.citizenservice.dto;

import com.justiceserve.citizenservice.entity.Citizen;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CitizenResponse {
    private Long citizenId;
    private Long userId;
    private String name;
    private LocalDate dob;
    private String gender;
    private String address;
    private String contactInfo;
    private String status;
    private LocalDateTime createdAt;

    public static CitizenResponse from(Citizen c) {
        CitizenResponse r = new CitizenResponse();
        r.citizenId = c.getCitizenId();
        r.userId = c.getUserId();          // plain Long — no local User entity
        r.name = c.getName();
        r.dob = c.getDob();
        r.gender = c.getGender();
        r.address = c.getAddress();
        r.contactInfo = c.getContactInfo();
        r.status = c.getStatus().name();
        r.createdAt = c.getCreatedAt();
        return r;
    }
}
