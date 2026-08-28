package ke.co.jodam.insurance.service;

import ke.co.jodam.insurance.dto.auth.AuthResponse;
import ke.co.jodam.insurance.dto.auth.CreateStaffRequest;
import ke.co.jodam.insurance.dto.auth.StaffSummaryResponse;
import ke.co.jodam.insurance.entity.Role;
import ke.co.jodam.insurance.entity.User;
import ke.co.jodam.insurance.repository.RoleRepository;
import ke.co.jodam.insurance.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StaffService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public StaffService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse createStaff(CreateStaffRequest request) {

        // Get the currently authenticated user
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException(
                    "You must be logged in to create staff accounts"
            );
        }

        String currentUsername = authentication.getName();

        // Find the currently logged-in user
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user not found"
                        )
                );

        // Only an ADMIN can create staff accounts
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getName()));

        if (!isAdmin) {
            throw new IllegalStateException(
                    "Only an administrator can create staff accounts"
            );
        }

        // Only AGENT and ADMIN accounts may be created here
        String requestedRole = request.getRole().toUpperCase();

        if (!requestedRole.equals("AGENT")
                && !requestedRole.equals("ADMIN")) {

            throw new IllegalArgumentException(
                    "Staff role must be AGENT or ADMIN"
            );
        }

        // Check for duplicate username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(
                    "Username is already in use"
            );
        }

        // Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email is already in use"
            );
        }

        // Find the requested role
        Role role = roleRepository.findByName(requestedRole)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Role " + requestedRole
                                        + " has not been configured"
                        )
                );

        // Create the new user
        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setActive(true);

        // Assign the requested staff role
        user.getRoles().add(role);

        userRepository.save(user);

        return new AuthResponse(
                "Staff account created successfully",
                user.getUsername()
        );
    }
    @Transactional(readOnly = true)
    public java.util.List<StaffSummaryResponse> getAgents() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("You must be logged in");
        }
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()));
        if (!isAdmin) throw new IllegalStateException("Only an administrator can view agents");

        return userRepository.findAll().stream()
                .filter(User::isActive)
                .filter(user -> user.getRoles().stream().anyMatch(role -> "AGENT".equals(role.getName())))
                .map(user -> new StaffSummaryResponse(
                        user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(),
                        user.getEmail(), user.getPhoneNumber(), "AGENT", user.isActive()))
                .toList();
    }

}
