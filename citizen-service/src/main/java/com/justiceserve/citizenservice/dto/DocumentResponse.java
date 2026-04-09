package com.justiceserve.citizenservice.dto;
import com.justiceserve.citizenservice.entity.CitizenDocument;
import lombok.Data; import java.time.LocalDate;
@Data public class DocumentResponse {
    private Long documentId; private String docType; private String fileUri;
    private LocalDate uploadedDate; private String verificationStatus;
    public static DocumentResponse from(CitizenDocument d) {
        DocumentResponse r=new DocumentResponse();
        r.documentId=d.getDocumentId(); r.docType=d.getDocType().name();
        r.fileUri=d.getFileUri(); r.uploadedDate=d.getUploadedDate();
        r.verificationStatus=d.getVerificationStatus().name(); return r;
    }
}
