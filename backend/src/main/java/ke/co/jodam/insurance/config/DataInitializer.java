package ke.co.jodam.insurance.config;

import ke.co.jodam.insurance.entity.Role;
import ke.co.jodam.insurance.entity.User;
import ke.co.jodam.insurance.repository.RoleRepository;
import ke.co.jodam.insurance.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    DataInitializer.class
            );

    @Bean
    CommandLineRunner initializeAdmin(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            @Value("${bootstrap.admin.enabled:true}")
            boolean adminBootstrapEnabled,
            @Value("${bootstrap.admin.username:admin}")
            String adminUsername,
            @Value("${bootstrap.admin.email:admin@jodam.co.ke}")
            String adminEmail,
            @Value("${bootstrap.admin.password:}")
            String adminPassword,
            @Value("${bootstrap.admin.first-name:System}")
            String adminFirstName,
            @Value("${bootstrap.admin.last-name:Administrator}")
            String adminLastName,
            @Value("${bootstrap.admin.phone:0700000000}")
            String adminPhoneNumber
    ) {
        return args -> {

            if (!adminBootstrapEnabled) {

                LOGGER.info(
                        "Administrator bootstrap is disabled."
                );

                return;
            }

            if (adminPassword == null
                    || adminPassword.isBlank()) {

                throw new IllegalStateException(
                        "Administrator bootstrap is enabled, "
                                + "but BOOTSTRAP_ADMIN_PASSWORD is not configured."
                );
            }

            Role adminRole =
                    roleRepository
                            .findByName("ADMIN")
                            .orElseThrow(
                                    () -> new IllegalStateException(
                                            "ADMIN role not found. "
                                                    + "Run the security migration before "
                                                    + "starting administrator bootstrap."
                                    )
                            );

            User admin =
                    userRepository
                            .findByUsername(adminUsername)
                            .orElseGet(User::new);

            boolean existingAdmin =
                    admin.getId() != null;

            admin.setUsername(
                    adminUsername
            );

            admin.setEmail(
                    adminEmail
            );

            admin.setPasswordHash(
                    passwordEncoder.encode(
                            adminPassword
                    )
            );

            admin.setFirstName(
                    adminFirstName
            );

            admin.setLastName(
                    adminLastName
            );

            admin.setPhoneNumber(
                    adminPhoneNumber
            );

            admin.setActive(
                    true
            );

            admin.getRoles().clear();

            admin.getRoles().add(
                    adminRole
            );

            userRepository.save(
                    admin
            );

            if (existingAdmin) {

                LOGGER.info(
                        "Administrator account '{}' updated by bootstrap.",
                        adminUsername
                );

            } else {

                LOGGER.info(
                        "Administrator account '{}' created by bootstrap.",
                        adminUsername
                );
            }
        };
    }
}