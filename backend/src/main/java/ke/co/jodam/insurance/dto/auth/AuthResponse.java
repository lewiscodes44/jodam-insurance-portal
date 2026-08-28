package ke.co.jodam.insurance.dto.auth;

public class AuthResponse {

    private String message;
    private String username;
    private String token;
    private String role;

    public AuthResponse() {
    }

    public AuthResponse(String message, String username) {
        this.message = message;
        this.username = username;
    }

    public AuthResponse(String message, String username, String token) { this(message, username, token, null); }

    public AuthResponse(String message, String username, String token, String role) {
        this.message = message; this.username = username; this.token = token; this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) { this.token = token; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}