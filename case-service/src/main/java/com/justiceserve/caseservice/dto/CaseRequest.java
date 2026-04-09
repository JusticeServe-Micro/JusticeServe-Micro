package com.justiceserve.caseservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CaseRequest {
    @NotNull(message = "citizenId is required")
    private Long citizenId;
    private Long lawyerId;
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
}
