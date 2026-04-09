package com.justiceserve.citizenservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CitizenRequest {
    @NotNull(message = "userId is required")
    private Long userId;

    @NotBlank(message = "Name is required")
    private String name;

    private LocalDate dob;
    private String gender;
    private String address;
    private String contactInfo;
}
