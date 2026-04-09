package com.justiceserve.citizenservice.dto;
import com.justiceserve.citizenservice.entity.CitizenDocument;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data public class DocumentRequest {
    @NotNull private CitizenDocument.DocType docType;
    @NotBlank private String fileUri;
}
