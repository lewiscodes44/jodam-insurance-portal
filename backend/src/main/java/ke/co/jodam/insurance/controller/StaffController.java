package ke.co.jodam.insurance.controller;

import ke.co.jodam.insurance.dto.auth.AuthResponse;
import ke.co.jodam.insurance.dto.auth.CreateStaffRequest;
import ke.co.jodam.insurance.dto.auth.StaffSummaryResponse;
import java.util.List;
import ke.co.jodam.insurance.service.StaffService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping("/agents")
    public ResponseEntity<List<StaffSummaryResponse>> getAgents() {
        return ResponseEntity.ok(staffService.getAgents());
    }

    @PostMapping("/create")
    public ResponseEntity<AuthResponse> createStaff(
            @Valid @RequestBody CreateStaffRequest request
    ) {
        AuthResponse response = staffService.createStaff(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}