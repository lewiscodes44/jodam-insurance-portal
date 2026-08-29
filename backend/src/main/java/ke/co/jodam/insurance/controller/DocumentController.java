package ke.co.jodam.insurance.controller;
import ke.co.jodam.insurance.dto.document.DocumentResponse;
import ke.co.jodam.insurance.entity.CustomerDocument;
import ke.co.jodam.insurance.service.DocumentService;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;
@RestController @RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentService service; public DocumentController(DocumentService service){this.service=service;}
    @PostMapping(value="/profile/{type}",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public ResponseEntity<DocumentResponse> profile(@PathVariable String type,@RequestParam("file") MultipartFile file,Authentication auth)throws IOException{return ResponseEntity.status(HttpStatus.CREATED).body(service.uploadProfile(type,file,auth.getName()));}
    @GetMapping("/profile") public List<DocumentResponse> profile(Authentication auth){return service.profile(auth.getName());}
    @GetMapping("/customers/{username}") public List<DocumentResponse> customerProfile(@PathVariable String username,Authentication auth){return service.customerProfile(username,auth.getName());}
    @PostMapping(value="/inquiries/{inquiryId}/{type}",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public ResponseEntity<DocumentResponse> inquiry(@PathVariable Long inquiryId,@PathVariable String type,@RequestParam("file") MultipartFile file,Authentication auth)throws IOException{return ResponseEntity.status(HttpStatus.CREATED).body(service.uploadInquiry(inquiryId,type,file,auth.getName()));}
    @GetMapping("/inquiries/{inquiryId}") public List<DocumentResponse> inquiry(@PathVariable Long inquiryId,Authentication auth){return service.inquiryDocuments(inquiryId,auth.getName());}
    @GetMapping("/{documentId}/download") public ResponseEntity<byte[]> download(@PathVariable Long documentId,Authentication auth){CustomerDocument d=service.download(documentId,auth.getName());return ResponseEntity.ok().contentType(MediaType.parseMediaType(d.getContentType())).header(HttpHeaders.CONTENT_DISPOSITION,"inline; filename=\""+d.getOriginalFilename().replace("\"","")+"\"").body(d.getContent());}
}
