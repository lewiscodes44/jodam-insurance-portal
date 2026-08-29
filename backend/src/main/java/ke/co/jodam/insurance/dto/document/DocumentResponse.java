package ke.co.jodam.insurance.dto.document;
import java.time.LocalDateTime;
public record DocumentResponse(Long id, String documentType, String filename, String contentType, Long inquiryId, LocalDateTime uploadedAt) {}
