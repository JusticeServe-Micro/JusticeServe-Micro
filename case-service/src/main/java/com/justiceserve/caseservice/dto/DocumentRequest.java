package com.justiceserve.caseservice.dto;

import com.justiceserve.caseservice.entity.CaseDocument;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DocumentRequest {
    @NotNull private CaseDocument.DocType docType;
    @NotBlank private String fileUri;
}
