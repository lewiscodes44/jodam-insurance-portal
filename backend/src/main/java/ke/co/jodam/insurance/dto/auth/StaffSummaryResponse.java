package ke.co.jodam.insurance.dto.auth;

public class StaffSummaryResponse {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String role;
    private boolean active;

    public StaffSummaryResponse() {}

    public StaffSummaryResponse(Long id, String username, String firstName, String lastName,
                                String email, String phoneNumber, String role, boolean active) {
        this.id = id; this.username = username; this.firstName = firstName; this.lastName = lastName;
        this.email = email; this.phoneNumber = phoneNumber; this.role = role; this.active = active;
    }
    public Long getId(){return id;} public String getUsername(){return username;} public String getFirstName(){return firstName;}
    public String getLastName(){return lastName;} public String getEmail(){return email;} public String getPhoneNumber(){return phoneNumber;}
    public String getRole(){return role;} public boolean isActive(){return active;}
}
