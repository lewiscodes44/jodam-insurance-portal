package ke.co.jodam.insurance.service;

import ke.co.jodam.insurance.dto.auth.AuthResponse;
import ke.co.jodam.insurance.dto.auth.LoginRequest;
import ke.co.jodam.insurance.dto.auth.RegisterRequest;
import ke.co.jodam.insurance.dto.auth.CreateStaffRequest;
import ke.co.jodam.insurance.entity.Role;
import ke.co.jodam.insurance.entity.User;
import ke.co.jodam.insurance.repository.RoleRepository;
import ke.co.jodam.insurance.repository.UserRepository;
import ke.co.jodam.insurance.security.JwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final String CUSTOMER_ROLE = "CUSTOMER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already in use");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        /*
         * Public registration ALWAYS assigns CUSTOMER.
         *
         * A user cannot select AGENT or ADMIN during registration.
         * Those roles must be assigned by an authorized administrator
         * through the appropriate administrative functionality.
         */
        Role customerRole = roleRepository.findByName(CUSTOMER_ROLE)
                .orElseThrow(() ->
                        new IllegalStateException("Default CUSTOMER role has not been configured")
                );

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

        // Every publicly registered account starts as a CUSTOMER.
        user.getRoles().add(customerRole);

        userRepository.save(user);

        return new AuthResponse(
                "Registration successful",
                user.getUsername(),
                null,
                CUSTOMER_ROLE
        );
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new IllegalStateException("User not found")
                );

        org.springframework.security.core.userdetails.UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPasswordHash())
                        .authorities(
                                user.getRoles().stream()
                                        .flatMap(role -> role.getPermissions().stream())
                                        .map(permission -> permission.getName())
                                        .toArray(String[]::new)
                        )
                        .accountLocked(!user.isActive())
                        .build();

        String token = jwtService.generateToken(userDetails);

        String primaryRole = user.getRoles().stream()
                .map(Role::getName)
                .filter(name -> name.equals("ADMIN") || name.equals("AGENT") || name.equals("CUSTOMER"))
                .findFirst()
                .orElse("CUSTOMER");

        return new AuthResponse(
                "Login successful",
                user.getUsername(),
                token,
                primaryRole
        );
    }
}