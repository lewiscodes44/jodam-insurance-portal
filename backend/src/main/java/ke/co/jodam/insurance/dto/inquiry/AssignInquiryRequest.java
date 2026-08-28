package ke.co.jodam.insurance.dto.inquiry;

import jakarta.validation.constraints.NotBlank;

public class AssignInquiryRequest {

    @NotBlank(message = "Agent username is required")
    private String agentUsername;

    public AssignInquiryRequest() {
    }

    public String getAgentUsername() {
        return agentUsername;
    }

    public void setAgentUsername(String agentUsername) {
        this.agentUsername = agentUsername;
    }
}