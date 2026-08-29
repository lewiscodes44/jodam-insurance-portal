package ke.co.jodam.insurance.service;

import ke.co.jodam.insurance.dto.document.DocumentResponse;
import ke.co.jodam.insurance.entity.*;
import ke.co.jodam.insurance.repository.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;

@Service
public class DocumentService {
    private static final Set<String> PROFILE_TYPES = Set.of("NATIONAL_ID", "KRA_PIN", "DRIVING_LICENCE");
    private static final Set<String> INQUIRY_TYPES = Set.of("LOGBOOK", "VALUATION_REPORT");
    private static final Set<String> CONTENT_TYPES = Set.of("application/pdf", "image/jpeg", "image/png");
    private final CustomerDocumentRepository documents; private final UserRepository users; private final InsuranceInquiryRepository inquiries;
    public DocumentService(CustomerDocumentRepository documents, UserRepository users, InsuranceInquiryRepository inquiries){this.documents=documents;this.users=users;this.inquiries=inquiries;}
    @Transactional public DocumentResponse uploadProfile(String type, MultipartFile file, String username) throws IOException { User customer=user(username); requireCustomer(customer); validate(type,file,PROFILE_TYPES); documents.findFirstByCustomerIdAndInquiryIsNullAndDocumentTypeOrderByUploadedAtDesc(customer.getId(),type).ifPresent(documents::delete); return response(save(customer,null,type,file)); }
    @Transactional public DocumentResponse uploadInquiry(Long inquiryId, String type, MultipartFile file, String username) throws IOException { User customer=user(username); requireCustomer(customer); InsuranceInquiry inquiry=inquiry(inquiryId); if(!inquiry.getCustomer().getId().equals(customer.getId())) throw new IllegalStateException("You are not authorized to upload documents for this inquiry"); validate(type,file,INQUIRY_TYPES); documents.findFirstByInquiryIdAndDocumentTypeOrderByUploadedAtDesc(inquiryId,type).ifPresent(documents::delete); return response(save(customer,inquiry,type,file)); }
    @Transactional(readOnly=true) public List<DocumentResponse> profile(String username){ User customer=user(username); requireCustomer(customer); return documents.findByCustomerIdAndInquiryIsNullOrderByUploadedAtDesc(customer.getId()).stream().map(this::response).toList(); }
    @Transactional(readOnly=true) public List<DocumentResponse> customerProfile(String customerUsername,String staffUsername){ User staff=user(staffUsername); if(!role(staff,"ADMIN")&&!role(staff,"AGENT")) throw new IllegalStateException("Only staff can view customer profile documents"); User customer=user(customerUsername); return documents.findByCustomerIdAndInquiryIsNullOrderByUploadedAtDesc(customer.getId()).stream().map(this::response).toList(); }
    @Transactional(readOnly=true) public List<DocumentResponse> inquiryDocuments(Long inquiryId,String username){ InsuranceInquiry inquiry=inquiry(inquiryId); ensureAccess(inquiry,user(username)); return documents.findByInquiryIdOrderByUploadedAtDesc(inquiryId).stream().map(this::response).toList(); }
    @Transactional(readOnly=true) public CustomerDocument download(Long documentId,String username){ CustomerDocument document=documents.findById(documentId).orElseThrow(()->new IllegalArgumentException("Document not found")); User user=user(username); if(document.getInquiry()!=null) ensureAccess(document.getInquiry(),user); else if(!document.getCustomer().getId().equals(user.getId())&&!role(user,"ADMIN")) throw new IllegalStateException("You are not authorized to view this profile document"); return document; }
    private CustomerDocument save(User customer,InsuranceInquiry inquiry,String type,MultipartFile file)throws IOException{ CustomerDocument document=new CustomerDocument();document.setCustomer(customer);document.setInquiry(inquiry);document.setDocumentType(type);document.setOriginalFilename(file.getOriginalFilename()==null?type:file.getOriginalFilename());document.setContentType(file.getContentType());document.setContent(file.getBytes());return documents.save(document); }
    private void validate(String type,MultipartFile file,Set<String> allowed){if(!allowed.contains(type))throw new IllegalArgumentException("Unsupported document type");if(file.isEmpty())throw new IllegalArgumentException("Choose a document to upload");if(file.getSize()>5_000_000)throw new IllegalArgumentException("Documents must be 5 MB or smaller");if(!CONTENT_TYPES.contains(file.getContentType()))throw new IllegalArgumentException("Use a PDF, JPEG or PNG document");}
    private void ensureAccess(InsuranceInquiry inquiry,User user){if(role(user,"ADMIN")||role(user,"AGENT")||inquiry.getCustomer().getId().equals(user.getId()))return;throw new IllegalStateException("You are not authorized to view this inquiry document");}
    private User user(String username){return users.findByUsername(username).orElseThrow(()->new IllegalStateException("Authenticated user not found"));} private InsuranceInquiry inquiry(Long id){return inquiries.findById(id).orElseThrow(()->new IllegalArgumentException("Insurance inquiry not found"));} private void requireCustomer(User user){if(!role(user,"CUSTOMER"))throw new IllegalStateException("Only customers can upload documents");} private boolean role(User user,String name){return user.getRoles().stream().anyMatch(r->name.equals(r.getName()));} private DocumentResponse response(CustomerDocument d){return new DocumentResponse(d.getId(),d.getDocumentType(),d.getOriginalFilename(),d.getContentType(),d.getInquiry()==null?null:d.getInquiry().getId(),d.getUploadedAt());}
}
